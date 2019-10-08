package ru.solon4ak.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.solon4ak.exceptions.RecordNotFoundException;
import ru.solon4ak.model.Role;
import ru.solon4ak.model.User;
import ru.solon4ak.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> listUsers() {
        List<User> users = (List<User>) userRepository.findAll();
        if (!users.isEmpty()) {
            return users;
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public User update(User user) throws RecordNotFoundException {
        Optional<User> userToUpdate = userRepository.findById(user.getId());
        if (userToUpdate.isPresent()) {
            User aUser = userToUpdate.get();
            return userRepository.save(aUser);
        } else {
            throw new RecordNotFoundException("Can't find user with specified id");
        }
    }

    @Override
    public User create(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User findUserById(Long id) throws RecordNotFoundException {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new RecordNotFoundException("No user record exist for given id");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public User findByName(String username) throws RecordNotFoundException {
        Optional<User> user = userRepository.findUserByUsername(username);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new RecordNotFoundException("No user record for given username");
        }
    }

    @Override
    public void deleteUser(User user) throws RecordNotFoundException {

        Optional<User> aUser = userRepository.findById(user.getId());
        if (aUser.isPresent()) {
            userRepository.delete(aUser.get());
        } else {
            throw new RecordNotFoundException("No user record exist for given id");
        }
    }

    @Override
    public String[] getUserRoles(User user) {
        String[] roles = new String[user.getRoles().size()];
        int i = 0;
        for (Role role : user.getRoles()) {
            roles[i] = role.getName();
            i++;
        }
        return roles;
    }
}
