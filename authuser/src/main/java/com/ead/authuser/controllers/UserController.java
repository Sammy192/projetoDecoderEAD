package com.ead.authuser.controllers;

import com.ead.authuser.configs.security.AuthenticationCurrentUserService;
import com.ead.authuser.configs.security.UserDetailsImpl;
import com.ead.authuser.dto.UserDTORequest;
import com.ead.authuser.enums.UserTypeEnum;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/users")
public class UserController {

    Logger logger = LogManager.getLogger(UserController.class);

    private final UserService userService;
    private final AuthenticationCurrentUserService authenticationCurrentUserService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, AuthenticationCurrentUserService authenticationCurrentUserService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.authenticationCurrentUserService = authenticationCurrentUserService;
        this.passwordEncoder = passwordEncoder;
    }
    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<UserModel>> getAllUsers(SpecificationTemplate.UserSpec spec,
                                                       Pageable pageable,
                                                       Authentication authentication) {
        UserDetails userDetails = (UserDetailsImpl) authentication.getPrincipal();
        logger.info("Authentication {}", userDetails.getUsername());

        Page<UserModel> userModelPage = userService.findAll(spec, pageable);
        if(!userModelPage.isEmpty()) {
            userModelPage.toList().forEach(user -> user.add(linkTo(methodOn(UserController.class).getOneUser(user.getUserId())).withSelfRel()));
        }
        return ResponseEntity.ok(userModelPage);
    }

    @PreAuthorize("hasAnyRole('ADMIN') or #userId == authentication.principal.userId")
    @GetMapping("/{userId}")
    public ResponseEntity<Object> getOneUser(@PathVariable(value = "userId") UUID userId) {
        UUID currrentUserId = authenticationCurrentUserService.getCurrentUser().getUserId();
        if (currrentUserId.equals(userId)) {
            UserModel userModelOptional = userService.findById(userId);
            return ResponseEntity.status(HttpStatus.OK).body(userModelOptional);
        } else {
            throw new AccessDeniedException("Forbidden");
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN') or #userId == authentication.principal.userId")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable(value = "userId") UUID userId) {
        logger.debug("DELETE deleteUser userId received {} ", userId);
        userService.delete(userService.findById(userId));
        return ResponseEntity.status(HttpStatus.OK).body("User deleted successfully.");
    }

    @PreAuthorize("hasAnyRole('ADMIN') or #userId == authentication.principal.userId")
    @PutMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable(value = "userId") UUID userId,
                                             @RequestBody @Validated(UserDTORequest.UserView.UserPut.class)
                                             @JsonView(UserDTORequest.UserView.UserPut.class)
                                             UserDTORequest userDTORequest) {
        logger.debug("PUT updateUser userDto received {} ", userDTORequest);
        UserModel userModel = userService.updateUser(userDTORequest, userService.findById(userId));
        return ResponseEntity.status(HttpStatus.OK).body(userModel);
    }

    @PreAuthorize("hasAnyRole('ADMIN') or #userId == authentication.principal.userId")
    @PutMapping("/{userId}/password")
    public ResponseEntity<Object> updatePassword(@PathVariable(value = "userId") UUID userId,
                                             @RequestBody @Validated(UserDTORequest.UserView.PasswordPut.class)
                                             @JsonView(UserDTORequest.UserView.PasswordPut.class)
                                             UserDTORequest userDTORequest) {
        logger.debug("PUT updatePassord userId received {} ", userId);
        UserModel userModel = userService.findById(userId);
        if (!passwordEncoder.matches(userDTORequest.oldPassword(), userModel.getPassword())) {
            logger.warn("Mismatched old password! userId {}", userId);
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: Mismatched old password!");
        }
        userService.updatePassword(userDTORequest, userModel);
        return ResponseEntity.status(HttpStatus.OK).body("Password updated successfully.");
    }

    @PreAuthorize("hasAnyRole('ADMIN') or #userId == authentication.principal.userId")
    @PutMapping("/{userId}/image")
    public ResponseEntity<Object> updateImage(@PathVariable(value = "userId") UUID userId,
                                             @RequestBody @Validated(UserDTORequest.UserView.ImagePut.class)
                                             @JsonView(UserDTORequest.UserView.ImagePut.class)
                                             UserDTORequest userDTORequest) {
        logger.debug("PUT updateImage userDto received {} ", userDTORequest);
        UserModel userModel = userService.updateImage(userDTORequest, userService.findById(userId));
        return ResponseEntity.status(HttpStatus.OK).body(userModel);
    }


    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/{userId}/usertype")
    public ResponseEntity<Void> updateUserType(@PathVariable(value = "userId") UUID userId,
                                                 @RequestParam(value = "userType", defaultValue = "USER") UserTypeEnum userTypeEnum) {
        userService.updateUserType(userId, userTypeEnum);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
