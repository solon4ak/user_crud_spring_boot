package ru.solon4ak.repository;

import org.springframework.data.repository.CrudRepository;
import ru.solon4ak.model.Role;

import java.util.Optional;

public interface RoleRepository extends CrudRepository<Role, Long> {

    Optional<Role> findRoleByName(String name);
}
