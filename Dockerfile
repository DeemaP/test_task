FROM openjdk:11-jre-slim
VOLUME /tmp
WORKDIR /app
COPY target/test_task-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]