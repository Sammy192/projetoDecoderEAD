package com.ead.course.controllers;

import com.ead.course.configs.exceptions.NotFoundException;
import com.ead.course.dto.SubscriptionDTO;
import com.ead.course.services.CourseService;
import com.ead.course.services.UserService;
import com.ead.course.specifications.SpecificationTemplate;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class CourseUserController {

    private final CourseService courseService;
    private final UserService userService;

    public CourseUserController(CourseService courseService, UserService userService) {
        this.courseService = courseService;
        this.userService = userService;
    }

    @PreAuthorize("hasAnyRole('INSTRUCTOR')")
    @GetMapping("/courses/{courseId}/users")
    public ResponseEntity<Object> getAllUsersByCourse(SpecificationTemplate.UserSpec spec,
                                                      @PathVariable(value = "courseId") UUID courseId,
                                                      @PageableDefault(page = 0, size = 10, sort = "userId", direction = Sort.Direction.ASC) Pageable pageable) {
        if(!courseService.existsByCourseId(courseId)) throw new NotFoundException("Course not found.");
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.getAllUsersByCourse(SpecificationTemplate.userCourseId(courseId).and(spec), pageable));
    }

    @PreAuthorize("hasAnyRole('USER')")
    @PostMapping("/courses/{courseId}/users/subscription")
    public ResponseEntity<Object> saveSubscriptionUserInCourse(@PathVariable(value = "courseId") UUID courseId,
                                                               @RequestBody @Valid SubscriptionDTO subscriptionDTO) {
        courseService.saveSubscriptionUserInCourse(courseId, subscriptionDTO.userId());
        return ResponseEntity.status(HttpStatus.CREATED).body("Subscription created successfully.");
    }
}
