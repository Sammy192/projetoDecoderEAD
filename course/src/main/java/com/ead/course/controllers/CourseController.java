package com.ead.course.controllers;

import com.ead.course.dto.CourseDTO;
import com.ead.course.dto.CourseResponseDTO;
import com.ead.course.models.CourseModel;
import com.ead.course.services.CourseService;
import com.ead.course.specifications.SpecificationTemplate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/courses")
@Tag(name = "Courses", description = "Endpoints para gerenciamento de Cursos")
public class CourseController {

    Logger logger = LogManager.getLogger(CourseController.class);

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @Operation(summary = "Cria um novo curso", description = "Cria um novo curso. Requer perfil de INSTRUTOR ou ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Curso criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "409", description = "Conflito: Nome do curso já existe ou usuário não é instrutor/admin")
    })
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    @PostMapping
    public ResponseEntity<CourseResponseDTO> saveCourse(@RequestBody @Valid CourseDTO courseDto) {
        logger.info("Entering saveCourse with courseDto: {}", courseDto);
        CourseModel courseModel = courseService.saveCourse(courseDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CourseResponseDTO(courseModel));
    }

    @Operation(summary = "Busca todos os cursos", description = "Retorna uma lista paginada de cursos, com filtros opcionais. Requer perfil de ESTUDANTE.")
    @Parameter(name = "userId", description = "ID do usuário para filtrar cursos onde ele está matriculado", in = ParameterIn.QUERY, schema = @Schema(type = "string", format = "uuid"))
    @Parameter(name = "name", description = "Filtra pelo nome do curso (busca parcial, case-insensitive).", in = ParameterIn.QUERY, schema = @Schema(type = "string"))
    @Parameter(name = "courseStatus", description = "Filtra pelo status do curso.", in = ParameterIn.QUERY, schema = @Schema(type = "string", allowableValues = {"INPROGRESS", "CONCLUDED"}))
    @Parameter(name = "courseLevel", description = "Filtra pelo nível do curso.", in = ParameterIn.QUERY, schema = @Schema(type = "string", allowableValues = {"BEGINNER", "INTERMEDIARY", "ADVANCED"}))
    @Parameter(name = "userInstructor", description = "Filtra pelo UUID do instrutor do curso.", in = ParameterIn.QUERY, schema = @Schema(type = "string", format = "uuid"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de cursos retornada com sucesso")
    })
    @PreAuthorize("hasAnyRole('STUDENT')")
    @GetMapping
    public ResponseEntity<Page<CourseResponseDTO>> getAllcourses(@ParameterObject SpecificationTemplate.CourseSpec spec,
                                                                @ParameterObject Pageable pageable,
                                                                @RequestParam(required = false) UUID userId) {
        logger.info("Entering getAllcourses");
        Page<CourseModel> courseModels = userId != null ? courseService.findAll(SpecificationTemplate.courseUserId(userId).and(spec), pageable) : courseService.findAll(spec, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(courseModels.map(CourseResponseDTO::new));
    }

    @Operation(summary = "Busca um curso pelo ID", description = "Retorna os detalhes de um curso específico. Requer perfil de ESTUDANTE.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Curso encontrado"),
            @ApiResponse(responseCode = "404", description = "Curso não encontrado")
    })
    @PreAuthorize("hasAnyRole('STUDENT')")
    @GetMapping("/{courseId}")
    public ResponseEntity<CourseResponseDTO> getOneCourse(@PathVariable(value = "courseId") UUID courseId) {
        logger.info("Entering getOneCourse with courseId: {}", courseId);
        CourseModel courseModel = courseService.findById(courseId);
        return ResponseEntity.status(HttpStatus.OK).body(new CourseResponseDTO(courseModel));
    }

    @Operation(summary = "Deleta um curso", description = "Deleta um curso existente. Requer perfil de INSTRUTOR.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Curso deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Curso não encontrado")
    })
    @PreAuthorize("hasAnyRole('INSTRUCTOR')")
    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> deleteCourse(@PathVariable(value = "courseId") UUID courseId) {
        logger.info("Entering deleteCourse with courseId: {}", courseId);
        courseService.deleteCourse(courseId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Atualiza um curso", description = "Atualiza os dados de um curso existente. Requer perfil de INSTRUTOR.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Curso atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Curso não encontrado")
    })
    @PreAuthorize("hasAnyRole('INSTRUCTOR')")
    @PutMapping("/{courseId}")
    public ResponseEntity<CourseResponseDTO> updateCourse(@PathVariable(value = "courseId") UUID courseId,
                                               @RequestBody @Valid CourseDTO courseDto) {
        logger.info("Entering updateCourse with courseId: {} and courseDto: {}", courseId, courseDto);
        CourseModel course = courseService.updateCourse(courseId, courseDto);

        return ResponseEntity.status(HttpStatus.OK).body(new CourseResponseDTO(course));
    }


}
