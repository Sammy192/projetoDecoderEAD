package com.ead.authuser.repositories;

import com.ead.authuser.enums.UserTypeEnum;
import com.ead.authuser.models.RoleModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<RoleModel, UUID> {

    Optional<RoleModel> findByRole(UserTypeEnum role);
}
