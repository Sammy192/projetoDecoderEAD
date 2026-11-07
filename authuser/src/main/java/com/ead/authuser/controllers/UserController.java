package com.ead.authuser.controllers;

import com.ead.authuser.configs.security.AuthenticationCurrentUserService;
import com.ead.authuser.configs.exceptions.ConflictException;
import com.ead.authuser.configs.security.UserDetailsImpl;
import com.ead.authuser.dto.UserResponseDTO;
import com.ead.authuser.dto.UserDTORequest;
import com.ead.authuser.enums.UserTypeEnum;
import com.ead.authuser.models.UserModel;
import com.ead.authuser.services.UserService;
import com.ead.authuser.specifications.SpecificationTemplate;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@Tag(name = "User", description = "Endpoints para gerenciamento de usuários")
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
    @Operation(summary = "Listar todos os usuários", description = "Retorna uma lista paginada de usuários, com suporte a filtros dinâmicos.")
    @Parameter(name = "userType", description = "Filtra pelo tipo de usuário.", in = ParameterIn.QUERY, schema = @Schema(type = "string", allowableValues = {"STUDENT", "INSTRUCTOR", "ADMIN", "USER"}))
    @Parameter(name = "userStatus", description = "Filtra pelo status do usuário.", in = ParameterIn.QUERY, schema = @Schema(type = "string", allowableValues = {"ACTIVE", "BLOCKED"}))
    @Parameter(name = "email", description = "Filtra pelo email do usuário (busca parcial).", in = ParameterIn.QUERY, schema = @Schema(type = "string"))
    @Parameter(name = "fullName", description = "Filtra pelo nome completo do usuário (busca parcial, case-insensitive).", in = ParameterIn.QUERY, schema = @Schema(type = "string"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso."),
            @ApiResponse(responseCode = "403", description = "Acesso negado (somente ADMIN).")
    })
    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<UserResponseDTO>> getAllUsers(@ParameterObject SpecificationTemplate.UserSpec spec,
                                                             @ParameterObject  @PageableDefault(page = 0, size = 10, sort = "userId", direction = Sort.Direction.ASC) Pageable pageable,
                                                             Authentication authentication) {
        UserDetails userDetails = (UserDetailsImpl) authentication.getPrincipal();
        logger.info("Authentication {}", userDetails.getUsername());

        Page<UserModel> userModelPage = userService.findAll(spec, pageable);
        Page<UserResponseDTO> userDtoPage = userModelPage.map(userModel -> {
            UserResponseDTO responseDTO = new UserResponseDTO(userModel);
            responseDTO.add(linkTo(methodOn(UserController.class).getOneUser(userModel.getUserId())).withSelfRel());
            return responseDTO;
        });
        return ResponseEntity.ok(userDtoPage);
    }

    @Operation(summary = "Buscar um usuário pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso."),
            @ApiResponse(responseCode = "403", description = "Acesso negado."),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado.")
    })
    @PreAuthorize("hasAnyRole('ADMIN') or #userId == authentication.principal.userId")
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> getOneUser(@PathVariable(value = "userId") UUID userId) {
        UserModel userModel = userService.findById(userId);
        return ResponseEntity.status(HttpStatus.OK).body(new UserResponseDTO(userModel));
    }

    @Operation(summary = "Deletar um usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuário deletado com sucesso."),
            @ApiResponse(responseCode = "403", description = "Acesso negado."),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado.")
    })
    @PreAuthorize("hasAnyRole('ADMIN') or #userId == authentication.principal.userId")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable(value = "userId") UUID userId) {
        logger.debug("DELETE deleteUser userId received {} ", userId);
        userService.delete(userService.findById(userId));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Atualizar dados básicos do usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados inválidos."),
            @ApiResponse(responseCode = "403", description = "Acesso negado."),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado.")
    })
    @PreAuthorize("hasAnyRole('ADMIN') or #userId == authentication.principal.userId")
    @PutMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable(value = "userId") UUID userId,
                                             @RequestBody @Validated(UserDTORequest.UserView.UserPut.class)
                                             @JsonView(UserDTORequest.UserView.UserPut.class)
                                             UserDTORequest userDTORequest) {
        logger.debug("PUT updateUser userDto received {} ", userDTORequest);
        UserModel userModel = userService.updateUser(userDTORequest, userService.findById(userId));
        return ResponseEntity.status(HttpStatus.OK).body(new UserResponseDTO(userModel));
    }

    @Operation(summary = "Atualizar a senha do usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Senha atualizada com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados inválidos."),
            @ApiResponse(responseCode = "403", description = "Acesso negado."),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado."),
            @ApiResponse(responseCode = "409", description = "Senha anterior não confere.")
    })
    @PreAuthorize("hasAnyRole('ADMIN') or #userId == authentication.principal.userId")
    @PutMapping("/{userId}/password")
    public ResponseEntity<Void> updatePassword(@PathVariable(value = "userId") UUID userId,
                                             @RequestBody @Validated(UserDTORequest.UserView.PasswordPut.class)
                                             @JsonView(UserDTORequest.UserView.PasswordPut.class)
                                             UserDTORequest userDTORequest) {
        logger.debug("PUT updatePassord userId received {} ", userId);
        UserModel userModel = userService.findById(userId);
        if (!passwordEncoder.matches(userDTORequest.oldPassword(), userModel.getPassword())) {
            throw new ConflictException("Erro: Senha anterior não confere!");
        }
        userService.updatePassword(userDTORequest, userModel);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Atualizar a imagem de perfil do usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Imagem atualizada com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados inválidos."),
            @ApiResponse(responseCode = "403", description = "Acesso negado."),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado.")
    })
    @PreAuthorize("hasAnyRole('ADMIN') or #userId == authentication.principal.userId")
    @PutMapping("/{userId}/image")
    public ResponseEntity<UserResponseDTO> updateImage(@PathVariable(value = "userId") UUID userId,
                                             @RequestBody @Validated(UserDTORequest.UserView.ImagePut.class)
                                             @JsonView(UserDTORequest.UserView.ImagePut.class)
                                             UserDTORequest userDTORequest) {
        logger.debug("PUT updateImage userDto received {} ", userDTORequest);
        UserModel userModel = userService.updateImage(userDTORequest, userService.findById(userId));
        return ResponseEntity.status(HttpStatus.OK).body(new UserResponseDTO(userModel));
    }


    @Operation(summary = "Atualizar o tipo do usuário (role)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tipo do usuário atualizado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Tipo de usuário inválido."),
            @ApiResponse(responseCode = "403", description = "Acesso negado (somente ADMIN)."),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado.")
    })
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/{userId}/usertype")
    public ResponseEntity<Void> updateUserType(@PathVariable(value = "userId") UUID userId,
                                                 @RequestParam(value = "userType") UserTypeEnum userTypeEnum) {
        userService.updateUserType(userId, userTypeEnum);
        return ResponseEntity.noContent().build();
    }
}
