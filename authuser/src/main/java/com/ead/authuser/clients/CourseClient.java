package com.ead.authuser.clients;

import com.ead.authuser.dto.CourseDTO;
import com.ead.authuser.dto.ResponsePageDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.http.HttpStatus.FORBIDDEN;

@Component
public class CourseClient {

    Logger logger = LogManager.getLogger(CourseClient.class);

    private final RestClient restClient;

    @Value("${ead.api.url.course}")
    private String baseUrlCourse;


    public CourseClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }

    //@Retry(name = "retryInstance", fallbackMethod = "fallbackForGetAllCoursesByUser")
    @CircuitBreaker(name = "circuitbreakerInstance", fallbackMethod = "fallbackForGetAllCoursesByUser")
    public Page<CourseDTO> getAllCoursesByUser(UUID userId, Pageable pageable, String token) {
        String url  = baseUrlCourse + "/courses?userId=" + userId + "&page=" + pageable.getPageNumber() + "&size="
                + pageable.getPageSize() + "&sort=" + pageable.getSort().toString().replace(": ", ",");

        logger.debug("Request URL: {} ", url);

        try {
            return restClient.get()
                    .uri(url)
                    .header("Authorization", token)
                    .retrieve()
                    .body(new ParameterizedTypeReference<ResponsePageDTO<CourseDTO>>() {});

        } catch (HttpStatusCodeException e) {
            logger.error("Error Request RestClient with status: {}, cause: {}", e.getStatusCode(), e.getMessage());
            switch (e.getStatusCode()) {
                case FORBIDDEN -> throw new AccessDeniedException("Forbidden");
                default -> throw new RuntimeException("Error Request RestClient", e);
            }
        } catch (RestClientException e) {
            logger.error("Error Request RestClient with cause: {} ", e.getMessage());
            throw new RuntimeException("Error Request RestClient", e);
        }

    }

    public Page<CourseDTO> fallbackForGetAllCoursesByUser(UUID userId, Pageable pageable, Throwable t) {
        logger.error("Error Request RestClient with cause: {} ", t.toString());
        List<CourseDTO> result = new ArrayList<>();
        return new PageImpl<>(result);
    }

    public Optional<CourseDTO> getCourseById(UUID courseId) {
        String url = baseUrlCourse + "/courses/" + courseId;
        logger.debug("Request URL: {} ", url);
        try {
            CourseDTO courseDTO = restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(CourseDTO.class);
            return Optional.ofNullable(courseDTO);
        } catch (HttpClientErrorException.NotFound e) {
            logger.warn("Course not found with id: {}", courseId);
            return Optional.empty();
        } catch (RestClientException e) {
            logger.error("Error Request RestClient with cause: {} ", e.getMessage());
            throw new RuntimeException("Error Request RestClient", e);
        }
    }

}
