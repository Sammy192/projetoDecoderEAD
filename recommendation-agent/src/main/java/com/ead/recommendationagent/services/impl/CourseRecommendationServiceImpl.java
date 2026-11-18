package com.ead.recommendationagent.services.impl;

import com.ead.recommendationagent.dtos.NotificationRecordCommandDto;
import com.ead.recommendationagent.publishers.NotificationCommandPublisher;
import com.ead.recommendationagent.services.CourseRecommendationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CourseRecommendationServiceImpl implements CourseRecommendationService {

    Logger logger = LogManager.getLogger(CourseRecommendationServiceImpl.class);

    private final NotificationCommandPublisher notificationCommandPublisher;
    private final VectorStore vectorStore;
    private final ChatModel chatModel;

    public CourseRecommendationServiceImpl(NotificationCommandPublisher notificationCommandPublisher, VectorStore vectorStore, ChatModel chatModel) {
        this.notificationCommandPublisher = notificationCommandPublisher;
        this.vectorStore = vectorStore;
        this.chatModel = chatModel;
    }

    @Override
    public String recommendCourseForUser(String userId) {
        logger.info("[RAG] Generating course recommendation for userId={}", userId);

        String filterExpression = """
            (domain == 'user_subscriptions' && userId == '%s')
            || domain == 'courses'
            """.formatted(userId);

        SearchRequest searchRequest = SearchRequest.builder()
                .topK(50)
                .similarityThreshold(0.2)
                .filterExpression(filterExpression)
                .build();

        QuestionAnswerAdvisor advisor = QuestionAnswerAdvisor.builder(vectorStore)
                .searchRequest(searchRequest)
                .build();

        String systemPromptTemplate = """
            Você é um agente inteligente de recomendação de cursos em uma plataforma EAD.
            
            Seu objetivo é analisar:
            1. O histórico de inscrições do usuário de cursos que ele deu inicio (recuperado automaticamente do contexto vetorial).
            2. O catálogo de cursos da plataforma (recuperado automaticamente do contexto vetorial).
            
            INSTRUÇÕES IMPORTANTES:
            - Utilize apenas as informações presentes no histórico acima e no catálogo de cursos fornecido no contexto.
            - Não invente cursos que não existam no contexto.
            - NÃO recomende cursos que já aparecem no histórico do usuário.
            - Escolha cursos que façam sentido como continuação lógica ou complementar ao histórico atual.
            - Inicie a mensagem cordialmente explicando que temos uma recomendação de curso para ajudar na continuidade do seu aprendizado.
            - Na segunda linha, inclua a recomendação do curso no formato: "Recomendamos o curso: [Título do Curso]".
            - Depois, explique diretamente para o usuário de forma breve o motivo da sua recomendação.
            - Seja objetivo, claro e responda sempre em português.
            
            Se o histórico não possuir dados suficientes, ainda assim tente sugerir cursos básicos ou complementares que façam sentido.
            """;

        String recommendationMessage = ChatClient.builder(chatModel)
                .defaultAdvisors(advisor)
                .build()
                .prompt()
                .system(systemPromptTemplate)
                .user("Recomende 1 curso para o usuário de ID %s e explique brevemente o motivo.".formatted(userId))
                .call()
                .content();

        sendCourseRecommendationNotification(userId, recommendationMessage);
        return recommendationMessage;

    }

    private void sendCourseRecommendationNotification(String userId, String recommendationMessage) {
        try {
            var notificationCommandDto = new NotificationRecordCommandDto(
                    "Recomendação de Curso: avance seus estudos!",
                    recommendationMessage,
                    UUID.fromString(userId));
            notificationCommandPublisher.publishNotificationCommand(notificationCommandDto);
        } catch (Exception e) {
            logger.warn("Error sending notification!", e);
        }
    }

}
