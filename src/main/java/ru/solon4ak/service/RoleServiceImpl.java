package ru.solon4ak.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.solon4ak.exceptions.RecordNotFoundException;
import ru.solon4ak.model.Role;
import ru.solon4ak.repository.RoleRepository;

import javax.management.relation.RoleNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> getAllRoles() {
        List<Role> roles = (List<Role>) roleRepository.findAll();
        if (!roles.isEmpty()) {
            return roles;
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public List<String> getAllRolesNames() {
        List<String> roleNames = new ArrayList<>();
        for (Role role : this.getAllRoles()) {
            roleNames.add(role.getName());
        }
        return roleNames;
    }

    @Override
    public Role createOrUpdateRole(Role role) {
        if (role.getId() == null) {
            return roleRepository.save(role);
        } else {
            Optional<Role> byId = roleRepository.findById(role.getId());
            if (byId.isPresent()) {
                Role aRole = byId.get();
                aRole.setName(role.getName());
                return roleRepository.save(aRole);
            } else {
                return roleRepository.save(role);
            }
        }
    }

    @Override
    public void deleteRoleById(Long id) throws RecordNotFoundException {
        Optional<Role> role = roleRepository.findById(id);
        if (role.isPresent()) {
            roleRepository.deleteById(id);
        } else {
            throw new RecordNotFoundException("No role record exist for given id");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Role getRoleByName(String roleName) throws RecordNotFoundException {
        Optional<Role> role = roleRepository.findRoleByName(roleName);
        if (role.isPresent()) {
            return role.get();
        } else {
            throw new RecordNotFoundException("No role record exist for given role name");
        }
    }
}
