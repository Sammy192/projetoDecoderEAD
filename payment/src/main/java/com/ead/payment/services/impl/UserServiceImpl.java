package com.ead.payment.services.impl;



import com.ead.payment.configs.exceptions.NotFoundException;
import com.ead.payment.dto.UserEventDTO;
import com.ead.payment.enums.PaymentStatusEnum;
import com.ead.payment.models.UserModel;
import com.ead.payment.repositories.UserRepository;
import com.ead.payment.services.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public UserModel saveUser(UserEventDTO userEventDTO) {
        UserModel userModel = new UserModel();
        BeanUtils.copyProperties(userEventDTO, userModel);
        //userModel.setPaymentStatus(PaymentStatusEnum.PENDING);
        return userRepository.save(userModel);
    }

    @Transactional
    @Override
    public UserModel updateUser(UserEventDTO userEventDTO) {
        UserModel userModel = new UserModel();
        BeanUtils.copyProperties(userEventDTO, userModel);
        return userRepository.save(userModel);
    }

    @Transactional
    @Override
    public void deleteUserById(UUID userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public UserModel findById(UUID userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found."));
    }

}
