package com.ead.authuser.services.impl;

import com.ead.authuser.services.exceptions.NotFoundException;
import com.ead.authuser.models.UserModel;
import com.ead.authuser.repositories.UserRepository;
import com.ead.authuser.services.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserModel> findAll() {
        return userRepository.findAll();
    }

    @Override
    public UserModel findById(UUID userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Error: User not found"));
    }

    @Override
    public void delete(UserModel userModel) {
        userRepository.delete(userModel);
    }
}
