package com.ead.course.clients;

import com.ead.course.dto.CourseDTO;
import com.ead.course.dto.ResponsePageDTO;
import com.ead.course.dto.SubscriptionCourseDTO;
import com.ead.course.dto.UserDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Optional;
import java.util.UUID;

@Component
public class AuthUserClient {

    Logger logger = LogManager.getLogger(AuthUserClient.class);

    private final RestClient restClient;

    @Value("${ead.api.url.authuser}")
    private String baseUrlAuthuser;

    public AuthUserClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }

    public Page<UserDTO> getAllUsersByCourse(UUID courseId, Pageable pageable) {
        String url  = baseUrlAuthuser + "/users?courseId=" + courseId + "&page=" + pageable.getPageNumber() + "&size="
                + pageable.getPageSize() + "&sort=" + pageable.getSort().toString().replace(": ", ",");

        logger.debug("Request URL: {} ", url);

        try {
            return restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(new ParameterizedTypeReference<ResponsePageDTO<UserDTO>>() {});

        } catch (RestClientException e) {
            logger.error("Error Request RestClient with cause: {} ", e.getMessage());
            throw new RuntimeException("Error Request RestClient", e);
        }

    }

    public Optional<UserDTO> getUserById(UUID userId) {
        String url = baseUrlAuthuser + "/users/" + userId;
        logger.debug("Request URL: {} ", url);
        try {
            UserDTO userDTO = restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(UserDTO.class);
            return Optional.ofNullable(userDTO);
        } catch (HttpClientErrorException.NotFound e) {
            logger.warn("User not found with id: {}", userId);
            return Optional.empty();
        } catch (RestClientException e) {
            logger.error("Error Request RestClient with cause: {} ", e.getMessage());
            throw new RuntimeException("Error Request RestClient", e);
        }
    }

    public void postSubscriptionUserInCourse(UUID courseId, UUID userId) {
        String url = baseUrlAuthuser + "/users/" + userId + "/courses/subscription";
        var courseUserDTO = new SubscriptionCourseDTO(courseId);
        logger.debug("Request URL: {} ", url);
        try {
            restClient.post()
                    .uri(url)
                    .body(courseUserDTO)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            logger.error("Error Request RestClient with cause: {} ", e.getMessage());
            throw new RuntimeException("Error Request RestClient", e);
        }
    }

    public void deleteCourseInAuthUser(UUID courseId) {
        String url = baseUrlAuthuser + "/users/courses/" + courseId;
        logger.debug("Request URL: {} ", url);
        try {
            restClient.delete()
                    .uri(url)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            logger.error("Error Request RestClient with cause: {} ", e.getMessage());
            throw new RuntimeException("Error Request RestClient", e);
        }
    }

}
