package com.ead.authuser.consumers;

import com.ead.authuser.dto.PaymentEventDTO;
import com.ead.authuser.enums.UserTypeEnum;
import com.ead.authuser.models.RoleModel;
import com.ead.authuser.models.UserModel;
import com.ead.authuser.services.RoleService;
import com.ead.authuser.services.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;


@Component
public class PaymentConsumer {

    Logger logger = LogManager.getLogger(PaymentConsumer.class);

    private final UserService userService;
    private final RoleService roleService;
    private final PaymentConsumerService paymentConsumerService;

    public PaymentConsumer(UserService userService, RoleService roleService, PaymentConsumerService paymentConsumerService) {
        this.userService = userService;
        this.roleService = roleService;
        this.paymentConsumerService = paymentConsumerService;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "${ead.broker.queue.paymentEventQueue.name}", durable = "true"),
            exchange = @Exchange(value = "${ead.broker.exchange.paymentEvent}",
                    type = ExchangeTypes.FANOUT, ignoreDeclarationExceptions = "true"))
    )
    public void listenPaymentEvent(@Payload PaymentEventDTO paymentEventDTO) {
        paymentConsumerService.receberPaymentMessage(paymentEventDTO);

    }
}
