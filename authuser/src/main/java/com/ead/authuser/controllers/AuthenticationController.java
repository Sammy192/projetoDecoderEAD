package com.ead.authuser.controllers;

import com.ead.authuser.configs.security.JwtProvider;
import com.ead.authuser.dto.JwtDTO;
import com.ead.authuser.dto.LoginDTO;
import com.ead.authuser.dto.UserDTORequest;
import com.ead.authuser.dto.UserResponseDTO;
import com.ead.authuser.models.UserModel;
import com.ead.authuser.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Endpoints para autenticação e registro")
public class AuthenticationController {

    Logger logger = LogManager.getLogger(AuthenticationController.class);

    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;

    public AuthenticationController(UserService userService, JwtProvider jwtProvider, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtProvider = jwtProvider;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/signup")
    @Operation(summary = "Registrar um novo usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário registrado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados de registro inválidos."),
            @ApiResponse(responseCode = "409", description = "Conflito: username ou email já existem.")
    })
    public ResponseEntity<UserResponseDTO> registerUser(@RequestBody @Validated(UserDTORequest.UserView.RegistrationPost.class)
                                                        @JsonView(UserDTORequest.UserView.RegistrationPost.class)
                                                        UserDTORequest userDTORequest) {
        logger.debug("POST registerUser userDto received {} ", userDTORequest);
        UserModel userModel = userService.registerUser(userDTORequest);
        UserResponseDTO responseDTO = new UserResponseDTO(userModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @PostMapping("/login")
    @Operation(summary = "Realizar login e obter um token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Login bem-sucedido, token JWT retornado."),
            @ApiResponse(responseCode = "400", description = "Dados de login inválidos."),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas (username ou senha incorretos).")
    })
    public ResponseEntity<JwtDTO> loginUser(@RequestBody @Valid LoginDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.username(), loginDTO.password()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtProvider.generateJwt(authentication);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new JwtDTO(jwt));
    }

}
