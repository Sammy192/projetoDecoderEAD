package com.ead.authuser.controllers;

import com.ead.authuser.dto.UserDTORequest;
import com.ead.authuser.services.UserService;
import com.fasterxml.jackson.annotation.JsonView;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    Logger logger = LogManager.getLogger(AuthenticationController.class);

    private final UserService userService;

    public AuthenticationController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<Object> registerUser(@RequestBody @Validated(UserDTORequest.UserView.RegistrationPost.class)
                                               @JsonView(UserDTORequest.UserView.RegistrationPost.class)
                                               UserDTORequest userDTORequest) {
        logger.debug("POST registerUser userDto received {} ", userDTORequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.registerUser(userDTORequest));
    }

}
