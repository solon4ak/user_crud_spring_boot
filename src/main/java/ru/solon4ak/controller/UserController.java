package ru.solon4ak.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.solon4ak.exceptions.RecordNotFoundException;
import ru.solon4ak.model.User;
import ru.solon4ak.service.UserService;

import java.security.Principal;
import java.util.Map;

@Controller
@RequestMapping("user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("view")
    public String viewUser(Map<String, Object> model, Principal principal) throws RecordNotFoundException {
        User user = userService.findByName(principal.getName());
        model.put("user", user);
        return "view_user";
    }
}
