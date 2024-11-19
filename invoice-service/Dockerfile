FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/invoice-service-0.0.1-SNAPSHOT.jar /app/invoice-service.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/invoice-service.jar"]
