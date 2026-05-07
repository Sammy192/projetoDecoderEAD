package com.ead.course.controllers;

import com.ead.course.dto.ModuleDTO;
import com.ead.course.models.ModuleModel;
import com.ead.course.services.ModuleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class ModuleController {

    private final ModuleService moduleService;

    public ModuleController(ModuleService moduleService) {
        this.moduleService = moduleService;
    }

    @PostMapping("/courses/{courseId}/modules")
    public ResponseEntity<Object> saveModuleIntoCourse(@PathVariable(value = "courseId") UUID courseId,
                                                       @RequestBody @Valid ModuleDTO moduleDto) {
        ModuleModel moduleModel =  moduleService.saveModuleIntoCourse(moduleDto, courseId);
        return ResponseEntity.status(HttpStatus.CREATED).body(moduleModel);
    }

    @GetMapping("/courses/{courseId}/modules")
    public ResponseEntity<List<ModuleModel>> findAllModulesByCourseId(@PathVariable(value = "courseId") UUID courseId) {
        return ResponseEntity.status(HttpStatus.OK).body(moduleService.findAllModulesByCourseId(courseId));
    }

    @GetMapping("/courses/{courseId}/modules/{moduleId}")
    public ResponseEntity<Object> findModuleByIdIntoCourse(@PathVariable(value = "courseId") UUID courseId,
                                                           @PathVariable(value = "moduleId") UUID moduleId) {
        return ResponseEntity.status(HttpStatus.OK).body(moduleService.findModuleByIdIntoCourse(courseId, moduleId));
    }

    @DeleteMapping("/courses/{courseId}/modules/{moduleId}")
    public ResponseEntity<Object> deleteModuleInsideACourse(@PathVariable(value = "courseId") UUID courseId,
                                                            @PathVariable(value = "moduleId") UUID moduleId) {
        moduleService.deleteModuleInsideACourse(courseId, moduleId);
        return ResponseEntity.status(HttpStatus.OK).body("Module deleted successfully.");
    }

    @PutMapping("/courses/{courseId}/modules/{moduleId}")
    public ResponseEntity<Object> updateModuleInsideACourse(@PathVariable(value = "courseId") UUID courseId,
                                                            @PathVariable(value = "moduleId") UUID moduleId,
                                                            @RequestBody @Valid ModuleDTO moduleDto) {
        ModuleModel moduleModel = moduleService.updateModuleInsideACourse(courseId, moduleId, moduleDto);
        return ResponseEntity.status(HttpStatus.OK).body(moduleModel);
    }
}
