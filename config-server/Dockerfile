FROM openjdk:21-jdk-slim

ARG JAR_FILE=build/libs/config-server-1.0.0-SNAPSHOT.jar

COPY ${JAR_FILE} app.jar

EXPOSE 8888

ENTRYPOINT ["java", "-jar", "/app.jar"]
