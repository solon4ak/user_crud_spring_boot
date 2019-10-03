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
    public User createOrUpdateUser(User user) {
        if (user.getId() == null) {
            return userRepository.save(user);
        } else {
            Optional<User> userToUpdate = userRepository.findById(user.getId());
            if (userToUpdate.isPresent()) {
                User aUser = userToUpdate.get();
                aUser.setPassword(user.getPassword());
                aUser.setFirstName(user.getFirstName());
                aUser.setLastName(user.getLastName());
                aUser.setEmail(user.getEmail());
                aUser.setAddress(user.getAddress());
                aUser.setBirthDate(user.getBirthDate());
                aUser.setPhoneNumber(user.getPhoneNumber());
                aUser.setRoles(user.getRoles());

                return userRepository.save(aUser);
            } else {
                return userRepository.save(user);
            }
        }
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
    public void deleteUserById(Long id) throws RecordNotFoundException {

        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            userRepository.delete(user.get());
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
