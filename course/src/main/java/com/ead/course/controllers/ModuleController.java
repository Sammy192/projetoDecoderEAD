package com.ead.course.controllers;

import com.ead.course.dto.ModuleDTO;
import com.ead.course.dto.ModuleResponseDTO;
import com.ead.course.models.ModuleModel;
import com.ead.course.services.ModuleService;
import com.ead.course.specifications.SpecificationTemplate;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class ModuleController {

    Logger logger = LogManager.getLogger(ModuleController.class);

    private final ModuleService moduleService;

    public ModuleController(ModuleService moduleService) {
        this.moduleService = moduleService;
    }

    @PreAuthorize("hasAnyRole('INSTRUCTOR')")
    @PostMapping("/courses/{courseId}/modules")
    public ResponseEntity<ModuleResponseDTO> saveModuleIntoCourse(@PathVariable(value = "courseId") UUID courseId,
                                                       @RequestBody @Valid ModuleDTO moduleDto) {
        logger.debug("POST saveModuleIntoCourse moduleDto received {} for courseId {} ", moduleDto, courseId);
        ModuleModel moduleModel =  moduleService.saveModuleIntoCourse(moduleDto, courseId);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ModuleResponseDTO(moduleModel));
    }

    @PreAuthorize("hasAnyRole('STUDENT')")
    @GetMapping("/courses/{courseId}/modules")
    public ResponseEntity<Page<ModuleResponseDTO>> findAllModulesByCourseId(@PathVariable(value = "courseId") UUID courseId,
                                                                      SpecificationTemplate.ModuleSpec spec, Pageable pageable) {
        logger.debug("GET findAllModulesByCourseId courseId received {} ", courseId);
        Page<ModuleModel> moduleModelPage = moduleService.findAllModulesByCourseId(SpecificationTemplate.moduleCourseId(courseId).and(spec), pageable);
        return ResponseEntity.status(HttpStatus.OK).body(moduleModelPage.map(ModuleResponseDTO::new));
    }

    @PreAuthorize("hasAnyRole('STUDENT')")
    @GetMapping("/courses/{courseId}/modules/{moduleId}")
    public ResponseEntity<ModuleResponseDTO> findModuleByIdIntoCourse(@PathVariable(value = "courseId") UUID courseId,
                                                           @PathVariable(value = "moduleId") UUID moduleId) {
        logger.debug("GET findModuleByIdIntoCourse courseId {} moduleId {} ", courseId, moduleId);
        ModuleModel moduleModel = moduleService.findModuleByIdIntoCourse(courseId, moduleId);
        return ResponseEntity.status(HttpStatus.OK).body(new ModuleResponseDTO(moduleModel));
    }

    @PreAuthorize("hasAnyRole('INSTRUCTOR')")
    @DeleteMapping("/courses/{courseId}/modules/{moduleId}")
    public ResponseEntity<Void> deleteModuleInsideACourse(@PathVariable(value = "courseId") UUID courseId,
                                                            @PathVariable(value = "moduleId") UUID moduleId) {
        logger.debug("DELETE deleteModuleInsideACourse courseId {} moduleId {} ", courseId, moduleId);
        moduleService.deleteModuleInsideACourse(courseId, moduleId);
        return ResponseEntity.noContent().build();
    }

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
