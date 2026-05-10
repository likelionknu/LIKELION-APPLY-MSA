FROM eclipse-temurin:21-jdk-alpine

EXPOSE 443

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

COPY config/application.yml /application.yml
COPY ./certs/keystore.p12 /etc/ssl/private/keystore.p12
RUN chmod 644 /etc/ssl/private/keystore.p12

ENTRYPOINT ["java", "-jar", "/app.jar", "--spring.config.location=file:/application.yml"]