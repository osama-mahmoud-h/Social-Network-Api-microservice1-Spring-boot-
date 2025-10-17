# WebSocket Authentication Usage Guide

## Overview

The chat-service is now secured by the auth-service. When a client establishes a WebSocket connection, the JWT token (from cookie or Authorization header) is validated, and user information is stored in the WebSocket session attributes.

## Stored User Information

During the WebSocket handshake, the following information is extracted and stored:

- `userId` (Long) - The authenticated user's ID
- `email` (String) - The user's email address
- `roles` (Set<String>) - The user's roles (e.g., ROLE_USER, ROLE_ADMIN)
- `token` (String) - The original JWT token

## How to Access User Data

### Method 1: Using WebSocketAuthenticationHelper (Recommended)

The `WebSocketAuthenticationHelper` utility class provides clean, reusable methods:

```java
@MessageMapping("/some/endpoint")
public void handleMessage(
        @Payload SomeDto payload,
        SimpMessageHeaderAccessor headerAccessor) {

    // Get user ID
    Long userId = WebSocketAuthenticationHelper.getUserId(headerAccessor);

    // Get user email
    String email = WebSocketAuthenticationHelper.getEmail(headerAccessor);

    // Get user roles
    Set<String> roles = WebSocketAuthenticationHelper.getRoles(headerAccessor);

    // Get JWT token
    String token = WebSocketAuthenticationHelper.getToken(headerAccessor);

    // Check if user has a specific role
    boolean isAdmin = WebSocketAuthenticationHelper.hasRole(headerAccessor, "ROLE_ADMIN");

    // Verify sender ID matches authenticated user (prevents impersonation)
    boolean isValidSender = WebSocketAuthenticationHelper.verifySender(headerAccessor, payload.getSenderId());
}
```

### Method 2: Direct Access

You can also access the session attributes directly:

```java
@MessageMapping("/some/endpoint")
public void handleMessage(
        @Payload SomeDto payload,
        SimpMessageHeaderAccessor headerAccessor) {

    Long userId = (Long) headerAccessor.getSessionAttributes().get("userId");
    String email = (String) headerAccessor.getSessionAttributes().get("email");

    @SuppressWarnings("unchecked")
    Set<String> roles = (Set<String>) headerAccessor.getSessionAttributes().get("roles");

    String token = (String) headerAccessor.getSessionAttributes().get("token");
}
```

## Security Best Practices

### 1. Always Verify Sender Identity

When a client sends a message with a sender ID, always verify it matches the authenticated user:

```java
@MessageMapping("/message.send")
public void sendMessage(
        @Payload MessageDto message,
        SimpMessageHeaderAccessor headerAccessor) {

    // Verify the sender ID matches the authenticated user
    if (!WebSocketAuthenticationHelper.verifySender(headerAccessor, message.getSenderId())) {
        log.warn("User impersonation attempt detected");
        return; // Reject the message
    }

    // Process the message...
}
```

### 2. Use Authenticated User ID Instead of Client-Provided ID

Always use the authenticated user ID from the session instead of trusting client input:

```java
@MessageMapping("/message.send")
public void sendMessage(
        @Payload MessageDto message,
        SimpMessageHeaderAccessor headerAccessor) {

    // Get the REAL authenticated user ID
    Long authenticatedUserId = WebSocketAuthenticationHelper.getUserId(headerAccessor);

    // Use authenticatedUserId for all operations, ignore client-provided sender ID
    messageService.saveMessage(authenticatedUserId, message);
}
```

### 3. Check Roles for Authorization

For admin-only or role-restricted operations:

```java
@MessageMapping("/admin/broadcast")
public void broadcastMessage(
        @Payload BroadcastDto message,
        SimpMessageHeaderAccessor headerAccessor) {

    // Check if user has admin role
    if (!WebSocketAuthenticationHelper.hasRole(headerAccessor, "ROLE_ADMIN")) {
        log.warn("Unauthorized access attempt to admin endpoint");
        return; // Reject the request
    }

    // Process admin operation...
}
```

### 4. Log User Actions

Always log important user actions with their authenticated identity:

```java
@MessageMapping("/message.send")
public void sendMessage(
        @Payload MessageDto message,
        SimpMessageHeaderAccessor headerAccessor) {

    Long userId = WebSocketAuthenticationHelper.getUserId(headerAccessor);
    String email = WebSocketAuthenticationHelper.getEmail(headerAccessor);

    log.info("User {} (ID: {}) sending message to {}", email, userId, message.getReceiverId());

    // Process message...
}
```

## Example: Complete Message Handler

Here's a complete example showing all security best practices:

```java
@MessageMapping("/message.sendPrivate")
public void sendPrivateMessage(
        @Payload PrivateMessageDto message,
        SimpMessageHeaderAccessor headerAccessor) {

    // 1. Extract authenticated user info
    Long authenticatedUserId = WebSocketAuthenticationHelper.getUserId(headerAccessor);
    String email = WebSocketAuthenticationHelper.getEmail(headerAccessor);

    // 2. Verify sender identity (prevent impersonation)
    if (!WebSocketAuthenticationHelper.verifySender(headerAccessor, message.getSenderId())) {
        log.warn("User {} attempted to impersonate user {}",
                authenticatedUserId, message.getSenderId());
        return;
    }

    // 3. Log the action
    log.info("User {} (ID: {}) sending private message to user {}",
            email, authenticatedUserId, message.getReceiverId());

    // 4. Process the message using the authenticated user ID
    ChatMessage savedMessage = messageService.savePrivateMessage(
            authenticatedUserId,  // Use authenticated ID, not client-provided
            message.getReceiverId(),
            message.getContent()
    );

    // 5. Send to recipient
    simpMessagingTemplate.convertAndSendToUser(
            message.getReceiverId(),
            "/queue/private",
            savedMessage
    );
}
```

## Token Authentication Flow

```
Client                    Chat-Service                Auth-Service
  |                            |                            |
  |-- WebSocket Connect ------>|                            |
  |    (with JWT cookie)       |                            |
  |                            |-- Validate Token --------->|
  |                            |                            |
  |                            |<-- User Info (valid) ------|
  |<-- Connection Accepted ----|                            |
  |    (user info stored)      |                            |
  |                            |                            |
  |-- Send Message ----------->|                            |
  |                            |                            |
  |                            | Extract user from session  |
  |                            | Verify sender identity     |
  |                            | Process message            |
  |                            |                            |
```

## Testing

You can test WebSocket authentication using browser JavaScript:

```javascript
// Connect with JWT token in cookie
const socket = new SockJS('http://localhost:8086/ws');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function(frame) {
    console.log('Connected: ' + frame);

    // Send a message
    stompClient.send("/app/message.sendPrivate", {}, JSON.stringify({
        senderId: "13",      // Will be verified against authenticated user
        receiverId: "14",
        content: "Hello!"
    }));
});
```

Or using Authorization header:

```javascript
const socket = new SockJS('http://localhost:8086/ws');
const stompClient = Stomp.over(socket);

// Send Authorization header during connection
stompClient.connect(
    { Authorization: 'Bearer ' + jwtToken },
    function(frame) {
        console.log('Connected: ' + frame);
    }
);
```

## Common Issues

### Issue: User data is null in handler

**Cause:** The WebSocket connection was not properly authenticated.

**Solution:** Ensure the JWT token is sent during the WebSocket handshake (in cookie or Authorization header).

### Issue: Sender verification always fails

**Cause:** The sender ID format doesn't match (String vs Long).

**Solution:** Ensure consistent ID types. The `verifySender()` method converts to String for comparison.

### Issue: Token validation fails

**Cause:** Token is expired, invalid, or auth-service is not running.

**Solution:**
- Check that auth-service is running on port 8087
- Verify the token is valid and not expired
- Check logs for detailed error messages

## Configuration

Ensure these settings are configured in `application.properties`:

```properties
# Auth Service URL
auth-service.url=http://localhost:8087

# Enable Feign clients
spring.cloud.openfeign.enabled=true
```

## Summary

1. ✅ Token is validated during WebSocket handshake via auth-service
2. ✅ User info (userId, email, roles) is stored in session attributes
3. ✅ Use `WebSocketAuthenticationHelper` to access user data
4. ✅ Always verify sender identity to prevent impersonation
5. ✅ Use authenticated user ID, never trust client-provided IDs
6. ✅ Check roles for authorization
7. ✅ Log all important user actions