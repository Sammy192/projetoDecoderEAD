package com.ead.course.repositories;

import com.ead.course.models.CourseModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CourseRepository extends JpaRepository<CourseModel, UUID>, JpaSpecificationExecutor<CourseModel> {
    boolean existsByName(String name);

    boolean existsByCourseIdAndUsersUserId(UUID courseId, UUID userId);

    @Modifying
    @Query(value = """
            insert into tb_courses_users(course_id, user_id)
            values (:courseId, :userId)
            """, nativeQuery = true)
    void saveSubscriptionUserInCourse(UUID courseId, UUID userId);

    @Modifying
    @Query(value = """
        delete
          from tb_courses_users
         where course_id = :courseId
        """, nativeQuery = true)
    void deleteSubscriptionsByCourseId(UUID courseId);
}
