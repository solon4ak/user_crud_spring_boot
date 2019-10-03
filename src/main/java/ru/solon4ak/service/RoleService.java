package ru.solon4ak.service;

import ru.solon4ak.exceptions.RecordNotFoundException;
import ru.solon4ak.model.Role;

import java.util.List;

public interface RoleService {

    List<Role> getAllRoles();
    List<String> getAllRolesNames();
    Role createOrUpdateRole(Role role);
    void deleteRoleById(Long id) throws RecordNotFoundException;
    Role getRoleByName(String roleName) throws RecordNotFoundException;
}
