package com.ead.authuser.controllers;

import com.ead.authuser.dto.UserDTORequest;
import com.ead.authuser.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final UserService userService;

    public AuthenticationController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<Object> registerUser(@RequestBody UserDTORequest userDTORequest) {
        if(userService.existsByUsername(userDTORequest.username())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: Username já existe.");
        }
        if(userService.existsByEmail(userDTORequest.email())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: Email já existe.");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.registerUser(userDTORequest));
    }

}
