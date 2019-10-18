package ru.solon4ak.repository;

import org.springframework.data.repository.CrudRepository;
import ru.solon4ak.model.User;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findUserByUsername(String username);

    Optional<User> findUserByEmail(String email);
}
