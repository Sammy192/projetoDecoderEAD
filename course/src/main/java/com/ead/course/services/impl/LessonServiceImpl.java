package com.ead.course.services.impl;

import com.ead.course.models.LessonModel;
import com.ead.course.models.ModuleModel;
import com.ead.course.repositories.LessonRepository;
import com.ead.course.services.LessonService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;

    public LessonServiceImpl(LessonRepository lessonRepository) {
        this.lessonRepository = lessonRepository;
    }

    @Transactional
    @Override
    public void deleteLessonsByModule(ModuleModel moduleModel) {
        List<LessonModel> lessonModelList = lessonRepository.findAllByModuleModuleId(moduleModel.getModuleId());
        if (!lessonModelList.isEmpty()) {
            lessonRepository.deleteAll(lessonModelList);
        }
    }
}