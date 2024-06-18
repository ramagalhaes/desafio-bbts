FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/desafio-0.0.1-SNAPSHOT.jar /app/desafio.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/desafio.jar"]
