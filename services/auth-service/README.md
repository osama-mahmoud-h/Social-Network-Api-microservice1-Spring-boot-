# Auth Service - Authentication & Authorization Microservice

## Overview

The Auth Service is a comprehensive authentication and authorization microservice built with Spring Boot. It provides JWT-based authentication, multi-device session management, OTP verification, password management, and OAuth2 integration.

## Features

### ✅ Implemented Features

- [x] **JWT-Based Authentication**
  - User registration and login
  - Access and refresh tokens
  - Token validation and revocation

- [x] **Multi-Device Session Management**
  - Track user sessions across multiple devices
  - View all active device sessions
  - Logout from specific devices
  - Device type detection (Mobile, Web, Tablet, Desktop)
  - IP address and User-Agent tracking

- [x] **OTP Verification**
  - Send OTP via email for registration/verification
  - 6-digit secure OTP codes
  - 10-minute expiration
  - Redis-based storage for fast access
  - Support for multiple OTP types (Registration, Password Reset, Email Verification)

- [x] **Password Management**
  - Change password with validation
  - Old password verification
  - Password strength validation
  - Auto-logout from all devices after password change

- [x] **OAuth2 Integration** (Prepared)
  - User model extended with OAuth2 fields
  - OAuth2 provider enum (Google, Facebook, GitHub)
  - Ready for OAuth2 client integration

- [x] **Security Features**
  - Password encryption with BCrypt
  - Token expiration and revocation
  - Device fingerprinting
  - Rate limiting ready (via OTP)

## Architecture

### Design Patterns Used

1. **Strategy Pattern**: OTP delivery mechanism (Email, future SMS)
2. **Factory Pattern**: Device information extraction from HTTP requests
3. **Mapper Pattern**: DTO to Entity conversions with builders
4. **Repository Pattern**: Data access layer abstraction

### Technology Stack

- **Framework**: Spring Boot 3.1.0
- **Java**: 17
- **Database**: PostgreSQL
- **Cache**: Redis (for OTP storage)
- **Security**: Spring Security + JWT
- **Message Queue**: Kafka
- **Email**: Spring Mail
- **API Documentation**: OpenAPI/Swagger
- **Build Tool**: Maven

## Project Structure

```
src/main/java/com/app/auth/
├── config/              # Configuration classes
├── controller/          # REST API endpoints
├── dto/
│   ├── request/         # Request DTOs
│   └── response/        # Response DTOs
├── enums/               # Enumerations
│   ├── DeviceType
│   ├── OtpType
│   ├── OtpStatus
│   ├── OAuthProvider
│   ├── PasswordChangeResult
│   ├── UserRole
│   └── UserEventType
├── event/               # Kafka events
├── exception/           # Custom exceptions
├── factory/             # Factory classes
│   └── DeviceInfoFactory
├── mapper/              # DTO-Entity mappers
│   ├── AuthMapper
│   ├── DeviceMapper
│   └── OtpMapper
├── model/               # JPA entities
│   ├── User
│   ├── Token
│   └── Otp (Redis)
├── publisher/           # Kafka publishers
├── repository/          # Data repositories
│   ├── UserRepository
│   ├── TokenRepository
│   └── OtpRepository
├── security/            # Security configurations
├── service/             # Business logic
│   ├── AuthService
│   ├── DeviceService
│   ├── EmailService
│   ├── OtpService
│   ├── PasswordService
│   └── JwtService
└── utils/               # Utility classes
```

## API Endpoints

### Authentication Endpoints

#### 1. Register User
```http
POST /api/auth/register
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "password": "password123",
  "phoneNumber": "+1234567890"
}

Response: 201 Created
{
  "status": "success",
  "message": "User registered successfully",
  "data": {
    "accessToken": "eyJhbGc...",
    "refreshToken": "eyJhbGc...",
    "tokenType": "Bearer",
    "expiresIn": 3600,
    "user": {
      "id": 1,
      "email": "john.doe@example.com",
      "firstName": "John",
      "lastName": "Doe",
      "roles": ["USER"]
    }
  }
}
```

#### 2. Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "john.doe@example.com",
  "password": "password123"
}

Response: 200 OK
{
  "status": "success",
  "message": "Login successful",
  "data": {
    "accessToken": "eyJhbGc...",
    "refreshToken": "eyJhbGc...",
    "tokenType": "Bearer",
    "expiresIn": 3600,
    "user": { ... }
  }
}
```

#### 3. Validate Token
```http
POST /api/auth/validate?token=eyJhbGc...

Response: 200 OK
{
  "status": "success",
  "message": "Token validated",
  "data": {
    "valid": true,
    "userId": 1,
    "email": "john.doe@example.com",
    "roles": ["USER"]
  }
}
```

#### 4. Logout
```http
POST /api/auth/logout
Authorization: Bearer eyJhbGc...

Response: 200 OK
{
  "status": "success",
  "message": "Logout successful"
}
```

#### 5. Logout All Devices
```http
POST /api/auth/logout-all?userId=1
Authorization: Bearer eyJhbGc...

Response: 200 OK
{
  "status": "success",
  "message": "Logged out from all devices successfully"
}
```

### Device Management Endpoints

#### 6. Get User Devices
```http
GET /api/auth/devices?userId=1
Authorization: Bearer eyJhbGc...

Response: 200 OK
{
  "status": "success",
  "message": "Device sessions retrieved successfully",
  "data": [
    {
      "tokenId": 1,
      "deviceName": "Google Chrome",
      "deviceType": "WEB",
      "ipAddress": "192.168.1.100",
      "createdAt": "2025-01-01T10:00:00Z",
      "lastUsedAt": "2025-01-01T12:00:00Z",
      "expiresAt": "2025-01-02T10:00:00Z",
      "currentDevice": true
    },
    {
      "tokenId": 2,
      "deviceName": "iPhone",
      "deviceType": "MOBILE",
      "ipAddress": "192.168.1.101",
      "createdAt": "2025-01-01T09:00:00Z",
      "lastUsedAt": "2025-01-01T11:30:00Z",
      "expiresAt": "2025-01-02T09:00:00Z",
      "currentDevice": false
    }
  ]
}
```

#### 7. Logout Specific Device
```http
POST /api/auth/logout-device?userId=1
Authorization: Bearer eyJhbGc...
Content-Type: application/json

{
  "tokenId": 2
}

Response: 200 OK
{
  "status": "success",
  "message": "Device logged out successfully"
}
```

### OTP Endpoints

#### 8. Send OTP
```http
POST /api/auth/send-otp
Content-Type: application/json

{
  "email": "john.doe@example.com",
  "type": "REGISTRATION"
}

Response: 200 OK
{
  "status": "success",
  "message": "OTP sent successfully",
  "data": {
    "status": "PENDING",
    "message": "OTP sent successfully to john.doe@example.com",
    "expiresAt": "2025-01-01T10:10:00Z"
  }
}
```

#### 9. Verify OTP
```http
POST /api/auth/verify-otp
Content-Type: application/json

{
  "email": "john.doe@example.com",
  "code": "123456",
  "type": "REGISTRATION"
}

Response: 200 OK
{
  "status": "success",
  "message": "OTP verification completed",
  "data": {
    "status": "VERIFIED",
    "message": "OTP verified successfully"
  }
}
```

### Password Management Endpoints

#### 10. Change Password
```http
POST /api/auth/change-password?userId=1
Authorization: Bearer eyJhbGc...
Content-Type: application/json

{
  "oldPassword": "password123",
  "newPassword": "newPassword456",
  "confirmPassword": "newPassword456"
}

Response: 200 OK
{
  "status": "success",
  "message": "Password change processed",
  "data": {
    "result": "SUCCESS",
    "message": "Password changed successfully. All active sessions have been logged out."
  }
}
```

## Configuration

### Application Properties

Create `application.yml` with the following configuration:

```yaml
spring:
  application:
    name: auth-service

  datasource:
    url: jdbc:postgresql://localhost:5432/auth_db
    username: your_username
    password: your_password
    driver-class-name: org.postgresql.Driver

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true

  redis:
    host: localhost
    port: 6379
    timeout: 2000

  mail:
    host: smtp.gmail.com
    port: 587
    username: your-email@gmail.com
    password: your-app-password
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
            required: true
          connectiontimeout: 5000
          timeout: 5000
          writetimeout: 5000

  kafka:
    bootstrap-servers: localhost:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
    consumer:
      group-id: auth-service-group
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer

server:
  port: 8081

jwt:
  secret: your-secret-key-min-256-bits-long
  expiration: 3600000  # 1 hour in milliseconds
  refresh-expiration: 86400000  # 24 hours
```

### OAuth2 Configuration (For Future Implementation)

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: your-google-client-id
            client-secret: your-google-client-secret
            scope:
              - email
              - profile
          facebook:
            client-id: your-facebook-client-id
            client-secret: your-facebook-client-secret
            scope:
              - email
              - public_profile
```

## Database Schema

### Users Table
```sql
CREATE TABLE users (
    user_id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255),
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255),
    phone_number VARCHAR(50),
    enabled BOOLEAN NOT NULL DEFAULT true,
    account_non_expired BOOLEAN NOT NULL DEFAULT true,
    account_non_locked BOOLEAN NOT NULL DEFAULT true,
    credentials_non_expired BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL,
    last_login_at TIMESTAMP,
    oauth_provider VARCHAR(50),
    oauth_provider_id VARCHAR(255),
    email_verified BOOLEAN DEFAULT false
);
```

### Tokens Table
```sql
CREATE TABLE tokens (
    token_id BIGSERIAL PRIMARY KEY,
    token VARCHAR(1000) UNIQUE NOT NULL,
    token_type VARCHAR(50),
    revoked BOOLEAN,
    expired BOOLEAN,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    device_name VARCHAR(255),
    device_type VARCHAR(50),
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    last_used_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);
```

### User Roles Table
```sql
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role VARCHAR(50) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);
```

## Setup Instructions

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 13+
- Redis 6+
- Kafka 2.8+

### Installation Steps

1. **Clone the repository**
```bash
cd services/auth-service
```

2. **Create PostgreSQL database**
```sql
CREATE DATABASE auth_db;
```

3. **Start Redis**
```bash
redis-server
```

4. **Start Kafka**
```bash
# Start Zookeeper
zookeeper-server-start.sh config/zookeeper.properties

# Start Kafka
kafka-server-start.sh config/server.properties
```

5. **Configure application properties**
```bash
# Copy and edit application.yml
cp src/main/resources/application.yml.example src/main/resources/application.yml
# Edit with your database, Redis, Kafka, and email credentials
```

6. **Build the project**
```bash
mvn clean install
```

7. **Run the service**
```bash
mvn spring-boot:run
```

The service will start on `http://localhost:8081`

### API Documentation

Once the service is running, access the Swagger UI at:
```
http://localhost:8081/swagger-ui.html
```

## Testing

### Run Unit Tests
```bash
mvn test
```

### Run Integration Tests
```bash
mvn verify
```

### Manual Testing with cURL

```bash
# Register
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"firstName":"John","lastName":"Doe","email":"john@example.com","password":"password123"}'

# Login
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"john@example.com","password":"password123"}'

# Get Devices
curl -X GET "http://localhost:8081/api/auth/devices?userId=1" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

## Security Considerations

1. **JWT Secret**: Use a strong, random secret key (minimum 256 bits)
2. **Password Policy**: Enforce strong password requirements
3. **Rate Limiting**: Implement rate limiting for OTP and login endpoints
4. **HTTPS**: Always use HTTPS in production
5. **CORS**: Configure CORS appropriately for your frontend
6. **Token Rotation**: Implement refresh token rotation
7. **Session Management**: Regularly clean up expired tokens

## Future Enhancements

- [ ] Complete OAuth2 implementation (Google, Facebook, GitHub)
- [ ] Implement refresh token endpoint
- [ ] Add 2FA (Two-Factor Authentication)
- [ ] Implement password reset via email
- [ ] Add rate limiting with Redis
- [ ] Implement account lockout after failed attempts
- [ ] Add audit logging
- [ ] Implement email verification on registration
- [ ] Add SMS OTP support
- [ ] Implement biometric authentication support
- [ ] Add user profile management
- [ ] Implement role-based access control (RBAC)

## Troubleshooting

### Common Issues

1. **Database Connection Error**
   - Verify PostgreSQL is running
   - Check database credentials in application.yml
   - Ensure database exists

2. **Redis Connection Error**
   - Verify Redis is running: `redis-cli ping`
   - Check Redis host and port configuration

3. **Email Sending Failure**
   - Verify SMTP credentials
   - For Gmail, use App Password instead of regular password
   - Check firewall settings for SMTP port

4. **Kafka Connection Error**
   - Ensure Zookeeper and Kafka are running
   - Verify bootstrap-servers configuration
   - Check topic creation

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License.

## Contact

For questions or support, please open an issue in the repository.

---

## Implementation Checklist

### Phase 1: Core Authentication ✅
- [x] User registration
- [x] User login
- [x] JWT token generation
- [x] Token validation
- [x] Logout functionality

### Phase 2: Multi-Device Management ✅
- [x] Device type enum
- [x] Token model with device fields
- [x] Device info factory (User-Agent parsing)
- [x] Device service implementation
- [x] Get user devices endpoint
- [x] Logout specific device endpoint
- [x] Device mapper with builders

### Phase 3: OTP System ✅
- [x] OTP enums (Type, Status)
- [x] OTP entity (Redis-based)
- [x] OTP repository
- [x] OTP DTOs (Send, Verify)
- [x] OTP mapper with code generation
- [x] Email service for OTP delivery
- [x] OTP service with strategy pattern
- [x] Send OTP endpoint
- [x] Verify OTP endpoint

### Phase 4: Password Management ✅
- [x] PasswordChangeResult enum
- [x] Change password DTOs
- [x] Password service with validation
- [x] Change password endpoint
- [x] Auto-logout on password change
- [x] Password strength validation

### Phase 5: OAuth2 Integration ⏳
- [x] OAuth2 client dependency
- [x] OAuthProvider enum
- [x] User model OAuth2 fields
- [ ] OAuth2 DTOs and mapper
- [ ] OAuth2 strategy interface
- [ ] Google OAuth2 strategy
- [ ] Facebook OAuth2 strategy
- [ ] OAuth2 service implementation
- [ ] OAuth2 endpoints

### Phase 6: Documentation ✅
- [x] Comprehensive README
- [x] API endpoint documentation
- [x] Configuration guide
- [x] Setup instructions
- [x] Database schema documentation
- [x] Architecture overview
- [x] Implementation checklist

---

**Last Updated**: 2025-01-07
**Version**: 1.0.0
**Status**: In Development
