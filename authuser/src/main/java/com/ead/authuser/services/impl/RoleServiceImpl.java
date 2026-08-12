package com.ead.authuser.services.impl;

import com.ead.authuser.configs.exceptions.NotFoundException;
import com.ead.authuser.enums.UserTypeEnum;
import com.ead.authuser.models.RoleModel;
import com.ead.authuser.repositories.RoleRepository;
import com.ead.authuser.services.RoleService;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public RoleModel findByRole(UserTypeEnum role) {
        return roleRepository.findByRole(role)
                .orElseThrow(() -> new NotFoundException("Error: Role not found"));
    }
}
