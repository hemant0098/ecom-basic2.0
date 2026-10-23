FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY microservice-backend/order-service/pom.xml .
COPY microservice-backend/order-service/src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 9070
CMD [ "java", "-jar", "app.jar" ]