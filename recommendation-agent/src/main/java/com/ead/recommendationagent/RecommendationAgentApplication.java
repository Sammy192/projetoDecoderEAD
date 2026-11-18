package com.ead.recommendationagent;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Map;

@SpringBootApplication
public class RecommendationAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecommendationAgentApplication.class, args);
    }

//    @Bean
//    ApplicationRunner pgVectorAndGeminiConnectionTest(VectorStore vectorStore) {
//        return args -> {
//            try {
//                // 1. Cria um Document de teste
//                List<Document> docs = List.of(
//                        new Document("Teste de conexão com Neon e Gemini!", Map.of("meta1", "valor1"))
//                );
//
//                // 2. Converte e Salva: aciona o Gemini p/ vetorizar e o Neon p/ salvar o contexto
//                vectorStore.add(docs);
//
//                System.out.println("----------------------------------------------------------");
//                System.out.println("SUCESSO! Conexão com Banco Vetorial e Gemini OK.");
//                System.out.println("----------------------------------------------------------");
//
//            } catch (Exception e) {
//                System.err.println("ERRO: Não foi possível salvar no vetor.");
//                e.printStackTrace();
//            }
//        };
//    }

}
