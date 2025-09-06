FROM openjdk:21-jdk-slim

ARG JAR_FILE=build/libs/api-gateway-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]
