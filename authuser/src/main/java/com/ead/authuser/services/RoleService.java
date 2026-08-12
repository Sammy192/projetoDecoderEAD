package com.ead.authuser.services;

import com.ead.authuser.enums.UserTypeEnum;
import com.ead.authuser.models.RoleModel;
import org.springframework.stereotype.Service;

@Service
public interface RoleService {

    RoleModel findByRole(UserTypeEnum role);
}
