package com.ead.course.services.impl;

import com.ead.course.configs.exceptions.NotFoundException;
import com.ead.course.dto.LessonDTO;
import com.ead.course.models.LessonModel;
import com.ead.course.models.ModuleModel;
import com.ead.course.repositories.LessonRepository;
import com.ead.course.repositories.ModuleRepository;
import com.ead.course.services.LessonService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Service
public class LessonServiceImpl implements LessonService {

    Logger logger = LogManager.getLogger(LessonServiceImpl.class);

    private final LessonRepository lessonRepository;
    private final ModuleRepository moduleRepository;

    public LessonServiceImpl(LessonRepository lessonRepository, ModuleRepository moduleRepository) {
        this.lessonRepository = lessonRepository;
        this.moduleRepository = moduleRepository;
    }

    @Override
    public void deleteLessonInsideAModule(UUID moduleId, UUID lessonId) {
        if (!moduleRepository.existsById(moduleId)) throw new NotFoundException("Module not found.");
        deleteOneLesson(lessonId);
    }

    private void deleteOneLesson(UUID lessonId) {
        LessonModel lessonModel = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException("Lesson not found."));
        lessonRepository.delete(lessonModel);
        logger.info("Lesson with id {} deleted successfully.", lessonId);
    }

    @Transactional
    @Override
    public void deleteLessonsByModule(ModuleModel moduleModel) {
        logger.info("Deleting all lessons for moduleId: {}", moduleModel.getModuleId());
        List<LessonModel> lessonModelList = lessonRepository.findAllByModuleModuleId(moduleModel.getModuleId());
        if (!lessonModelList.isEmpty()) {
            lessonRepository.deleteAllInBatch(lessonModelList);
        }
    }

    @Transactional
    @Override
    public void deleteAllLessonsByModules(List<ModuleModel> moduleModels) {
        List<UUID> moduleIds = moduleModels.stream().map(ModuleModel::getModuleId).toList();
        logger.info("Deleting all lessons for moduleIds: {}", moduleIds);
        List<LessonModel> lessons = lessonRepository.findAllByModuleModuleIdIn(moduleIds);
        if (!lessons.isEmpty()) {
            lessonRepository.deleteAllInBatch(lessons);
        }
    }

    @Override
    public LessonModel saveLessonIntoModule(LessonDTO lessonDto, UUID moduleId) {
        logger.info("Attempting to save a new lesson in module {}", moduleId);
        ModuleModel moduleModel = getModuleModel(moduleId);
        var lessonModel = new LessonModel();
        BeanUtils.copyProperties(lessonDto, lessonModel);
        lessonModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        lessonModel.setModule(moduleModel);
        LessonModel savedLesson = lessonRepository.save(lessonModel);
        logger.info("Lesson saved successfully with id {}", savedLesson.getLessonId());
        return savedLesson;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LessonModel> findAllLessonsByModuleId(UUID moduleId) {
        if (!moduleRepository.existsById(moduleId)) throw new NotFoundException("Module not found.");
        return lessonRepository.findAllByModuleModuleId(moduleId);
    }

    @Override
    public Page<LessonModel> findAllLessonsByModuleId(Specification<LessonModel> spec, Pageable pageable) {
        return lessonRepository.findAll(spec, pageable);
    }

    @Override
    public LessonModel getOneLessonByModuleId(UUID moduleId, UUID lessonId) {
        if (!moduleRepository.existsById(moduleId)) throw new NotFoundException("Module not found.");
        return lessonRepository.findByModuleModuleIdAndLessonId(moduleId, lessonId)
                .orElseThrow(() -> new NotFoundException("Lesson not found for this module."));
    }

    @Override
    public LessonModel updateLessonInsideAModule(UUID moduleId, UUID lessonId, LessonDTO lessonDTO) {
        logger.info("Attempting to update lesson {} in module {}", lessonId, moduleId);
        LessonModel lessonModel = getOneLessonByModuleId(moduleId, lessonId);
        BeanUtils.copyProperties(lessonDTO, lessonModel);
        LessonModel updatedLesson = lessonRepository.save(lessonModel);
        logger.info("Lesson updated successfully with id {}", updatedLesson.getLessonId());
        return updatedLesson;
    }

    private ModuleModel getModuleModel(UUID moduleId) {
        return moduleRepository.findById(moduleId).orElseThrow(() -> new NotFoundException("Module not found."));
    }
}