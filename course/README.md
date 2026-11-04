# Microserviço de Cursos (EAD)

Este projeto é o microserviço de Cursos da plataforma EAD. Ele é responsável por todo o gerenciamento de cursos, módulos e lições, além de controlar as inscrições dos usuários nos cursos. A arquitetura segue as melhores práticas de microsserviços, com uma API RESTful bem definida, segura e documentada.

## Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3.x**
- **Spring Data JPA**: Para persistência de dados.
- **Spring Security**: Para controle de acesso e autenticação via JWT.
- **Spring AMQP**: Para comunicação assíncrona com RabbitMQ.
- **PostgreSQL**: Como banco de dados relacional.
- **Springdoc OpenAPI 3 (Swagger)**: Para documentação interativa da API.
- **Maven**: Como gerenciador de dependências e build.

---

## Como Começar

Siga os passos abaixo para configurar e executar o projeto em seu ambiente local.

### Pré-requisitos

- **JDK 17** ou superior.
- **Maven 3.8+**.
- **Docker** e **Docker Compose** (recomendado para instanciar o banco de dados e o RabbitMQ de forma rápida).

### Executando a Aplicação

1.  Clone o repositório para sua máquina local.
2.  Navegue até o diretório raiz do projeto.
3.  Para subir a infraestrutura (PostgreSQL e RabbitMQ), use o Docker Compose:
    ```sh
    docker-compose up -d
    ```
4.  Execute o comando abaixo para iniciar a aplicação com o Maven:

    ```sh
    mvn spring-boot:run
    ```

A aplicação estará disponível em `http://localhost:8082/ead-course`.

---

## Documentação da API (Swagger)

A API deste microserviço é documentada utilizando OpenAPI 3, com uma interface interativa fornecida pelo Swagger UI. Após iniciar a aplicação, você pode acessar a documentação no seguinte endereço:

*   **URL do Swagger UI:** **http://localhost:8082/ead-course/swagger-ui.html**

Na interface do Swagger, você pode visualizar todos os endpoints, seus parâmetros, os corpos de requisição esperados e os possíveis códigos de resposta, além de poder testar os endpoints diretamente.

### Segurança

Todos os endpoints são protegidos e requerem um **Token JWT** válido, que deve ser enviado no cabeçalho `Authorization` como um Bearer Token.

Este Token deve ser gerado no microsserviço de autenticação (MS-AUTH-USER) no endpoint **`/auth/login`**.

```http
Authorization: Bearer <seu-token-jwt>
```

---

## Padrão de Resposta de Erros

A API utiliza um padrão de resposta para erros, facilitando o tratamento no lado do cliente.

#### Erro Genérico / Não Encontrado (Ex: 404 NOT_FOUND, 409 CONFLICT)
```json
{
  "timestamp": "2026-11-02T21:00:00.000Z",
  "status": 404,
  "error": "Curso não encontrado",
  "path": "/ead-course/courses/some-course-id"
}
```

#### Erro de Validação (422 UNPROCESSABLE_ENTITY)
```json
{
  "timestamp": "2026-11-02T21:00:00.000Z",
  "status": 422,
  "error": "Dados inválidos",
  "path": "/ead-notification/users/some-user-id/notifications/some-notification-id/status",
  "errors": [
    {
      "fieldName": "notificationStatus",
      "message": "O status da notificação não pode ser nulo"
    }
  ]
}
```

 
---

## Comunicação Assíncrona (Mensageria)
