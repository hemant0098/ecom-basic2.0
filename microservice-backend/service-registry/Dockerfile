FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY microservice-backend/service-registry/pom.xml .
COPY microservice-backend/service-registry/src ./src

RUN mvn package -DskipTests
EXPOSE 8761

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
CMD [ "java", "-jar", "app.jar" ]