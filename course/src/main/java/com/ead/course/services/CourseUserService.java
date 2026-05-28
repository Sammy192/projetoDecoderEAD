package com.ead.course.services;

import com.ead.course.dto.UserDTO;
import com.ead.course.models.CourseUserModel;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CourseUserService {
    CourseUserModel saveSubscriptionUserInCourse(UUID courseId, UUID uuid);
    Page<UserDTO> getAllUsersByCourse(UUID courseId, Pageable pageable);

    void deleteCourseUserByUserId(UUID userId);
}
