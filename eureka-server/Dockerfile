# Temel olarak OpenJDK 21 imajını alıyoruz
FROM openjdk:21-jdk-slim

# Uygulamanın JAR dosyasını /app dizinine kopyalayacağız
VOLUME /tmp

# Uygulamanın target dizinindeki JAR dosyasını alıyoruz
ARG JAR_FILE=target/eureka-server-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} /eureka-server.jar

# Eureka server'ı başlatıyoruz
ENTRYPOINT ["java", "-jar", "/eureka-server.jar"]
