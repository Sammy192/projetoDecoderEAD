package com.ead.course.publishers;

import com.ead.course.dto.NotificationCommandDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class NotificationCommandPublischer {

    private final RabbitTemplate rabbitTemplate;

    public NotificationCommandPublischer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Value(value = "${ead.broker.exchange.notificationCommandExchange}")
    private String notificationCommandExchange;

    @Value(value = "${ead.broker.key.notificationCommandKey}")
    private String notificationCommandKey;

    public void publishNotificationCommand(NotificationCommandDTO notificationCommandDTO) {
        rabbitTemplate.convertAndSend(notificationCommandExchange, notificationCommandKey, notificationCommandDTO);
    }

}
