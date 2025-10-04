# Build stage
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app

# Copy the full project to resolve parent POMs
COPY ../../pom.xml .
COPY ../../src ./src

# Download dependencies (and resolve parent POM)
#RUN mvn dependency:go-offline

# Build the application
RUN mvn clean package -DskipTests

# Run stage
FROM openjdk:17-slim
WORKDIR /app
COPY --from=build /app/target/*.jar main-service.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "main-service.jar"]

