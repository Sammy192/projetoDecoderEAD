package com.ead.recommendationagent.bootstraps;

import com.ead.recommendationagent.dtos.CourseEventRecordDto;
import com.ead.recommendationagent.dtos.UserSubscriptionEventRecordDto;
import com.ead.recommendationagent.services.VectorIngestionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@ConditionalOnProperty(name = "ead.runner.vectorstore.enabled", havingValue = "true", matchIfMissing = false)
public class VectorStoreSeedRunner implements ApplicationRunner {

    Logger logger = LogManager.getLogger(VectorStoreSeedRunner.class);

    private final VectorIngestionService service;

    public VectorStoreSeedRunner(VectorIngestionService service) {
        this.service = service;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info("Runner Vector Store with course and subscription data.");

        var course1 = new CourseEventRecordDto(
                "e4aca632-e678-4b9d-bf0e-63a5b0dab707",
                "Fundamentos de Java para Iniciantes",
                "Este curso ensina lógica de programação, sintaxe básica do Java.",
                "BEGINNER"
        );
        service.ingestCourse(course1);

        var course2 = new CourseEventRecordDto(
                "3d72238a-e614-41d2-bfea-ad42f70be133",
                "Spring Boot - APIs RESTful",
                "Criar APIs REST com Spring Boot, controllers, services, validação, tratamento de erros e integração com bancos de dados.",
                "INTERMEDIARY"
        );
        service.ingestCourse(course2);

        var course3 = new CourseEventRecordDto(
                "319f1504-db1e-44bd-8f00-c872eb8bf559",
                "Arquitetura de Microservices",
                "Padrões arquiteturais, comunicação assíncrona, mensageria.",
                "ADVANCED"
        );
        service.ingestCourse(course3);

        var course4 = new CourseEventRecordDto(
                "d5eafb66-f574-4005-95d6-0321a3a60e23",
                "Deploy e Cloud para Desenvolvedores",
                "Aprenda a configurar e realizar o deploy, subir facilmente seus services na nuvem.",
                "INTERMEDIARY"
        );
        service.ingestCourse(course4);

        var course5 = new CourseEventRecordDto(
                "452992de-62d6-43ce-85da-3dc728409fa1",
                "Git e Github para iniciantes",
                "Aprenda a versionar seus projetos e utilizar o Github.",
                "BEGINNER"
        );
        service.ingestCourse(course5);

        //subscription
        var subscription = new UserSubscriptionEventRecordDto(
                UUID.randomUUID().toString(), "e4aca632-e678-4b9d-bf0e-63a5b0dab707"
        );
        service.ingestUserSubscription(subscription);

        System.out.println("[Vector Store] Vectors saved successfully!");
    }
}
