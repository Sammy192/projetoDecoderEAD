# Microserviço de Pagamento (EAD)

Este projeto é um microserviço responsável pelo gerenciamento de pagamentos dos usuários da plataforma EAD. Ele lida com a criação de solicitações de pagamento, processamento de transações via Stripe e notificação de resultados, seguindo as melhores práticas de arquitetura de microsserviços, com uma API RESTful bem definida, segura e documentada.

## Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3.x**
- **Spring Data JPA**: Para persistência de dados.
- **Spring Security**: Para controle de acesso e autenticação via JWT.
- **Spring AMQP**: Para comunicação assíncrona com RabbitMQ.
- **Stripe API**: Para processamento de pagamentos com cartão de crédito.
- **PostgreSQL**: Como banco de dados relacional.
- **Springdoc OpenAPI 3 (Swagger)**: Para documentação interativa da API.
- **Maven**: Como gerenciador de dependências e build.

---

## Como Começar

Siga os passos abaixo para configurar e executar o projeto em seu ambiente local.

### Pré-requisitos

- **JDK 17** ou superior.
- **Maven 3.8+**.
- **Docker** (recomendado para instanciar o banco de dados e o RabbitMQ de forma rápida).

### Executando a Aplicação

1.  Clone o repositório para sua máquina local.
2.  Navegue até o diretório raiz do projeto.
3.  Execute o comando abaixo para iniciar a aplicação com o Maven:

    ```sh
    mvn spring-boot:run
    ```

A aplicação estará disponível em `http://localhost:8086/ead-payment`.

---

## Documentação da API (Swagger)

A API deste microserviço é documentada utilizando OpenAPI 3, com uma interface interativa fornecida pelo Swagger UI. Após iniciar a aplicação, você pode acessar a documentação no seguinte endereço:

*   **URL do Swagger UI:** **http://localhost:8086/ead-payment/swagger-ui.html**

Na interface do Swagger, você pode visualizar todos os endpoints, seus parâmetros, os corpos de requisição esperados e os possíveis códigos de resposta, além de poder testar os endpoints diretamente.

### Segurança

Todos os endpoints são protegidos e requerem um **Token JWT** válido, que deve ser enviado no cabeçalho `Authorization` como um Bearer Token.

Este Token deve ser gerado no MS-AUTH-USER no endpoint **'/ead-authuser/auth/login'**

```http
Authorization: Bearer <seu-token-jwt>
```

---

## Padrão de Resposta de Erros

A API utiliza um padrão de resposta para erros, facilitando o tratamento no lado do cliente.

#### Erro Genérico / Não Encontrado (Ex: 404 NOT_FOUND)
```json
{
  "timestamp": "2026-11-02T21:00:00.000Z",
  "status": 404,
  "error": "Notificação não encontrada",
  "path": "/ead-notification/users/some-user-id/notifications/some-notification-id/status"
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

Este microserviço participa ativamente do ecossistema de eventos da plataforma, consumindo e publicando mensagens para garantir a consistência e o desacoplamento entre os serviços.

### Consumidores (Consumers)

-   **`UserConsumer`**:
    -   **Fila**: `ead.user.userevent-queue.ms.payment`
    -   **Exchange**: `ead.userevent-exchange` (FANOUT)
    -   **Descrição**: Ouve eventos relacionados a usuários (criação, atualização, deleção) publicados por outros serviços. Ele é responsável por manter a base de dados de usuários deste microsserviço sincronizada com a fonte da verdade (geralmente o microsserviço de autenticação/usuários).

-   **`PaymentConsumer`**:
    -   **Fila**: `ead.paymentcommand-queue.ms.payment`
    -   **Exchange**: `ead.paymentcommand-exchange` (TOPIC)
    -   **Descrição**: Ouve comandos para processar um pagamento. Após uma solicitação de pagamento ser criada via API, um comando é enviado para esta fila para ser processado de forma assíncrona, realizando a cobrança efetiva no gateway de pagamento (Stripe).

### Publicadores (Publishers)

-   **`PaymentCommandPublisher`**:
    -   **Exchange**: `ead.paymentcommand-exchange` (TOPIC)
    -   **Descrição**: Publica uma mensagem de comando (`PaymentCommandDTO`) para iniciar o processo de pagamento. Isso desacopla a solicitação de pagamento (via API) do seu processamento efetivo.

-   **`PaymentEventPublisher`**:
    -   **Exchange**: `ead.paymentevent-exchange` (FANOUT)
    -   **Descrição**: Publica uma mensagem de evento (`PaymentEventDTO`) após a conclusão de um processamento de pagamento. Este evento informa a outros microsserviços (como o de Cursos ou Notificação) sobre o resultado da transação (ex: `APPROVED`, `DECLINED`), permitindo que eles reajam de acordo (ex: liberando acesso a um curso).
