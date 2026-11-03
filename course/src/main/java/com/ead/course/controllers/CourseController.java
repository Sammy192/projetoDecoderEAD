package com.ead.course.controllers;

import com.ead.course.dto.CourseDTO;
import com.ead.course.dto.CourseResponseDTO;
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
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    @PostMapping
    public ResponseEntity<CourseResponseDTO> saveCourse(@RequestBody @Valid CourseDTO courseDto) {
        CourseModel courseModel = courseService.saveCourse(courseDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CourseResponseDTO(courseModel));
    }

    @PreAuthorize("hasAnyRole('STUDENT')")
    @GetMapping
    public ResponseEntity<Page<CourseResponseDTO>> getAllcourses(SpecificationTemplate.CourseSpec spec,
                                                           Pageable pageable,
                                                           @RequestParam(required = false) UUID userId) {
        Page<CourseModel> courseModels = userId != null ? courseService.findAll(SpecificationTemplate.courseUserId(userId).and(spec), pageable) : courseService.findAll(spec, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(courseModels.map(CourseResponseDTO::new));
    }

    @PreAuthorize("hasAnyRole('STUDENT')")
    @GetMapping("/{courseId}")
    public ResponseEntity<CourseResponseDTO> getOneCourse(@PathVariable(value = "courseId") UUID courseId) {
        CourseModel courseModel = courseService.findById(courseId);
        return ResponseEntity.status(HttpStatus.OK).body(new CourseResponseDTO(courseModel));
    }

    @PreAuthorize("hasAnyRole('INSTRUCTOR')")
    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> deleteCourse(@PathVariable(value = "courseId") UUID courseId) {
        logger.debug("DELETE deleteCourse courseId received {} ", courseId);
        courseService.deleteCourse(courseId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('INSTRUCTOR')")
    @PutMapping("/{courseId}")
    public ResponseEntity<CourseResponseDTO> updateCourse(@PathVariable(value = "courseId") UUID courseId,
                                               @RequestBody @Valid CourseDTO courseDto) {
        logger.debug("PUT updateCourse courseId received {} with courseDto {} ", courseId, courseDto);
        CourseModel course = courseService.updateCourse(courseId, courseDto);

        return ResponseEntity.status(HttpStatus.OK).body(new CourseResponseDTO(course));
    }


}
