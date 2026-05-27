package com.ead.authuser.controllers;

import com.ead.authuser.clients.CourseClient;
import com.ead.authuser.dto.CourseDTO;
import com.ead.authuser.dto.SubscriptionDTO;
import com.ead.authuser.services.UserCourseService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.ead.authuser.models.UserCourseModel;

@RestController
public class UserCourseController {

    private final CourseClient courseClient;
    private final UserCourseService userCourseService;

    public UserCourseController(CourseClient courseClient, UserCourseService userCourseService) {
        this.courseClient = courseClient;
        this.userCourseService = userCourseService;
    }

    @GetMapping("/users/{userId}/courses")
    public ResponseEntity<Page<CourseDTO>> getAllCoursesByUser(@PathVariable(value = "userId") UUID userId,
                                                               @PageableDefault(page = 0, size = 10, sort = "courseId", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(userCourseService.getAllCoursesByUser(userId, pageable));
    }

    @PostMapping("/users/{userId}/courses/subscription")
    public ResponseEntity<Object> saveSubscriptionUserInCourse(@PathVariable(value = "userId") UUID userId,
                                                               @RequestBody @Valid SubscriptionDTO subscriptionDTO) {
        UserCourseModel userCourseModel = userCourseService.saveSubscriptionUserInCourse(userId, subscriptionDTO.courseId());

        if (userCourseModel.getUser() != null) {
            userCourseModel.getUser().add(linkTo(methodOn(UserController.class)
                    .getOneUser(userId)).withRel("user"));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(userCourseModel);
    }

    @DeleteMapping("/users/courses/{courseId}")
    public ResponseEntity<Object> deleteUserCourseByCourse(@PathVariable(value = "courseId") UUID courseId) {
        userCourseService.deleteUserCourseByCourseId(courseId);
        return ResponseEntity.status(HttpStatus.OK).body("UserCourse deleted successfully.");
    }
}
