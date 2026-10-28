package com.ead.payment.services.impl;



import com.ead.payment.configs.exceptions.NotFoundException;
import com.ead.payment.enums.UserStatusEnum;
import com.ead.payment.repositories.CreditCardRepository;
import com.ead.payment.dto.UserEventDTO;
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
    private final CreditCardRepository creditCardRepository;

    public UserServiceImpl(UserRepository userRepository, CreditCardRepository creditCardRepository) {
        this.userRepository = userRepository;
        this.creditCardRepository = creditCardRepository;
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
        UserModel userModel = this.findById(userEventDTO.userId());
        userModel.setFullName(userEventDTO.fullName());
        userModel.setPhoneNumber(userEventDTO.phoneNumber());
        userModel.setUserStatus(userEventDTO.userStatus());
        userModel.setUserType(userEventDTO.userType());
        return userRepository.save(userModel);
    }

    @Transactional
    @Override
    public void deleteUserById(UUID userId) {
        UserModel userModel = this.findById(userId);
        userModel.setDeleted(true);
        userModel.setUserStatus(UserStatusEnum.DELETED.name());
        creditCardRepository.findByUser(userModel).ifPresent(creditCard -> {
            creditCard.setDeleted(true);
            creditCardRepository.save(creditCard);
        });
        userRepository.save(userModel);
    }

    @Override
    public UserModel findById(UUID userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found."));
    }

}
