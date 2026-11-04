package com.ead.course.controllers;

import com.ead.course.dto.LessonDTO;
import com.ead.course.dto.LessonResponseDTO;
import com.ead.course.models.LessonModel;
import com.ead.course.services.LessonService;
import com.ead.course.specifications.SpecificationTemplate;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Operation;
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
@Tag(name = "Lessons", description = "Endpoints para gerenciamento de Lições")
public class LessonController {

    Logger logger = LogManager.getLogger(LessonController.class);

    private final LessonService lessonService;

    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @Operation(summary = "Cria uma nova lição", description = "Cria uma nova lição dentro de um módulo. Requer perfil de INSTRUTOR.")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Lição criada com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
                        @ApiResponse(responseCode = "404", description = "Módulo não encontrado")
    })
    @PreAuthorize("hasAnyRole('INSTRUCTOR')")
    @PostMapping("/modules/{moduleId}/lessons")
    public ResponseEntity<LessonResponseDTO> saveLessonIntoModule(@PathVariable(value = "moduleId") UUID moduleId,
                                                       @RequestBody @Valid LessonDTO lessonDto) {
        logger.info("Entering saveLessonIntoModule with moduleId: {} and lessonDto: {}", moduleId, lessonDto);
        LessonModel lessonModel =  lessonService.saveLessonIntoModule(lessonDto, moduleId);
        return ResponseEntity.status(HttpStatus.CREATED).body(new LessonResponseDTO(lessonModel));
    }

    @Operation(summary = "Busca todas as lições de um módulo", description = "Retorna uma lista paginada de lições de um módulo específico. Requer perfil de ESTUDANTE.",
            parameters = {@Parameter(name = "title", description = "Filtra pelo título da lição (busca parcial, case-insensitive).",
                    in = ParameterIn.QUERY, schema = @Schema(type = "string"), example = "Primeiros Passos")
            })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de lições retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Módulo não encontrado")
    })
    @PreAuthorize("hasAnyRole('STUDENT')")
    @GetMapping("/modules/{moduleId}/lessons")
    public ResponseEntity<Page<LessonResponseDTO>> findAllLessonsByModuleId(@PathVariable(value = "moduleId") UUID moduleId,
                                                                      SpecificationTemplate.LessonSpec spec,
                                                                      @ParameterObject Pageable pageable) {
        logger.info("Entering findAllLessonsByModuleId with moduleId: {}", moduleId);
        Page<LessonModel> lessonModelPage = lessonService.findAllLessonsByModuleId(SpecificationTemplate.lessonModuleId(moduleId).and(spec), pageable);
        return ResponseEntity.status(HttpStatus.OK).body(lessonModelPage.map(LessonResponseDTO::new));
    }

    @Operation(summary = "Busca uma lição específica", description = "Retorna os detalhes de uma lição específica dentro de um módulo. Requer perfil de ESTUDANTE.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lição encontrada"),
            @ApiResponse(responseCode = "404", description = "Módulo ou lição não encontrada")
    })
    @PreAuthorize("hasAnyRole('STUDENT')")
    @GetMapping("/modules/{moduleId}/lessons/{lessonId}")
    public ResponseEntity<LessonResponseDTO> getOneLessonByModuleId(@PathVariable(value = "moduleId") UUID moduleId,
                                                         @PathVariable(value = "lessonId") UUID lessonId) {
        logger.info("Entering getOneLessonByModuleId with moduleId: {} and lessonId: {}", moduleId, lessonId);
        LessonModel lessonModel = lessonService.getOneLessonByModuleId(moduleId, lessonId);
        return ResponseEntity.status(HttpStatus.OK).body(new LessonResponseDTO(lessonModel));
    }

    @Operation(summary = "Deleta uma lição", description = "Deleta uma lição de um módulo. Requer perfil de INSTRUTOR.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Lição deletada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Módulo ou lição não encontrada")
    })
    @PreAuthorize("hasAnyRole('INSTRUCTOR')")
    @DeleteMapping("/modules/{moduleId}/lessons/{lessonId}")
    public ResponseEntity<Void> deleteLessonInsideAModule(@PathVariable(value = "moduleId") UUID moduleId,
                                                            @PathVariable(value = "lessonId") UUID lessonId) {
        logger.info("Entering deleteLessonInsideAModule with moduleId: {} and lessonId: {}", moduleId, lessonId);
        lessonService.deleteLessonInsideAModule(moduleId, lessonId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Atualiza uma lição", description = "Atualiza os dados de uma lição existente em um módulo. Requer perfil de INSTRUTOR.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lição atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Módulo ou lição não encontrada")
    })
    @PreAuthorize("hasAnyRole('INSTRUCTOR')")
    @PutMapping("/modules/{moduleId}/lessons/{lessonId}")
    public ResponseEntity<LessonResponseDTO> updateLessonInsideAModule(@PathVariable(value = "moduleId") UUID moduleId,
                                                            @PathVariable(value = "lessonId") UUID lessonId,
                                                            @RequestBody @Valid LessonDTO lessonDto) {
        logger.info("Entering updateLessonInsideAModule with moduleId: {}, lessonId: {} and lessonDto: {}", moduleId, lessonId, lessonDto);
        LessonModel lessonModel = lessonService.updateLessonInsideAModule(moduleId, lessonId, lessonDto);
        return ResponseEntity.status(HttpStatus.OK).body(new LessonResponseDTO(lessonModel));
    }

}
