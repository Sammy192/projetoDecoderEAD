package com.ead.course.controllers;

import com.ead.course.dto.ModuleDTO;
import com.ead.course.models.ModuleModel;
import com.ead.course.services.ModuleService;
import com.ead.course.specifications.SpecificationTemplate;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class ModuleController {

    Logger logger = LogManager.getLogger(ModuleController.class);

    private final ModuleService moduleService;

    public ModuleController(ModuleService moduleService) {
        this.moduleService = moduleService;
    }

    @PostMapping("/courses/{courseId}/modules")
    public ResponseEntity<Object> saveModuleIntoCourse(@PathVariable(value = "courseId") UUID courseId,
                                                       @RequestBody @Valid ModuleDTO moduleDto) {
        logger.debug("POST saveModuleIntoCourse moduleDto received {} for courseId {} ", moduleDto, courseId);
        ModuleModel moduleModel =  moduleService.saveModuleIntoCourse(moduleDto, courseId);
        return ResponseEntity.status(HttpStatus.CREATED).body(moduleModel);
    }

    @GetMapping("/courses/{courseId}/modules")
    public ResponseEntity<Page<ModuleModel>> findAllModulesByCourseId(@PathVariable(value = "courseId") UUID courseId,
                                                                      SpecificationTemplate.ModuleSpec spec, Pageable pageable) {
        logger.debug("GET findAllModulesByCourseId courseId received {} ", courseId);
        return ResponseEntity.status(HttpStatus.OK).body(moduleService.findAllModulesByCourseId(SpecificationTemplate.moduleCourseId(courseId).and(spec), pageable));
    }

    @GetMapping("/courses/{courseId}/modules/{moduleId}")
    public ResponseEntity<Object> findModuleByIdIntoCourse(@PathVariable(value = "courseId") UUID courseId,
                                                           @PathVariable(value = "moduleId") UUID moduleId) {
        logger.debug("GET findModuleByIdIntoCourse courseId {} moduleId {} ", courseId, moduleId);
        return ResponseEntity.status(HttpStatus.OK).body(moduleService.findModuleByIdIntoCourse(courseId, moduleId));
    }

    @DeleteMapping("/courses/{courseId}/modules/{moduleId}")
    public ResponseEntity<Object> deleteModuleInsideACourse(@PathVariable(value = "courseId") UUID courseId,
                                                            @PathVariable(value = "moduleId") UUID moduleId) {
        logger.debug("DELETE deleteModuleInsideACourse courseId {} moduleId {} ", courseId, moduleId);
        moduleService.deleteModuleInsideACourse(courseId, moduleId);
        return ResponseEntity.status(HttpStatus.OK).body("Module deleted successfully.");
    }

    @PutMapping("/courses/{courseId}/modules/{moduleId}")
    public ResponseEntity<Object> updateModuleInsideACourse(@PathVariable(value = "courseId") UUID courseId,
                                                            @PathVariable(value = "moduleId") UUID moduleId,
                                                            @RequestBody @Valid ModuleDTO moduleDto) {
        logger.debug("PUT updateModuleInsideACourse courseId {} moduleId {} moduleDto {} ", courseId, moduleId, moduleDTO);
        ModuleModel moduleModel = moduleService.updateModuleInsideACourse(courseId, moduleId, moduleDto);
        return ResponseEntity.status(HttpStatus.OK).body(moduleModel);
    }
}
