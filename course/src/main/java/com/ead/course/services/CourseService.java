package com.ead.course.services;

import com.ead.course.dto.CourseDTO;
import com.ead.course.models.CourseModel;
import com.ead.course.specifications.SpecificationTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.UUID;

public interface CourseService {
    void deleteCourse(UUID courseId);
    CourseModel findById(UUID courseId);

    CourseModel saveCourse(CourseDTO courseDto);

    boolean existsByName(String name);

    Page<CourseModel> findAll(Specification<CourseModel> spec, Pageable pageable);

    CourseModel updateCourse(UUID courseId, CourseDTO courseDto);
}