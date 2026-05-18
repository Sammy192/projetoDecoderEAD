package com.ead.course.controllers;

import com.ead.course.clients.AuthUserClient;
import com.ead.course.dto.SubscriptionDTO;
import com.ead.course.dto.UserDTO;
import com.ead.course.models.CourseUserModel;
import com.ead.course.services.CourseUserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class CourseUserController {

    private final AuthUserClient authUserClient;
    private final CourseUserService courseUserService;

    public CourseUserController(AuthUserClient authUserClient, CourseUserService courseUserService) {
        this.authUserClient = authUserClient;
        this.courseUserService = courseUserService;
    }

    @GetMapping("/courses/{courseId}/users")
    public ResponseEntity<Page<UserDTO>> getAllUsersByCourse(@PathVariable(value = "courseId") UUID courseId,
                                                             @PageableDefault(page = 0, size = 10, sort = "userId", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(authUserClient.getAllUsersByCourse(courseId, pageable));
    }

    @PostMapping("/courses/{courseId}/users/subscription")
    public ResponseEntity<Object> saveSubscriptionUserInCourse(@PathVariable(value = "courseId") UUID courseId,
                                                               @RequestBody @Valid SubscriptionDTO subscriptionDTO) {
        CourseUserModel courseUserModel = courseUserService.saveSubscriptionUserInCourse(courseId, subscriptionDTO.userId());
        return ResponseEntity.status(HttpStatus.CREATED).body(courseUserModel);

    }
}
