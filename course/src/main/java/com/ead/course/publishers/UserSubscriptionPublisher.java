package com.ead.course.publishers;

import com.ead.course.dto.UserSubscriptionEventRecordDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UserSubscriptionPublisher {

    final RabbitTemplate rabbitTemplate;

    public UserSubscriptionPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Value(value = "${ead.broker.exchange.courseEventExchange}")
    private String courseEventExchange;

    @Value(value = "${ead.broker.key.userSubscriptionCreatedKey}")
    private String userSubscriptionCreatedKey;

    public void publishUserSubscriptionEvent(UserSubscriptionEventRecordDto userSubscriptionEventRecordDto) {
        rabbitTemplate.convertAndSend(courseEventExchange, userSubscriptionCreatedKey, userSubscriptionEventRecordDto);
    }

}
