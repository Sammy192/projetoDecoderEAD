package com.ead.course.services;

import com.ead.course.dto.UserEventDTO;
import com.ead.course.models.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public interface UserService {
    Page<UserModel> getAllUsersByCourse(Specification<UserModel> spec, Pageable pageable, UUID courseId);

    UserModel saveUser(UserEventDTO userEventDTO);

    void deleteUserById(UUID userId);

    UserModel findById(UUID userId);
}
