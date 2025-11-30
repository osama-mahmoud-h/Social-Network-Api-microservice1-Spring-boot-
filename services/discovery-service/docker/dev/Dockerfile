# =================== Build stage ===================
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app

# Layer 1: Copy service-specific POM file from the correct path
# Since build context is project root, copy from services/discovery-service
COPY services/discovery-service/pom.xml .

# Layer 2: Download dependencies (cached until pom.xml changes)
# This creates a separate Docker layer that won't be invalidated by code changes
RUN mvn dependency:go-offline -B || true

# Layer 3: Copy source code from the correct path
COPY services/discovery-service/src ./src

# Layer 4: Build (reuses cached dependencies from Layer 2)
RUN mvn clean package -DskipTests

# =================== Run stage ===================
FROM amazoncorretto:17-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar eureka-server.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "eureka-server.jar"]
