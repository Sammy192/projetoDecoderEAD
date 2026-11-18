package com.ead.recommendationagent.services;

import com.ead.recommendationagent.dtos.CourseEventRecordDto;
import com.ead.recommendationagent.dtos.UserSubscriptionEventRecordDto;

public interface VectorIngestionService {
    void ingestCourse(CourseEventRecordDto event);
    void ingestUserSubscription(UserSubscriptionEventRecordDto event);
}
