FROM openjdk:21-slim
ENV TZ=America/Sao_Paulo

WORKDIR /app

COPY build/libs/*.jar app.jar
COPY wait-for-it.sh wait-for-it.sh
RUN chmod +x wait-for-it.sh

EXPOSE 8081

RUN apt-get update && apt-get install -y netcat-openbsd && rm -rf /var/lib/apt/lists/*

ENTRYPOINT ["./wait-for-it.sh", "mariadb", "3306", "--", "./wait-for-it.sh", "vote-challenge", "8080", "--", "java", "-jar", "app.jar"]