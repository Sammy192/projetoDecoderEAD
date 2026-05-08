package com.ead.course.controllers;

import com.ead.course.dto.LessonDTO;
import com.ead.course.models.LessonModel;
import com.ead.course.services.LessonService;
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
public class LessonController {

    private final LessonService lessonService;

    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @PostMapping("/modules/{moduleId}/lessons")
    public ResponseEntity<Object> saveLessonIntoModule(@PathVariable(value = "moduleId") UUID moduleId,
                                                       @RequestBody @Valid LessonDTO lessonDto) {
        LessonModel lessonModel =  lessonService.saveLessonIntoModule(lessonDto, moduleId);
        return ResponseEntity.status(HttpStatus.CREATED).body(lessonModel);
    }

    @GetMapping("/modules/{moduleId}/lessons")
    public ResponseEntity<Page<LessonModel>> findAllLessonsByModuleId(@PathVariable(value = "moduleId") UUID moduleId,
                                                                      SpecificationTemplate.LessonSpec spec, Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(lessonService.findAllLessonsByModuleId(SpecificationTemplate.lessonModuleId(moduleId).and(spec), pageable));
    }

    @GetMapping("/modules/{moduleId}/lessons/{lessonId}")
    public ResponseEntity<Object> getOneLessonByModuleId(@PathVariable(value = "moduleId") UUID moduleId,
                                                         @PathVariable(value = "lessonId") UUID lessonId) {
        return ResponseEntity.status(HttpStatus.OK).body(lessonService.getOneLessonByModuleId(moduleId, lessonId));
    }

    @DeleteMapping("/modules/{moduleId}/lessons/{lessonId}")
    public ResponseEntity<Object> deleteLessonInsideAModule(@PathVariable(value = "moduleId") UUID moduleId,
                                                            @PathVariable(value = "lessonId") UUID lessonId) {
        lessonService.deleteLessonInsideAModule(moduleId, lessonId);
        return ResponseEntity.status(HttpStatus.OK).body("Lesson deleted successfully.");
    }

    @PutMapping("/modules/{moduleId}/lessons/{lessonId}")
    public ResponseEntity<Object> updateLessonInsideAModule(@PathVariable(value = "moduleId") UUID moduleId,
                                                            @PathVariable(value = "lessonId") UUID lessonId,
                                                            @RequestBody @Valid LessonDTO lessonDto) {
        LessonModel lessonModel = lessonService.updateLessonInsideAModule(moduleId, lessonId, lessonDto);
        return ResponseEntity.status(HttpStatus.OK).body(lessonModel);
    }

}
