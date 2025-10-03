# Microservices Architecture Migration Guide

## Overview
This guide documents the migration from a monolithic user management system to a microservices architecture with separate Auth-Service and Main-Service.

## Architecture Changes

### Before (Monolithic)
- Main-service handled both authentication and business logic
- Single database with `AppUser` table
- Direct relationships: `Post` → `AppUser`, `Comment` → `AppUser`

### After (Microservices)
- **Auth-Service**: Handles user authentication, registration, JWT tokens
- **Main-Service**: Handles posts, comments, profiles (business logic)
- **Separate Databases**: Each service owns its data
- **Event-Driven Sync**: Kafka events synchronize user data

## Database Schema

### Auth-Service Database (`auth_db`)
```sql
-- Source of truth for authentication
users (
    id BIGINT PRIMARY KEY,
    first_name VARCHAR NOT NULL,
    last_name VARCHAR,
    email VARCHAR UNIQUE NOT NULL,
    password VARCHAR NOT NULL,      -- Only in auth-service
    phone_number VARCHAR,
    created_at TIMESTAMP NOT NULL,
    last_login_at TIMESTAMP
)

user_roles (
    user_id BIGINT,
    role VARCHAR                      -- Only in auth-service
)

tokens (
    id BIGINT PRIMARY KEY,
    user_id BIGINT,
    token VARCHAR,
    revoked BOOLEAN,
    expires_at TIMESTAMP
)
```

### Main-Service Database (`main_db`)
```sql
-- Synchronized minimal user data
user_profiles (
    user_id BIGINT PRIMARY KEY,      -- Same ID from auth-service
    first_name VARCHAR NOT NULL,
    last_name VARCHAR,
    email VARCHAR UNIQUE NOT NULL,
    phone_number VARCHAR,
    created_at TIMESTAMP NOT NULL,
    synced_at TIMESTAMP              -- Last sync timestamp
    -- NO password field
    -- NO roles field
)

posts (
    post_id BIGINT PRIMARY KEY,
    author_id BIGINT,                -- References user_profiles.user_id
    content VARCHAR,
    created_at TIMESTAMP
)

comments (
    comment_id BIGINT PRIMARY KEY,
    author_id BIGINT,                -- References user_profiles.user_id
    post_id BIGINT,
    content VARCHAR,
    created_at TIMESTAMP
)

profiles (
    profile_id BIGINT PRIMARY KEY,
    user_id BIGINT,                  -- References user_profiles.user_id
    bio VARCHAR,
    image_url VARCHAR,
    address_id BIGINT
)
```

## Event Flow

### User Registration Flow
```
1. Client → Gateway → Auth-Service POST /api/auth/register
2. Auth-Service creates User in auth_db
3. Auth-Service generates JWT tokens
4. Auth-Service publishes UserCreatedEvent to Kafka (topic: user-events)
5. Main-Service consumes event and creates UserProfile in main_db
6. Auth-Service returns tokens to client
```

### Event Schema
```json
// UserCreatedEvent
{
  "userId": 123,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "phoneNumber": "+1234567890",
  "createdAt": "2025-10-03T09:00:00Z",
  "eventType": "USER_CREATED"
}

// UserUpdatedEvent
{
  "userId": 123,
  "firstName": "John",
  "lastName": "Smith",
  "email": "john.smith@example.com",
  "phoneNumber": "+1234567890",
  "eventType": "USER_UPDATED"
}
```

## Implementation Status

### ✅ Completed
1. **Auth-Service**
   - ✅ User registration endpoint (`POST /api/auth/register`)
   - ✅ User login endpoint (`POST /api/auth/login`)
   - ✅ Token validation endpoints
   - ✅ Kafka event publisher (`UserEventPublisher`)
   - ✅ UserCreatedEvent publishing on registration
   - ✅ Kafka dependency added to pom.xml

2. **Main-Service**
   - ✅ UserProfile entity created
   - ✅ UserProfileRepository created
   - ✅ Kafka event consumer (`UserEventConsumer`)
   - ✅ UserCreatedEvent handler
   - ✅ UserUpdatedEvent handler

3. **Gateway-Service**
   - ✅ Authentication filter implemented
   - ✅ Feign client integration
   - ✅ User context headers (X-User-Id, X-User-Email, X-User-Roles)

### 🚧 TODO - Next Steps

#### 1. Update Entity Relationships (CRITICAL)
```java
// OLD (main-service)
@Entity
public class Post {
    @ManyToOne
    @JoinColumn(name = "author_id")
    private AppUser author;  // ❌ Remove this
}

// NEW (main-service)
@Entity
public class Post {
    @Column(name = "author_id")
    private Long authorId;  // ✅ Use this instead

    @Transient
    private UserProfile author;  // ✅ Populated from UserProfileRepository
}
```

#### 2. Update Services to Use UserProfile
```java
// PostService example
public PostDTO getPost(Long postId) {
    Post post = postRepository.findById(postId);

    // Fetch user from UserProfileRepository
    UserProfile author = userProfileRepository.findById(post.getAuthorId())
        .orElseThrow(() -> new NotFoundException("User not found"));

    return mapToDTO(post, author);
}
```

#### 3. Migrate Existing Users
Create a one-time migration service:
```java
@Service
public class UserMigrationService {
    // 1. Copy users from AppUser (main_db) to User (auth_db)
    // 2. Publish UserCreatedEvent for each user
    // 3. Main-service will create UserProfile records
    // 4. Verify all users migrated successfully
}
```

#### 4. Update Profile Entity
```java
// OLD
@Entity
public class Profile {
    @OneToOne
    private AppUser user;  // ❌ Remove
}

// NEW
@Entity
public class Profile {
    @Column(name = "user_id")
    private Long userId;  // ✅ Reference UserProfile
}
```

#### 5. Configuration Files

**Auth-Service (`application.properties`)**
```properties
# Kafka Configuration
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer=org.apache.kafka.common.serialization.StringSerializer

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/auth_db
spring.datasource.username=postgres
spring.datasource.password=password
```

**Main-Service (`application.properties`)**
```properties
# Kafka Configuration
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=main-service-group
spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.consumer.value-deserializer=org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.consumer.auto-offset-reset=earliest

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/main_db
spring.datasource.username=postgres
spring.datasource.password=password
```

## API Endpoints

### Auth-Service (Port: 8087)
| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/auth/register` | POST | Register new user |
| `/api/auth/login` | POST | User login |
| `/api/auth/validate` | POST | Validate token (query param) |
| `/api/auth/validate-header` | POST | Validate token (header) |
| `/api/auth/logout` | POST | Logout current device |
| `/api/auth/logout-all` | POST | Logout all devices |

### Main-Service (Port: 8083)
| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/posts` | GET | Get all posts |
| `/api/posts/{id}` | GET | Get post by ID |
| `/api/posts` | POST | Create post |
| `/api/comments` | POST | Create comment |
| `/api/profiles/{userId}` | GET | Get user profile |

## Testing the System

### 1. Register a New User
```bash
curl -X POST http://localhost:8087/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@example.com",
    "password": "password123",
    "phoneNumber": "+1234567890"
  }'
```

**Expected Result:**
- User created in `auth_db.users`
- JWT tokens returned
- UserCreatedEvent published to Kafka
- UserProfile created in `main_db.user_profiles`

### 2. Login
```bash
curl -X POST http://localhost:8087/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "password123"
  }'
```

### 3. Create Post (via Gateway)
```bash
curl -X POST http://localhost:8080/api/posts \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "content": "Hello from microservices!"
  }'
```

**Gateway Flow:**
1. Gateway validates token via auth-service
2. Gateway adds headers: X-User-Id, X-User-Email, X-User-Roles
3. Main-service receives request with user context
4. Main-service creates post with authorId from X-User-Id header

## Kafka Topics

| Topic | Producer | Consumer | Events |
|-------|----------|----------|--------|
| `user-events` | auth-service | main-service | UserCreatedEvent, UserUpdatedEvent |

## Benefits of This Architecture

1. **Separation of Concerns**: Auth logic separated from business logic
2. **Independent Scaling**: Scale auth-service and main-service independently
3. **Service Resilience**: Main-service works even if auth-service is down (cached data)
4. **Security**: Password/credentials only in auth-service
5. **Data Ownership**: Each service owns its data
6. **Event-Driven**: Loose coupling via Kafka events
7. **Eventual Consistency**: UserProfile eventually consistent with User

## Migration Strategy

### Phase 1: Setup (DONE ✅)
- [x] Create UserProfile entity
- [x] Create Kafka events
- [x] Implement event publisher (auth-service)
- [x] Implement event consumer (main-service)

### Phase 2: Update Entities (TODO 🚧)
- [ ] Update Post entity to use userId reference
- [ ] Update Comment entity to use userId reference
- [ ] Update Profile entity to use userId reference
- [ ] Remove AppUser foreign keys

### Phase 3: Update Services (TODO 🚧)
- [ ] Update PostService to fetch user from UserProfileRepository
- [ ] Update CommentService to fetch user from UserProfileRepository
- [ ] Update ProfileService to use UserProfile

### Phase 4: Data Migration (TODO 🚧)
- [ ] Create migration script
- [ ] Copy users from main_db to auth_db
- [ ] Publish events for existing users
- [ ] Verify UserProfiles created

### Phase 5: Cleanup (TODO 🚧)
- [ ] Remove AppUser entity from main-service
- [ ] Remove old authentication code from main-service
- [ ] Update frontend to use auth-service endpoints

## Monitoring & Observability

### Key Metrics to Track
1. **Event Lag**: Time between UserCreatedEvent published and UserProfile created
2. **Sync Failures**: Failed event processing count
3. **Data Consistency**: User count in auth_db vs user_profiles count in main_db

### Logging
```java
// Auth-Service
log.info("Published UserCreatedEvent for userId: {}", userId);

// Main-Service
log.info("Created UserProfile for userId: {}", userId);
log.error("Failed to process user event: {}", error);
```

## Rollback Plan

If issues occur:
1. Stop publishing events (disable Kafka publisher)
2. Revert to AppUser in main-service
3. Re-enable old authentication endpoints
4. Debug issues and retry

## Future Enhancements

1. **Redis Caching**: Cache UserProfile data in Redis
2. **Event Replay**: Ability to replay events for data recovery
3. **CQRS**: Separate read/write models
4. **Saga Pattern**: Distributed transactions
5. **Service Mesh**: Istio for advanced routing/security