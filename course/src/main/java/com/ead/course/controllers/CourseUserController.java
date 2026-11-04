package com.ead.course.controllers;

import com.ead.course.configs.exceptions.NotFoundException;
import com.ead.course.dto.SubscriptionDTO;
import com.ead.course.dto.UserResponseDTO;
import com.ead.course.models.UserModel;
import com.ead.course.services.CourseService;
import com.ead.course.services.UserService;
import com.ead.course.specifications.SpecificationTemplate;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@Tag(name = "Courses/Users", description = "Endpoints para gerenciamento de inscrições de usuários em cursos")
public class CourseUserController {

    Logger logger = LogManager.getLogger(CourseUserController.class);

    private final CourseService courseService;
    private final UserService userService;

    public CourseUserController(CourseService courseService, UserService userService) {
        this.courseService = courseService;
        this.userService = userService;
    }

    @Operation(summary = "Busca usuários por curso", description = "Retorna uma lista paginada de usuários inscritos em um curso específico. Requer perfil de INSTRUTOR.")
    @Parameters({
            @Parameter(name = "email", description = "Filtra pelo email do usuário (busca parcial).", in = ParameterIn.QUERY, schema = @Schema(type = "string"), example = "test@example.com"),
            @Parameter(name = "fullName", description = "Filtra pelo nome completo do usuário (busca parcial, case-insensitive).", in = ParameterIn.QUERY, schema = @Schema(type = "string"), example = "John Doe"),
            @Parameter(name = "userStatus", description = "Filtra pelo status do usuário.", in = ParameterIn.QUERY, schema = @Schema(type = "string", allowableValues = {"ACTIVE", "BLOCKED"}), example = "ACTIVE"),
            @Parameter(name = "userType", description = "Filtra pelo tipo de usuário.", in = ParameterIn.QUERY, schema = @Schema(type = "string", allowableValues = {"STUDENT", "INSTRUCTOR", "ADMIN"}), example = "STUDENT")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuários encontrados"),
            @ApiResponse(responseCode = "404", description = "Curso não encontrado")
    })
    @PreAuthorize("hasAnyRole('INSTRUCTOR')")
    @GetMapping("/courses/{courseId}/users")
    public ResponseEntity<Page<UserResponseDTO>> getAllUsersByCourse(SpecificationTemplate.UserSpec spec,
                                                      @PathVariable(value = "courseId") UUID courseId,
                                                      @ParameterObject @PageableDefault(page = 0, size = 10, sort = "userId", direction = Sort.Direction.ASC) Pageable pageable) {
        logger.info("Entering getAllUsersByCourse with courseId: {}", courseId);
        if(!courseService.existsByCourseId(courseId)) throw new NotFoundException("Course not found.");
        Page<UserModel> userModelPage = userService.getAllUsersByCourse(SpecificationTemplate.userCourseId(courseId).and(spec), pageable);
        return ResponseEntity.status(HttpStatus.OK)
                .body(userModelPage.map(UserResponseDTO::new));
    }

    @Operation(summary = "Inscreve um usuário em um curso", description = "Realiza a inscrição de um usuário em um curso. Requer perfil de ESTUDANTE.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Inscrição realizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Curso não encontrado"),
            @ApiResponse(responseCode = "409", description = "Usuário já inscrito ou não encontrado")
    })
    @PreAuthorize("hasAnyRole('STUDENT')")
    @PostMapping("/courses/{courseId}/users/subscription")
    public ResponseEntity<Void> saveSubscriptionUserInCourse(@PathVariable(value = "courseId") UUID courseId,
                                                               @RequestBody @Valid SubscriptionDTO subscriptionDTO) {
        logger.info("Entering saveSubscriptionUserInCourse with courseId: {} and userId: {}", courseId, subscriptionDTO.userId());
        courseService.saveSubscriptionUserInCourse(courseId, subscriptionDTO.userId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
