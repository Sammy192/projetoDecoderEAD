![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens)
![Swagger](https://img.shields.io/badge/swagger-%2385EA2D.svg?style=for-the-badge&logo=swagger&logoColor=black)
![RabbitMQ](https://img.shields.io/badge/rabbitmq-%23FF6600.svg?style=for-the-badge&logo=rabbitmq&logoColor=white)
![Kibana](https://img.shields.io/badge/kibana-%23005571.svg?style=for-the-badge&logo=kibana&logoColor=white)


# Projeto Decoder EAD
Desenvolvido durante treinamento da Micheli Britto - Decoder;

## Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3.x**
- **Spring Data JPA**: Para persistência de dados.
- **Spring Security**: Para controle de acesso e autenticação via JWT.
- **Spring AMQP**: Para comunicação assíncrona com RabbitMQ.
- **PostgreSQL**: Como banco de dados relacional.
- **Springdoc OpenAPI 3 (Swagger)**: Para documentação interativa da API.
- **Maven**: Como gerenciador de dependências e build.

Estrutura resumida e tecnologias aplicadas:
<img width="1208" height="661" alt="image" src="https://github.com/user-attachments/assets/8e19b879-e08b-4e4a-80b0-a5376dbd9b41" />

Cada microserviço tem seu README que contém informações sobre a aplicação e acesso ao SWAGGER.

Arquivo da Collection Postman disponível na pasta raiz do repo;

## Iniciar primeira vez:

Após download do projeto siga os passos:

## OBS:

Necessário configurar variáveis de ambiente local para que estejam disponíveis no momento de execução do projeto para que o serviço de config consiga logar em seu repo git
de configuração e consiga obter os arquivos necessários de ambiente.
Tais var de ambientes são utilizadas pelo projeto 'config-server' como configuradas no docker-compose.yml

      - GITHUB_USER: ${GITHUB_USER} (Seu usuário da conta git)
      - GITHUB_PASS: ${GITHUB_PASS} (Seu token da conta git - necessário criar um token para ser utilizado para este fim)

<b> Necessário ter um config-repo privado no git. Exemplo de arquivos em pasta com nome 'config-repo-exemplo' <b>

Precisa criar uma pasta 'logs' na raiz do projeto com esta estrutura: (isto é para o Kibana gerar os logs corretamente)


<img width="1237" height="386" alt="image" src="https://github.com/user-attachments/assets/0e2112ea-caf2-4c55-a8f0-6b4c8dd64924" />



Inicie o docker compose com:
```
 docker compose create
```
Acesse o PGADMIN para configurarmos acessos ao bancos de dados que foram criados ao subir o arquivo docker:
```
http://localhost:5050/browser/
```
Segue imagens de configs de exemplos: ( configs estão no arquivo docker compose )


<img width="886" height="709" alt="image" src="https://github.com/user-attachments/assets/3366dcf0-6c2a-43a9-b056-709023f6d1e6" />

--

<img width="886" height="700" alt="image" src="https://github.com/user-attachments/assets/d51854dc-1586-4df8-82ad-bb322766bb38" />

--

<img width="886" height="699" alt="image" src="https://github.com/user-attachments/assets/268b0050-5ee5-4011-8395-495c1005bb08" />

--

<img width="886" height="687" alt="image" src="https://github.com/user-attachments/assets/6b248e08-9091-47ce-8eda-0a24a88a01b7" />


------------------------------------------------------------

Ficaria ao final assim os servers:


<img width="589" height="492" alt="image" src="https://github.com/user-attachments/assets/33b7cbe3-a6c0-49e7-9b9a-215b2183c159" />



Precisamos inserir um USER do tipo ADMIN para termos um user com privilégios elevados:
Comandos listados logo abaixo desta imagem que pode auxiliar a abrir o painel de query/consultas:

<img width="886" height="572" alt="image" src="https://github.com/user-attachments/assets/2b71904d-1540-4fb3-992d-dd80b41b358f" />

Insira os perfis do sistema ou ROLES:
```
INSERT INTO public.tb_roles (role_id, role)
VALUES
    (gen_random_uuid(), 'ADMIN'),
    (gen_random_uuid(), 'USER'),
    (gen_random_uuid(), 'STUDENT'),
    (gen_random_uuid(), 'INSTRUCTOR');

```
Depois o USER:
```
INSERT INTO public.tb_users ( user_id, creation_date, email, full_name, image_url,last_update_date, password, phone_number, user_status, user_type, username)
VALUES (
    gen_random_uuid(),
    CURRENT_TIMESTAMP,
    'lucassilva@example.com',
    'Lucas Silva',
    NULL,
    CURRENT_TIMESTAMP,
    '{bcrypt}$2a$10$u20Zm.OzFOl3xvXr0HMMQOFLOryl6dLQ/979CX.asOKBvjNcK8rNC',
    '11123423459',
    'ACTIVE',
    'ADMIN',
    'lucassilva'
);

```
Depois devemos linkar o USER_ID com o ROLE_ID: 
```
INSERT INTO public.tb_users_roles(
	user_id, role_id)
	VALUES ('2caaa2b9-d438-4a92-bedc-18c43b1eb99a', '57aab27d-0a38-45d3-854d-dc3adf1262d0');

```

Após essa preparação, podemos iniciar os microserviços:

Executar os dockers:
```
docker compose up -d
```

Subir os projetos na IDE na ordem:

1 - service-registry

2 - Config-server

3 - Api-gateway

4 - Authuser

5 - Course

6 - Notification

7 - payment


## Configuração Kibana

Ao iniciar o Kibana precisamos configurar a senha do 'kibana_system' que é perdida numa instalação docker do zero.
A senha ficará nesta seção do service do Kibana no arquivo docker-compose:
<img width="886" height="319" alt="image" src="https://github.com/user-attachments/assets/6f2a2d45-bf7a-415e-944b-c34637c0e3d5" />

Para gerar a senha:
No terminal:
```
docker exec -it elasticsearch /usr/share/elasticsearch/bin/elasticsearch-reset-password -u kibana_system -i
```
Guarde a senha que informou no comando anterior para colocar no arquivo Docker mencionado acima.
Após colocar no arquivo Docker a senha, recrie o serviço do Kibana como comando no terminal:

```
docker compose up -d --force-recreate kibana’
```

Feito isso poderá acessar o Kibana na url: 
```
http://localhost:5601/
```

User: elastic

Senha:1234567

Que fica configurada no arquivo Filebeat:  ".\elastic\filebeat.yml"

Agora poderá manipular os logs gerados e criar o dashboard. (perdido ao zerar os dados do docker - necessário salvar caso necessário)


