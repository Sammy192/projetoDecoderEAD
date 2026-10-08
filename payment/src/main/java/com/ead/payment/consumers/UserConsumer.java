package com.ead.payment.consumers;

import com.ead.payment.dto.UserEventDTO;
import com.ead.payment.enums.ActionType;
import com.ead.payment.services.UserService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class UserConsumer {

    private final UserService userService;

    public UserConsumer(UserService userService) {
        this.userService = userService;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "${ead.broker.queue.userEventQueue.name}", durable = "true"),
            exchange = @Exchange(value = "${ead.broker.exchange.userEventExchange}",
                    type = ExchangeTypes.FANOUT, ignoreDeclarationExceptions = "true"))
    )
    public void listenUserEvent(@Payload UserEventDTO userEventDTO) {
        switch (ActionType.valueOf(userEventDTO.actionType())) {
            case CREATE -> userService.saveUser(userEventDTO);
            case UPDATE -> userService.updateUser(userEventDTO);
            case DELETE -> userService.deleteUserById(userEventDTO.userId());
        }
    }
}
