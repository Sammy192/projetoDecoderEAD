package com.ead.course.controllers;

import com.ead.course.dto.ModuleDTO;
import com.ead.course.dto.ModuleResponseDTO;
import com.ead.course.models.ModuleModel;
import com.ead.course.services.ModuleService;
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
@Tag(name = "Modules", description = "Endpoints para gerenciamento de Módulos")
public class ModuleController {

    Logger logger = LogManager.getLogger(ModuleController.class);

    private final ModuleService moduleService;

    public ModuleController(ModuleService moduleService) {
        this.moduleService = moduleService;
    }

    @Operation(summary = "Cria um novo módulo", description = "Cria um novo módulo dentro de um curso. Requer perfil de INSTRUTOR.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Módulo criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Curso não encontrado")
    })
    @PreAuthorize("hasAnyRole('INSTRUCTOR')")
    @PostMapping("/courses/{courseId}/modules")
    public ResponseEntity<ModuleResponseDTO> saveModuleIntoCourse(@PathVariable(value = "courseId") UUID courseId,
                                                       @RequestBody @Valid ModuleDTO moduleDto) {
        logger.debug("POST saveModuleIntoCourse moduleDto received {} for courseId {} ", moduleDto, courseId);
        ModuleModel moduleModel =  moduleService.saveModuleIntoCourse(moduleDto, courseId);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ModuleResponseDTO(moduleModel));
    }

    @Operation(summary = "Busca todos os módulos de um curso", description = "Retorna uma lista paginada de módulos de um curso específico. Requer perfil de ESTUDANTE.",
            parameters = {@Parameter(name = "title", description = "Filtra pelo título do módulo (busca parcial, case-insensitive).",
                    in = ParameterIn.QUERY, schema = @Schema(type = "string"), example = "Introdução")
            })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de módulos retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Curso não encontrado")
    })
    @PreAuthorize("hasAnyRole('STUDENT')")
    @GetMapping("/courses/{courseId}/modules")
    public ResponseEntity<Page<ModuleResponseDTO>> findAllModulesByCourseId(@PathVariable(value = "courseId") UUID courseId,
                                                                      SpecificationTemplate.ModuleSpec spec,
                                                                      @ParameterObject Pageable pageable) {
        logger.debug("GET findAllModulesByCourseId courseId received {} ", courseId);
        Page<ModuleModel> moduleModelPage = moduleService.findAllModulesByCourseId(SpecificationTemplate.moduleCourseId(courseId).and(spec), pageable);
        return ResponseEntity.status(HttpStatus.OK).body(moduleModelPage.map(ModuleResponseDTO::new));
    }

    @Operation(summary = "Busca um módulo específico", description = "Retorna os detalhes de um módulo específico dentro de um curso. Requer perfil de ESTUDANTE.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Módulo encontrado"),
            @ApiResponse(responseCode = "404", description = "Curso ou módulo não encontrado")
    })
    @PreAuthorize("hasAnyRole('STUDENT')")
    @GetMapping("/courses/{courseId}/modules/{moduleId}")
    public ResponseEntity<ModuleResponseDTO> findModuleByIdIntoCourse(@PathVariable(value = "courseId") UUID courseId,
                                                           @PathVariable(value = "moduleId") UUID moduleId) {
        logger.debug("GET findModuleByIdIntoCourse courseId {} moduleId {} ", courseId, moduleId);
        ModuleModel moduleModel = moduleService.findModuleByIdIntoCourse(courseId, moduleId);
        return ResponseEntity.status(HttpStatus.OK).body(new ModuleResponseDTO(moduleModel));
    }

    @Operation(summary = "Deleta um módulo", description = "Deleta um módulo de um curso. Requer perfil de INSTRUTOR.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Módulo deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Curso ou módulo não encontrado")
    })
    @PreAuthorize("hasAnyRole('INSTRUCTOR')")
    @DeleteMapping("/courses/{courseId}/modules/{moduleId}")
    public ResponseEntity<Void> deleteModuleInsideACourse(@PathVariable(value = "courseId") UUID courseId,
                                                            @PathVariable(value = "moduleId") UUID moduleId) {
        logger.debug("DELETE deleteModuleInsideACourse courseId {} moduleId {} ", courseId, moduleId);
        moduleService.deleteModuleInsideACourse(courseId, moduleId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Atualiza um módulo", description = "Atualiza os dados de um módulo existente em um curso. Requer perfil de INSTRUTOR.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Módulo atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Curso ou módulo não encontrado")
    })
    @PreAuthorize("hasAnyRole('INSTRUCTOR')")
    @PutMapping("/courses/{courseId}/modules/{moduleId}")
    public ResponseEntity<ModuleResponseDTO> updateModuleInsideACourse(@PathVariable(value = "courseId") UUID courseId,
                                                            @PathVariable(value = "moduleId") UUID moduleId,
                                                            @RequestBody @Valid ModuleDTO moduleDto) {
        logger.debug("PUT updateModuleInsideACourse courseId {} moduleId {} moduleDto {} ", courseId, moduleId, moduleDto);
        ModuleModel moduleModel = moduleService.updateModuleInsideACourse(courseId, moduleId, moduleDto);
        return ResponseEntity.status(HttpStatus.OK).body(new ModuleResponseDTO(moduleModel));
    }
}
