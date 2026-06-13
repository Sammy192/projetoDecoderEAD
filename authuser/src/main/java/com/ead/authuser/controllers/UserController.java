package com.ead.authuser.controllers;

import com.ead.authuser.dto.UserDTORequest;
import com.ead.authuser.models.UserModel;
import com.ead.authuser.services.UserService;
import com.ead.authuser.specifications.SpecificationTemplate;
import com.fasterxml.jackson.annotation.JsonView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/users")
public class UserController {

    Logger logger = LogManager.getLogger(UserController.class);

    final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Page<UserModel>> getAllUsers(SpecificationTemplate.UserSpec spec,
                                                       Pageable pageable) {
        Page<UserModel> userModelPage = userService.findAll(spec, pageable);
        if(!userModelPage.isEmpty()) {
            userModelPage.toList().forEach(user -> user.add(linkTo(methodOn(UserController.class).getOneUser(user.getUserId())).withSelfRel()));
        }
        return ResponseEntity.ok(userModelPage);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getOneUser(@PathVariable(value = "userId") UUID userId) {
        UserModel userModelOptional = userService.findById(userId);
        return ResponseEntity.status(HttpStatus.OK).body(userModelOptional);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable(value = "userId") UUID userId) {
        logger.debug("DELETE deleteUser userId received {} ", userId);
        userService.delete(userService.findById(userId));
        return ResponseEntity.status(HttpStatus.OK).body("User deleted successfully.");
    }

    @PutMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable(value = "userId") UUID userId,
                                             @RequestBody @Validated(UserDTORequest.UserView.UserPut.class)
                                             @JsonView(UserDTORequest.UserView.UserPut.class)
                                             UserDTORequest userDTORequest) {
        logger.debug("PUT updateUser userDto received {} ", userDTORequest);
        UserModel userModel = userService.updateUser(userDTORequest, userService.findById(userId));
        return ResponseEntity.status(HttpStatus.OK).body(userModel);
    }

    @PutMapping("/{userId}/password")
    public ResponseEntity<Object> updatePassword(@PathVariable(value = "userId") UUID userId,
                                             @RequestBody @Validated(UserDTORequest.UserView.PasswordPut.class)
                                             @JsonView(UserDTORequest.UserView.PasswordPut.class)
                                             UserDTORequest userDTORequest) {
        logger.debug("PUT updatePassord userId received {} ", userId);
        UserModel userModel = userService.findById(userId);
        if (!userModel.getPassword().equals(userDTORequest.oldPassword())) {
            logger.warn("Mismatched old password! userId {}", userId);
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
        logger.debug("PUT updateImage userDto received {} ", userDTORequest);
        UserModel userModel = userService.updateImage(userDTORequest, userService.findById(userId));
        return ResponseEntity.status(HttpStatus.OK).body(userModel);
    }


    @PatchMapping("/{userId}/instructor")
    public ResponseEntity<Object> promoteToInstructor(@PathVariable(value = "userId") UUID userId) {
        logger.debug("PUT promoteToInstructor userId received {} ", userId);
        userService.promoteToInstructor(userId);

        return ResponseEntity.status(HttpStatus.OK).body("User promoted to instructor successfully.");
    }
}
