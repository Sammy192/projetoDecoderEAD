package com.ead.course.publishers;

import com.ead.course.dto.CourseEventRecordDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CourseEventPublisher {

    final RabbitTemplate rabbitTemplate;

    public CourseEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Value(value = "${ead.broker.exchange.courseEventExchange}")
    private String courseEventExchange;

    @Value(value = "${ead.broker.key.courseCreatedKey}")
    private String courseCreatedKey;

    public void publishCourseEvent(CourseEventRecordDto courseEventRecordDto) {
        rabbitTemplate.convertAndSend(courseEventExchange, courseCreatedKey, courseEventRecordDto);
    }
}
