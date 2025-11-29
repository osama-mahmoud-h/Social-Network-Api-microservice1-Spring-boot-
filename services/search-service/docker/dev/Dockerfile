# =================== Build stage ===================
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app

# Layer 1: Copy POM files ONLY (changes infrequently)
COPY pom.xml .
COPY shared/security-lib/pom.xml ./shared/security-lib/pom.xml
COPY services/search-service/pom.xml ./services/search-service/pom.xml

# Layer 2: Download dependencies (cached until pom.xml changes)
# This creates a separate Docker layer that won't be invalidated by code changes
RUN mvn dependency:go-offline -B \
    -pl shared/security-lib,services/search-service \
    -am \
    || true

# Layer 3: Now copy ALL source code
COPY shared ./shared
COPY services/search-service ./services/search-service

# Layer 4: Build (reuses cached dependencies from Layer 2)
RUN mvn clean install -pl shared/security-lib -am -DskipTests

# Build the search-service
WORKDIR /app/services/search-service
RUN mvn clean package -DskipTests

# =================== Run stage ===================
FROM amazoncorretto:17-alpine
WORKDIR /app
COPY --from=build /app/services/search-service/target/*.jar search-service.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "search-service.jar"]
