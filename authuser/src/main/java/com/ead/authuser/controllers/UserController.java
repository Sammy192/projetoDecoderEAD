package com.ead.authuser.controllers;

import com.ead.authuser.dto.UserDTORequest;
import com.ead.authuser.models.UserModel;
import com.ead.authuser.services.UserService;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserModel>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getOneUser(@PathVariable(value = "userId") UUID userId) {
        UserModel userModelOptional = userService.findById(userId);
        return ResponseEntity.status(HttpStatus.OK).body(userModelOptional);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable(value = "userId") UUID userId) {
        userService.delete(userService.findById(userId));
        return ResponseEntity.status(HttpStatus.OK).body("User deleted successfully.");
    }

    @PutMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable(value = "userId") UUID userId,
                                             @RequestBody @Validated(UserDTORequest.UserView.UserPut.class)
                                             @JsonView(UserDTORequest.UserView.UserPut.class)
                                             UserDTORequest userDTORequest) {
        UserModel userModel = userService.updateUser(userDTORequest, userService.findById(userId));
        return ResponseEntity.status(HttpStatus.OK).body(userModel);
    }

    @PutMapping("/{userId}/password")
    public ResponseEntity<Object> updatePassword(@PathVariable(value = "userId") UUID userId,
                                             @RequestBody @Validated(UserDTORequest.UserView.PasswordPut.class)
                                             @JsonView(UserDTORequest.UserView.PasswordPut.class)
                                             UserDTORequest userDTORequest) {
        UserModel userModel = userService.findById(userId);
        if (!userModel.getPassword().equals(userDTORequest.oldPassword())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: Mismatched old password!");
        }
        userService.updatePassword(userDTORequest, userModel);
        return ResponseEntity.status(HttpStatus.OK).body("Password updated successfully.");
    }

    @PutMapping("/{userId}/image")
    public ResponseEntity<Object> updateImage(@PathVariable(value = "userId") UUID userId,
                                             @RequestBody @Validated(UserDTORequest.UserView.ImagePut.class)
                                             @JsonView(UserDTORequest.UserView.ImagePut.class)
                                             UserDTORequest userDTORequest) {
        UserModel userModel = userService.updateImage(userDTORequest, userService.findById(userId));
        return ResponseEntity.status(HttpStatus.OK).body(userModel);
    }

}
