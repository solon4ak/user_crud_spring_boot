package ru.solon4ak.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.solon4ak.exceptions.RecordNotFoundException;
import ru.solon4ak.model.User;
import ru.solon4ak.service.UserService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("rest")
public class RestApiController {
    private final UserService userService;
    private final PasswordEncoder encoder;


    @Autowired
    public RestApiController(UserService userService, PasswordEncoder encoder) {
        this.userService = userService;
        this.encoder = encoder;
    }

    @GetMapping
    public List<User> getAllUsers(Map<String, Object> model) {
        return userService.listUsers();
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        String password = user.getPassword();
        user.setPassword(encoder.encode(password));
        return userService.create(user);
    }

    @PutMapping("{id}")
    public User updateUser(
            @PathVariable("id") User userToUpdate,
            @RequestBody User user) {
        BeanUtils.copyProperties(user, userToUpdate, "id", "password");
        String password = user.getPassword();
        userToUpdate.setPassword(encoder.encode(password));
        return userService.create(userToUpdate);
    }

    @DeleteMapping("{id}")
    public void deleteEmployeeById(@PathVariable("id") User user)
            throws RecordNotFoundException {
        userService.deleteUser(user);
    }

    @GetMapping("{id}")
    public User viewUserById(@PathVariable("id") User user) {
        return user;
    }
}
