package ru.solon4ak.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;
import ru.solon4ak.exceptions.RecordNotFoundException;
import ru.solon4ak.model.Role;
import ru.solon4ak.model.User;
import ru.solon4ak.service.RoleService;
import ru.solon4ak.service.UserService;

import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    public String createOrUpdateUser(User user) {
        String password = user.getPassword();
        user.setPassword(encoder.encode(password));
        userService.createOrUpdateUser(user);
        return "redirect:/admin/list";
    }

    @GetMapping("delete/{id}")
    public String deleteEmployeeById(@PathVariable("id") Long id)
            throws RecordNotFoundException {
        userService.deleteUserById(id);
        return "redirect:/admin/list";
    }

    @GetMapping("view/{userId}")
    public String viewUserById(Map<String, Object> model,
                               @PathVariable long userId) throws RecordNotFoundException {
        User user = userService.findUserById(userId);
        model.put("user", user);
        return "view_user";
    }

}
