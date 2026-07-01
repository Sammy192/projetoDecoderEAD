package com.ead.course.services.impl;

import com.ead.course.configs.exceptions.NotFoundException;
import com.ead.course.dto.UserEventDTO;
import com.ead.course.models.UserModel;
import com.ead.course.repositories.UserRepository;
import com.ead.course.services.CourseService;
import com.ead.course.services.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CourseService courseService;

    public UserServiceImpl(UserRepository userRepository, CourseService courseService) {
        this.userRepository = userRepository;
        this.courseService = courseService;
    }

    @Override
    public Page<UserModel> getAllUsersByCourse(Specification<UserModel> spec, Pageable pageable, UUID courseId) {
        if(!courseService.existsByCourseId(courseId)) throw new NotFoundException("Course not found.");
        return userRepository.findAll(spec, pageable);
    }

    @Transactional
    @Override
    public UserModel saveUser(UserEventDTO userEventDTO) {
        UserModel userModel = new UserModel();
        BeanUtils.copyProperties(userEventDTO, userModel);
        return userRepository.save(userModel);
    }

    @Transactional
    @Override
    public void deleteUserById(UUID userId) {
        userRepository.deleteById(userId);
    }

}
