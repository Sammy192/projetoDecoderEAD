package com.ead.course.repositories;

import com.ead.course.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserModel, UUID>, JpaSpecificationExecutor<UserModel> {


    @Modifying
    @Query(value = """
        delete
          from tb_courses_users
         where user_id = :userId
        """, nativeQuery = true)
    void deleteSubscriptionsByUserId(UUID userId);
}
