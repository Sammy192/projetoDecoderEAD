package com.ead.recommendationagent.services.impl;

import com.ead.recommendationagent.dtos.CourseEventRecordDto;
import com.ead.recommendationagent.dtos.UserSubscriptionEventRecordDto;
import com.ead.recommendationagent.services.VectorIngestionService;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class VectorIngestionServiceImpl implements VectorIngestionService {

    private final VectorStore vectorStore;

    public VectorIngestionServiceImpl(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Override
    public void ingestCourse(CourseEventRecordDto event) {
        String content = """
            courseId: %s
            Curso: %s
            Descrição: %s
            Level: %s
            """.formatted(event.courseId(), event.title(), event.description(), event.courseLevel());

        Map<String, Object> metadata = Map.of(
                "domain", "courses",
                "type", "COURSE",
                "courseId", event.courseId(),
                "title", event.title(),
                "description", event.description(),
                "courseLevel", event.courseLevel()
        );

        vectorStore.add(List.of(new Document(content, metadata)));
    }

    @Override
    public void ingestUserSubscription(UserSubscriptionEventRecordDto event) {
        String content = """
            Histórico de inscrição:
            O userId %s se inscreveu no courseId %s.
            """
                .formatted(event.userId(), event.courseId());

        Map<String, Object> metadata = Map.of(
                "domain", "user_subscriptions",
                "type", "USER_SUBSCRIPTION",
                "userId", event.userId(),
                "courseId", event.courseId()
        );

        vectorStore.add(List.of(new Document(content, metadata)));
    }
}
