package com.ead.course.repositories;

import com.ead.course.models.LessonModel;
import com.ead.course.models.ModuleModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LessonRepository extends JpaRepository<LessonModel, UUID> {

    List<LessonModel> findAllByModuleModuleId(UUID moduleId);

    Optional<LessonModel> findByModuleModuleIdAndLessonId(UUID moduleId, UUID lessonId);

    @Query("select l from LessonModel l where l.module.moduleId in :moduleIds")
    List<LessonModel> findAllLessonsIntoModules(@Param("moduleIds") List<UUID> moduleIds);

}
