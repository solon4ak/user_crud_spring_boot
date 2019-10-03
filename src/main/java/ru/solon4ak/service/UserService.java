package ru.solon4ak.service;

import ru.solon4ak.exceptions.RecordNotFoundException;
import ru.solon4ak.model.User;

import java.util.List;

public interface UserService {

    List<User> listUsers();
    User createOrUpdateUser(User user);
    User findUserById(Long id) throws RecordNotFoundException;
    User findByName(String username) throws RecordNotFoundException;
    void deleteUserById(Long id) throws RecordNotFoundException;
    String[] getUserRoles(User user);
}
