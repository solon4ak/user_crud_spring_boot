package ru.solon4ak.service;

import org.springframework.transaction.annotation.Transactional;
import ru.solon4ak.exceptions.RecordNotFoundException;
import ru.solon4ak.model.User;

import java.util.List;

public interface UserService {

    List<User> listUsers();
    User create(User user);
    User update(User user) throws RecordNotFoundException;
    User findUserById(Long id) throws RecordNotFoundException;
    User findByName(String username) throws RecordNotFoundException;

    User findByEmail(String email) throws RecordNotFoundException;

    void deleteUser(User user) throws RecordNotFoundException;
    String[] getUserRoles(User user);

}
