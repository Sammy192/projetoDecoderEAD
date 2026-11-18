package com.ead.recommendationagent.bootstraps;

import com.ead.recommendationagent.dtos.UserSubscriptionEventRecordDto;
import com.ead.recommendationagent.services.CourseRecommendationService;
import com.ead.recommendationagent.services.VectorIngestionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@ConditionalOnProperty(name = "ead.runner.rag.enabled", havingValue = "true", matchIfMissing = false)
public class RagTriggerRunner implements ApplicationRunner {

    Logger logger = LogManager.getLogger(RagTriggerRunner.class);

    private final VectorIngestionService service;
    private final CourseRecommendationService courseRecommendationService;

    public RagTriggerRunner(VectorIngestionService service, CourseRecommendationService courseRecommendationService) {
        this.service = service;
        this.courseRecommendationService = courseRecommendationService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info("Runner RAG Trigger for course recommendation.");
        //subscription ingestion
        var subscription = new UserSubscriptionEventRecordDto(
                "500f74a0-a6eb-46d6-ad64-f8086cf3a8d8", "d5eafb66-f574-4005-95d6-0321a3a60e23"
        );
        service.ingestUserSubscription(subscription);

        //RAG Recommendation Course
        String recommendation = courseRecommendationService
                .recommendCourseForUser(subscription.userId());

        System.out.println("== RECOMMENDATION GENERATED ==");
        System.out.println("UserId: " + subscription.userId());
        System.out.println(recommendation);
        System.out.println("==============================");
    }
}
