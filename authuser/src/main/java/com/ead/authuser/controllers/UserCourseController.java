package com.ead.authuser.controllers;

import com.ead.authuser.clients.CourseClient;
import com.ead.authuser.configs.security.AuthenticationCurrentUserService;
import com.ead.authuser.configs.security.UserDetailsImpl;
import com.ead.authuser.dto.CourseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class UserCourseController {

    private final CourseClient courseClient;
    private final AuthenticationCurrentUserService authenticationCurrentUserService;

    public UserCourseController(CourseClient courseClient, AuthenticationCurrentUserService authenticationCurrentUserService) {
        this.courseClient = courseClient;
        this.authenticationCurrentUserService = authenticationCurrentUserService;
    }

    //@PreAuthorize("hasAnyRole('ADMIN') or #userId == authentication.principal.userId")
    @PreAuthorize("hasAnyRole('USER')")
    @GetMapping("/users/{userId}/courses")
    public ResponseEntity<Page<CourseDTO>> getAllCoursesByUser(@PathVariable(value = "userId") UUID userId,
                                                               @PageableDefault(page = 0, size = 10, sort = "courseId", direction = Sort.Direction.ASC) Pageable pageable,
                                                               @RequestHeader("Authorization") String token) {
        UserDetailsImpl loggedInUser = authenticationCurrentUserService.getCurrentUser();
        boolean isUserAdmin = loggedInUser.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));

        if (!isUserAdmin && !loggedInUser.getUserId().equals(userId)) {
            //Disparar e tratar erro com a msg ou pode usar regra na notacao preAuthorize
            throw new AccessDeniedException("Acesso permitido somente para admin ou próprio usuário.");
        }

        return ResponseEntity.status(HttpStatus.OK).body(courseClient.getAllCoursesByUser(userId, pageable, token));
    }

}
