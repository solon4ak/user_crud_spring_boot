package ru.solon4ak.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.solon4ak.model.Role;
import ru.solon4ak.model.User;

import javax.annotation.PostConstruct;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

@Service
public class DBInit {

    private final UserService userService;

    private final RoleService roleService;

    private final PasswordEncoder encoder;

    @Autowired
    public DBInit(UserService userService, RoleService roleService, PasswordEncoder encoder) {
        this.userService = userService;
        this.roleService = roleService;
        this.encoder = encoder;
    }

    @PostConstruct
    public void initDB() {
        Role adminRole =  new Role("ADMIN");
        roleService.createOrUpdateRole(adminRole);
        Role userRole = new Role("USER");
        roleService.createOrUpdateRole(userRole);

        User user = new User(
                "Marcy",
                "Gray",
                "marcy@",
                "",
                "",
                new Date(new GregorianCalendar(1977, Calendar.OCTOBER, 6).getTime().getTime()),
                "mc",
                encoder.encode("marcy")
        );
        user.addRole(adminRole);
        userService.createOrUpdateUser(user);

        user = new User(
                "Henry",
                "Moore",
                "henry@",
                "",
                "",
                new Date(new GregorianCalendar(1998, Calendar.JUNE, 25).getTime().getTime()),
                "henry",
                encoder.encode("henry")
        );
        user.addRole(userRole);
        userService.createOrUpdateUser(user);

        user = new User(
                "Antony",
                "Goose",
                "tony@",
                "",
                "",
                new Date(new GregorianCalendar(1998, Calendar.JUNE, 25).getTime().getTime()),
                "ant",
                encoder.encode("ant")
        );
        user.addRole(userRole);
        userService.createOrUpdateUser(user);
    }
}
