# =================== Build stage ===================
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app

# Layer 1: Copy only the POM files we actually need
COPY pom.xml .
COPY services/search-service/pom.xml ./services/search-service/pom.xml

# Layer 2: Download dependencies (cached until pom.xml changes)
WORKDIR /app/services/search-service
RUN mvn dependency:go-offline -B || true

# Layer 3: Copy source code
WORKDIR /app
COPY services/search-service ./services/search-service

# Layer 4: Build the search-service
WORKDIR /app/services/search-service
RUN mvn clean package -DskipTests

# =================== Run stage ===================
FROM amazoncorretto:17-alpine
WORKDIR /app
COPY --from=build /app/services/search-service/target/*.jar search-service.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "search-service.jar"]