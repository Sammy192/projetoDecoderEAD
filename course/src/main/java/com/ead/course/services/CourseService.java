package com.ead.course.services;

import com.ead.course.dto.CourseDTO;
import com.ead.course.models.CourseModel;

import java.util.UUID;

public interface CourseService {
    void deleteCourse(CourseModel course);
    CourseModel findById(UUID courseId);

    CourseModel saveCourse(CourseDTO courseDto);

    boolean existsByName(String name);
}