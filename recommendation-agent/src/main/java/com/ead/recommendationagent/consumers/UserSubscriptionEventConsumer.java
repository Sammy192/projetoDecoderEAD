package com.ead.recommendationagent.consumers;

import com.ead.recommendationagent.dtos.UserSubscriptionEventRecordDto;
import com.ead.recommendationagent.services.CourseRecommendationService;
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
public class UserSubscriptionEventConsumer {

    Logger logger = LogManager.getLogger(UserSubscriptionEventConsumer.class);

    private final VectorIngestionService vectorIngestionService;
    private final CourseRecommendationService courseRecommendationService;

    public UserSubscriptionEventConsumer(VectorIngestionService vectorIngestionService, CourseRecommendationService courseRecommendationService) {
        this.vectorIngestionService = vectorIngestionService;
        this.courseRecommendationService = courseRecommendationService;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "${ead.broker.queue.userSubscriptionEventQueue.name}", durable = "true"),
            exchange = @Exchange(value = "${ead.broker.exchange.courseEventExchange}",
                    type = ExchangeTypes.TOPIC, ignoreDeclarationExceptions = "true"),
            key = "${ead.broker.key.userSubscriptionCreatedKey}")
    )
    public void listenUserSubscriptionEvent(@Payload UserSubscriptionEventRecordDto event) {
        logger.info("Listen User Subscription Event: UserId: {}", event.userId());

        vectorIngestionService.ingestUserSubscription(event);

        String recommendation = courseRecommendationService
                .recommendCourseForUser(event.userId());

        System.out.println("==== RECOMMENDATION GENERATED ====");
        System.out.println("UserId: " + event.userId());
        System.out.println(recommendation);
        System.out.println("==============================");
    }

}
