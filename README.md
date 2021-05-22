# clinica-abc-bot

## Como ejecutarlo?

1) Crear un bot en Telegram, se puede seguir [esta página](https://sendpulse.com/knowledge-base/chatbot/create-telegram-chatbot).

2) Crear estas variables de ambiente:

|Variable de ambiente|Valor|Ejemplo|
|---|---|---|
|`BOT_TOKEN`|Token del bot. Se puede obtener preguntándole a **BotFather**|5529559979:AAHm3-YuDxsq5hTC-FaTSJ12tyQnB7OH5tt|
|`USERS_URL`|URL del microservicio de usuarios|http://localhost:8080|
|`SCHEDULES_URL`|URL del microservicio de agendamiento|http://localhost:3000/graphql|
|`AUTHORIZATIONS_URL`|URL del microservicio de autorizaciones|http://localhost:9090|

3) Correr el proyecto:

```bash
./gradlew bootRun
```

4) En Telegram, bucar el bot creado y a disfrutar !
