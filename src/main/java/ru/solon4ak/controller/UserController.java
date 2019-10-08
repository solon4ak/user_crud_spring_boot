package ru.solon4ak.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.solon4ak.exceptions.RecordNotFoundException;
import ru.solon4ak.model.User;
import ru.solon4ak.model.Views;
import ru.solon4ak.service.UserService;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("user/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @JsonView(Views.IgnorePassword.class)
    public User viewUser(Principal principal) throws RecordNotFoundException {
        return userService.findByName(principal.getName());
    }
}
