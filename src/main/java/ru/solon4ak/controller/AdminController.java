package ru.solon4ak.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.solon4ak.exceptions.RecordNotFoundException;
import ru.solon4ak.model.User;
import ru.solon4ak.model.Views;
import ru.solon4ak.service.UserService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("admin/user")
public class AdminController {

    private final UserService userService;
    private final PasswordEncoder encoder;


    @Autowired
    public AdminController(UserService userService, PasswordEncoder encoder) {
        this.userService = userService;
        this.encoder = encoder;
    }

    @GetMapping
    @JsonView(Views.IgnorePassword.class)
    public List<User> getAllUsers(Map<String, Object> model) {
        return userService.listUsers();
    }

    @PostMapping
    @JsonView(Views.IgnorePassword.class)
    public User createUser(@RequestBody User user) {
        String password = user.getPassword();
        user.setPassword(encoder.encode(password));
        return userService.create(user);
    }

    @PutMapping("{id}")
    @JsonView(Views.IgnorePassword.class)
    public User updateUser(
            @PathVariable("id") User userToUpdate,
            @RequestBody User user) {
        BeanUtils.copyProperties(user, userToUpdate, "id", "password");
        String password = user.getPassword();
        userToUpdate.setPassword(encoder.encode(password));
        return userService.create(userToUpdate);
    }

    @DeleteMapping("{id}")
    @JsonView(Views.IgnorePassword.class)
    public void deleteEmployeeById(@PathVariable("id") User user)
            throws RecordNotFoundException {
        userService.deleteUser(user);
    }

    @GetMapping("{id}")
    @JsonView(Views.IgnorePassword.class)
    public User viewUserById(@PathVariable("id") User user) {
        return user;
    }

}
