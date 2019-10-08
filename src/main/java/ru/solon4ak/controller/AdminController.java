package ru.solon4ak.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.solon4ak.exceptions.RecordNotFoundException;
import ru.solon4ak.model.User;
import ru.solon4ak.service.RoleService;
import ru.solon4ak.service.UserService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("admin/user")
public class AdminController {

    private final UserService userService;

//    private final RoleService roleService;

    private final PasswordEncoder encoder;

//    @Autowired
//    public AdminController(UserService userService, RoleService roleService, PasswordEncoder encoder) {
//        this.userService = userService;
//        this.roleService = roleService;
//        this.encoder = encoder;
//    }

    @Autowired
    public AdminController(UserService userService, PasswordEncoder encoder) {
        this.userService = userService;
        this.encoder = encoder;
    }

    @GetMapping
    public List<User> getAllUsers(Map<String, Object> model) {
        return userService.listUsers();
    }

//    @GetMapping({"edit", "edit/{id}"})
//    public String editUserById(Map<String, Object> model, @PathVariable("id") Optional<Long> id)
//            throws RecordNotFoundException {
//        if (id.isPresent()) {
//            User user = userService.findUserById(id.get());
//            model.put("user", user);
//        } else {
//            model.put("user", new User());
//        }
//        model.put("user_roles", roleService.getAllRoles());
//        return "add_edit_user";
//    }

    @PostMapping
    public List<User> createUser(@RequestBody User user) {
        String password = user.getPassword();
        user.setPassword(encoder.encode(password));
        userService.create(user);
        return userService.listUsers();
    }

    @PutMapping
    public List<User> updateUser(@RequestBody User user)
            throws RecordNotFoundException {
        String password = user.getPassword();
        user.setPassword(encoder.encode(password));
        userService.update(user);
        return userService.listUsers();
    }

    @DeleteMapping("{id}")
    public List<User> deleteEmployeeById(@PathVariable Long id)
            throws RecordNotFoundException {
        userService.deleteUserById(id);
        return userService.listUsers();
    }

    @GetMapping("{id}")
    public User viewUserById(@PathVariable Long id)
            throws RecordNotFoundException {
        return userService.findUserById(id);
    }

}
