package com.ead.course.services;

import com.ead.course.dto.CourseDTO;
import com.ead.course.models.CourseModel;

import java.util.List;
import java.util.UUID;

public interface CourseService {
    void deleteCourse(UUID courseId);
    CourseModel findById(UUID courseId);

    CourseModel saveCourse(CourseDTO courseDto);

    boolean existsByName(String name);

    List<CourseModel> findAll();

    CourseModel updateCourse(UUID courseId, CourseDTO courseDto);
}