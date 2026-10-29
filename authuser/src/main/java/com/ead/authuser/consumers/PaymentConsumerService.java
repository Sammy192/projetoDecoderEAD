package com.ead.authuser.consumers;

import com.ead.authuser.dto.PaymentEventDTO;
import com.ead.authuser.enums.PaymentStatusControlEnum;
import com.ead.authuser.enums.UserTypeEnum;
import com.ead.authuser.models.RoleModel;
import com.ead.authuser.models.UserModel;
import com.ead.authuser.services.RoleService;
import com.ead.authuser.services.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class PaymentConsumerService {

    Logger logger = LogManager.getLogger(PaymentConsumer.class);

    private final UserService userService;
    private final RoleService roleService;

    public PaymentConsumerService(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }


    public void receberPaymentMessage(PaymentEventDTO paymentEventDTO) {
        UserModel userModel = userService.findById(paymentEventDTO.userId());
        RoleModel roleModel = roleService.findByRole(UserTypeEnum.STUDENT);

        switch (PaymentStatusControlEnum.valueOf(paymentEventDTO.paymentControl())) {
            case APPROVED -> {
                if(UserTypeEnum.USER.equals(userModel.getUserType())) {
                    userModel.setUserType(UserTypeEnum.STUDENT);
                    userModel.getRoles().add(roleModel);
                    userService.updateUser(userModel);
                }
            }
            case DECLINED -> {
                if(UserTypeEnum.STUDENT.equals(userModel.getUserType())) {
                    userModel.setUserType(UserTypeEnum.USER);
                    userModel.getRoles().remove(roleModel);
                    userService.updateUser(userModel);
                }
            }
            case ERROR -> logger.error("Payment with error userId: {}.", paymentEventDTO.userId());
        }


    }
}
