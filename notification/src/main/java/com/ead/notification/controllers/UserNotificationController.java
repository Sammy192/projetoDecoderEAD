package com.ead.notification.controllers;

import com.ead.notification.configs.security.AuthenticationCurrentUserService;
import com.ead.notification.configs.security.UserDetailsImpl;
import com.ead.notification.dtos.NotificationDTO;
import com.ead.notification.dtos.NotificationStatusDTO;
import com.ead.notification.models.NotificationModel;
import com.ead.notification.services.NotificationService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "User Notifications", description = "Endpoints para gerenciar notificações de usuários")
public class UserNotificationController {

    private final NotificationService notificationService;
    private final AuthenticationCurrentUserService authenticationCurrentUserService;

    public UserNotificationController(NotificationService notificationService, AuthenticationCurrentUserService authenticationCurrentUserService) {
        this.notificationService = notificationService;
        this.authenticationCurrentUserService = authenticationCurrentUserService;
    }

    @Operation(summary = "Lista todas as notificações Não lidas de um usuário", description = "Retorna uma lista paginada de notificações não lidas para um usuário específico. Requer autenticação, e o usuário deve ser o dono do recurso ou um administrador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de notificações retornada com sucesso"),
            @ApiResponse(responseCode = "403", description = "Proibido - Acesso negado a este recurso",
                    content = @Content(schema = @Schema(implementation = com.ead.notification.configs.handlers.dto.CustomErrorDTO.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
                    content = @Content(schema = @Schema(implementation = com.ead.notification.configs.handlers.dto.CustomErrorDTO.class)))
    })
    @PreAuthorize("hasAnyRole('USER')")
    @GetMapping("/users/{userId}/notifications")
    public ResponseEntity<Page<NotificationDTO>> findAllNotificationsCreatedByUser(@PathVariable(value = "userId") UUID userId,
                                                                           @ParameterObject Pageable pageable) {
        UserDetailsImpl userDetails = authenticationCurrentUserService.getCurrentUser();
        if(userDetails.getUserId().equals(userId) || userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            Page<NotificationModel> notificationModelPage = notificationService.findAllNotificationsCreatedByUser(userId, pageable);
            return ResponseEntity.ok(notificationModelPage.map(NotificationDTO::new));
        } else {
            throw  new AccessDeniedException("Forbidden");
        }
    }

    @Operation(summary = "Atualiza o status de uma notificação", description = "Atualiza o status de uma notificação específica para 'LIDA'. Requer autenticação, e o usuário deve ser o dono do recurso ou um administrador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status da notificação atualizado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Proibido - Acesso negado a este recurso",
                    content = @Content(schema = @Schema(implementation = com.ead.notification.configs.handlers.dto.CustomErrorDTO.class))),
            @ApiResponse(responseCode = "404", description = "Notificação ou Usuário não encontrado",
                    content = @Content(schema = @Schema(implementation = com.ead.notification.configs.handlers.dto.CustomErrorDTO.class))),
            @ApiResponse(responseCode = "422", description = "Erro de validação - Dados inválidos",
                    content = @Content(schema = @Schema(implementation = com.ead.notification.configs.handlers.dto.ValidationErrorDTO.class)))
    })
    @PreAuthorize("hasAnyRole('USER')")
    @PutMapping("/users/{userId}/notifications/{notificationId}/status")
    public ResponseEntity<NotificationDTO> updateNotificationStatus(@PathVariable(value = "userId") UUID userId,
                                                           @PathVariable(value = "notificationId") UUID notificationId,
                                                           @RequestBody @Valid NotificationStatusDTO notificationStatusDTO) {
        UserDetailsImpl userDetails = authenticationCurrentUserService.getCurrentUser();
        if(userDetails.getUserId().equals(userId) || userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            NotificationModel notificationModel = notificationService.updateNotificationStatus(userId, notificationId, notificationStatusDTO);
            return ResponseEntity.ok(new NotificationDTO(notificationModel));
        } else {
            throw  new AccessDeniedException("Forbidden");
        }
    }
}
