# =================== Build stage ===================
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app

# Layer 1: Copy only the POM files we actually need (parent + security-lib + this service)
COPY pom.xml .
COPY shared/security-lib/pom.xml ./shared/security-lib/pom.xml
COPY services/auth-service/pom.xml ./services/auth-service/pom.xml

# Layer 2: Download dependencies (cached until pom.xml changes)
# Build security-lib dependencies first
WORKDIR /app/shared/security-lib
RUN mvn dependency:go-offline -B || true

# Build auth-service dependencies
WORKDIR /app/services/auth-service
RUN mvn dependency:go-offline -B || true

# Layer 3: Now copy source code
WORKDIR /app
COPY shared/security-lib ./shared/security-lib
COPY services/auth-service ./services/auth-service

# Layer 4: Build security-lib first (standalone, not reactor build)
WORKDIR /app/shared/security-lib
RUN mvn clean install -DskipTests

# Build the auth-service (standalone)
WORKDIR /app/services/auth-service
RUN mvn clean package -DskipTests

# =================== Run stage ===================
FROM amazoncorretto:17-alpine
WORKDIR /app
COPY --from=build /app/services/auth-service/target/*.jar auth-service.jar
EXPOSE 8087
ENTRYPOINT ["java", "-jar", "auth-service.jar"]