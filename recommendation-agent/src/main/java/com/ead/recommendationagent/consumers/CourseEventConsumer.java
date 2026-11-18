package com.ead.recommendationagent.consumers;

import com.ead.recommendationagent.dtos.CourseEventRecordDto;
import com.ead.recommendationagent.services.VectorIngestionService;
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
public class CourseEventConsumer {

    Logger logger = LogManager.getLogger(CourseEventConsumer.class);

    private final VectorIngestionService vectorIngestionService;

    public CourseEventConsumer(VectorIngestionService vectorIngestionService) {
        this.vectorIngestionService = vectorIngestionService;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "${ead.broker.queue.courseEventQueue.name}", durable = "true"),
            exchange = @Exchange(value = "${ead.broker.exchange.courseEventExchange}",
                    type = ExchangeTypes.TOPIC, ignoreDeclarationExceptions = "true"),
            key = "${ead.broker.key.courseCreatedKey}")
    )
    public void listenCourseEvent(@Payload CourseEventRecordDto event) {
        logger.info("Listen Course Event: CourseId: {}", event.courseId());
        vectorIngestionService.ingestCourse(event);
    }
}
