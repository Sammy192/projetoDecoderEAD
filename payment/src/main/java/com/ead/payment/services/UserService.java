package com.ead.payment.services;


import com.ead.payment.dto.UserEventDTO;
import com.ead.payment.models.UserModel;

import java.util.UUID;

public interface UserService {

    UserModel saveUser(UserEventDTO userEventDTO);

    void deleteUserById(UUID userId);

    //UserModel findById(UUID userId);
}
