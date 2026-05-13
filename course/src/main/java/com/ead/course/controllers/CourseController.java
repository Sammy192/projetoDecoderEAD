package com.ead.course.controllers;

import com.ead.course.dto.CourseDTO;
import com.ead.course.models.CourseModel;
import com.ead.course.services.CourseService;
import com.ead.course.specifications.SpecificationTemplate;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/courses")
public class CourseController {

    Logger logger = LogManager.getLogger(CourseController.class);

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping
    public ResponseEntity<Object> saveCourse(@RequestBody @Valid CourseDTO courseDto) {
        logger.debug("POST saveCourse courseDto received {} ", courseDto);
        if(courseService.existsByName(courseDto.name())) {
            logger.warn("Error: Course Name already exists. {}", courseDto.name());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: Course Name already exists.");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.saveCourse(courseDto));
    }

    @GetMapping
    public ResponseEntity<Page<CourseModel>> getAllcourses(SpecificationTemplate.CourseSpec spec,
                                                           Pageable pageable,
                                                           @RequestParam(required = false) UUID userId) {
        Page<CourseModel> courseModels = userId != null ? courseService.findAll(SpecificationTemplate.courseUserId(userId).and(spec), pageable) : courseService.findAll(spec, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(courseModels);
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<Object> getOneCourse(@PathVariable(value = "courseId") UUID courseId) {
        return ResponseEntity.status(HttpStatus.OK).body(courseService.findById(courseId));
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<Object> deleteCourse(@PathVariable(value = "courseId") UUID courseId) {
        logger.debug("DELETE deleteCourse courseId received {} ", courseId);
        courseService.deleteCourse(courseId);
        return ResponseEntity.status(HttpStatus.OK).body("Course deleted successfully.");
    }

    @PutMapping("/{courseId}")
    public ResponseEntity<Object> updateCourse(@PathVariable(value = "courseId") UUID courseId,
                                               @RequestBody @Valid CourseDTO courseDto) {
        logger.debug("PUT updateCourse courseId received {} with courseDto {} ", courseId, courseDto);
        CourseModel course = courseService.updateCourse(courseId, courseDto);

        return ResponseEntity.status(HttpStatus.OK).body(course);
    }


}
