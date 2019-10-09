package ru.solon4ak.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.solon4ak.exceptions.RecordNotFoundException;
import ru.solon4ak.model.User;
import ru.solon4ak.service.RoleService;
import ru.solon4ak.service.UserService;

import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("admin")
public class AdminController {

    private final UserService userService;

    private final RoleService roleService;

    private final PasswordEncoder encoder;

    @Autowired
    public AdminController(UserService userService, RoleService roleService, PasswordEncoder encoder) {
        this.userService = userService;
        this.roleService = roleService;
        this.encoder = encoder;
    }

    @GetMapping("list")
    public String getAllUsers(Map<String, Object> model) {
        model.put("users", userService.listUsers());
        return "list_users";
    }

    @GetMapping({"edit", "edit/{id}"})
    public String editUserById(Map<String, Object> model, @PathVariable("id") Optional<Long> id)
            throws RecordNotFoundException {
        if (id.isPresent()) {
            User user = userService.findUserById(id.get());
            model.put("user", user);
        } else {
            model.put("user", new User());
        }
        model.put("user_roles", roleService.getAllRoles());
        return "add_edit_user";
    }

    @PostMapping("add")
    public String createOrUpdateUser(@RequestParam User id, User user)
            throws RecordNotFoundException {
        String password = user.getPassword();
        if (id == null) {
            user.setPassword(encoder.encode(password));
            userService.create(user);
        } else {
            BeanUtils.copyProperties(user, id, "id", "password");
            id.setPassword(encoder.encode(password));
            userService.update(user);
        }
        return "redirect:/admin/list";
    }

    @GetMapping("delete/{id}")
    public String deleteEmployeeById(@PathVariable("id") User user)
            throws RecordNotFoundException {
        userService.deleteUser(user);
        return "redirect:/admin/list";
    }

    @GetMapping("view/{id}")
    public String viewUserById(Map<String, Object> model,
                               @PathVariable("id") User user) {
        model.put("user", user);
        return "view_user";
    }

}
