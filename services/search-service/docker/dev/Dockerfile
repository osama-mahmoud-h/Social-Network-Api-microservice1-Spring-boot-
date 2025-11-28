# Build stage
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app

# Copy parent POM and shared modules
COPY pom.xml .
COPY shared ./shared

# Copy all services (needed for Maven reactor)
COPY services ./services

# Build shared security library first
RUN mvn clean install -pl shared/security-lib -am -DskipTests

# Build the search-service
WORKDIR /app/services/search-service
RUN mvn clean package -DskipTests

# Run stage
FROM amazoncorretto:17-alpine
WORKDIR /app
COPY --from=build /app/services/search-service/target/*.jar search-service.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "search-service.jar"]
