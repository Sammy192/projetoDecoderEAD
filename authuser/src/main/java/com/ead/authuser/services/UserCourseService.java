package com.ead.authuser.services;

import com.ead.authuser.dto.CourseDTO;
import com.ead.authuser.models.UserCourseModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserCourseService {
    UserCourseModel saveSubscriptionUserInCourse(UUID userId, UUID courseId);
    Page<CourseDTO> getAllCoursesByUser(UUID userId, Pageable pageable);
}
