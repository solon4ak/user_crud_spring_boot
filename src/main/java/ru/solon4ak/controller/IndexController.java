package ru.solon4ak.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.solon4ak.exceptions.RecordNotFoundException;
import ru.solon4ak.model.User;
import ru.solon4ak.service.RoleService;
import ru.solon4ak.service.UserService;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
public class IndexController {

    private Logger log = Logger.getLogger(IndexController.class.getName());

    private UserService userService;
    private RoleService roleService;

    @Autowired
    public IndexController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/")
    public String index(Model model, Principal principal) {
        if (principal != null) {
            OAuth2Authentication auth = (OAuth2Authentication) principal;
            Map<String, Object> map =
                    (LinkedHashMap<String, Object>) auth.getUserAuthentication().getDetails();
            String email = (String) map.get("email");
            try {
                User user = userService.findByName(email);
                if (user.getRoles().contains(roleService.getAllRoles().get(1))) {
                    model.addAttribute("users", userService.listUsers());
                    return "list_users";
                } else {
                    model.addAttribute("user", user);
                    return "view_user";
                }
            } catch (RecordNotFoundException e) {
                log.log(Level.WARNING, "Authentication exception. Can't find user.", e);

                User user = new User();
                user.setEmail((String) map.get("email"));
                user.setFirstName((String) map.get("given_name"));
                user.setLastName((String) map.get("family_name"));
                user.setUsername((String) map.get("name"));

                user.addRole(roleService.getAllRoles().get(1));
                userService.create(user);
                model.addAttribute("user", user);
            }
            return "view_user";
        }
        return "redirect:/login";
    }
}
