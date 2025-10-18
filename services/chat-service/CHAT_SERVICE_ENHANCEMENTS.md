# Chat Service Enhancements Documentation

## Overview
This document describes the enhancements made to the chat-service to support Redis caching, private chat functionality, and integration with main-service for friends list retrieval.

## Table of Contents
1. [Redis Integration](#redis-integration)
2. [Private Chat Functionality](#private-chat-functionality)
3. [Friends List Integration](#friends-list-integration)
4. [API Endpoints](#api-endpoints)
5. [Configuration](#configuration)
6. [Frontend Integration Guide](#frontend-integration-guide)

---

## 1. Redis Integration

### Overview
Redis has been integrated as a caching layer for chat messages, providing faster message retrieval and reduced database load.

### Files Added/Modified

#### Dependencies (`pom.xml`)
```xml
<!-- Redis for caching and message storage -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>

<!-- Jedis client for Redis -->
<dependency>
    <groupId>redis.clients</groupId>
    <artifactId>jedis</artifactId>
</dependency>
```

#### Configuration Files

**`application.properties`**
```properties
# Redis Configuration
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.password=
spring.data.redis.timeout=60000
spring.cache.type=redis
spring.cache.redis.time-to-live=600000
```

#### New Classes

1. **`RedisConfig.java`** - `/config/RedisConfig.java`
   - Configures Redis connection factory
   - Sets up RedisTemplate with JSON serialization
   - Enables caching

2. **`RedisChatMessageRepository.java`** - `/repository/RedisChatMessageRepository.java`
   - Handles Redis operations for chat messages
   - Methods:
     - `saveMessage(ChatMessage)` - Save message to Redis with 24h TTL
     - `getMessagesByChatId(String)` - Retrieve all messages for a chat
     - `getRecentMessages(String, int)` - Get last N messages
     - `deleteChatMessages(String)` - Delete all messages for a chat
     - `chatExists(String)` - Check if chat exists in cache
     - `getMessageCount(String)` - Get message count for a chat
     - `cachePrivateChatUsers(String, String, String)` - Cache private chat participants
     - `getPrivateChatUsers(String)` - Retrieve private chat participants

#### Modified Classes

**`ChatMessageServiceImpl.java`**
- Now uses both MongoDB (persistence) and Redis (caching)
- `saveMessage()`: Saves to both MongoDB and Redis
- `getConversations()`: Reads from Redis first, falls back to MongoDB if not cached

### Benefits
- **Performance**: Faster message retrieval from Redis cache
- **Scalability**: Reduced database load
- **TTL Support**: Automatic cleanup of old messages after 24 hours
- **Hybrid Storage**: MongoDB for persistence, Redis for speed

---

## 2. Private Chat Functionality

### Overview
Private chat functionality was already implemented in the codebase. The enhancements improve it with Redis caching and better integration.

### Existing Features
- **Direct messaging** between two users via WebSocket
- **Chat room management** with unique chat IDs
- **Message persistence** in MongoDB
- **Real-time delivery** via STOMP protocol

### WebSocket Endpoints for Private Chat

#### Send Private Message
- **Destination**: `/app/message.sendPrivateMessage`
- **Subscription**: `/user/{receiverId}/private`
- **Request DTO**: `NewPrivateChatMessageRequestDto`
```json
{
  "chatId": "1_2",
  "senderId": "1",
  "receiverId": "2",
  "content": "Hello!",
  "messageType": "TEXT"
}
```

#### Get Conversation History
- **HTTP GET**: `/messages/{senderId}/{receiverId}`
- **Response**: List of `ChatMessageResponseDto`

### Chat Room ID Generation
- Format: `sorted(userId1, userId2)` joined by underscore
- Example: User 1 and User 2 → `1_2`
- Ensures same chat ID regardless of who initiates

---

## 3. Friends List Integration

### Overview
Chat-service now communicates asynchronously with main-service to fetch the user's friends list for initiating private chats.

### Architecture

```
Frontend → chat-service → main-service
                ↓
          (Feign Client)
                ↓
         Friends Endpoint
```

### New Components

#### Main-Service Changes

**1. FriendshipService Interface** - Added method:
```java
Page<AppUserResponseDto> getFriendsPaginated(AppUser currentUser, int page, int size);
```

**2. FriendshipServiceImpl** - Implementation:
- Fetches friends from database with pagination
- Returns Spring Data `Page` object with metadata

**3. FriendshipController** - New endpoint:
```
GET /api/v1/friendship/get-friends-paginated?page=0&size=20
Authorization: Bearer {token}

Response:
{
  "success": true,
  "message": "Friends retrieved",
  "data": {
    "content": [
      {
        "userId": 2,
        "username": "John Doe",
        "email": "john@example.com",
        "imageUrl": null
      }
    ],
    "pageNumber": 0,
    "pageSize": 20,
    "totalElements": 50,
    "totalPages": 3,
    "last": false,
    "first": true
  }
}
```

#### Chat-Service Changes

**1. New DTOs**
- `AppUserForChatDto` - Represents friend data
- `MyApiResponse<T>` - Wrapper for API responses
- `PageResponse<T>` - Pagination metadata

**2. Feign Client** - `MainServiceClient.java`
```java
@FeignClient(name = "main-service", url = "${main-service.url}")
public interface MainServiceClient {
    @GetMapping("/api/v1/friendship/get-friends-paginated")
    MyApiResponse<Page<AppUserForChatDto>> getFriendsPaginated(
        @RequestHeader("Authorization") String token,
        @RequestParam int page,
        @RequestParam int size
    );
}
```

**3. FriendsService** - Business logic layer
- Calls main-service via Feign
- Handles errors and logging

**4. FriendsController** - REST endpoint
```
GET /api/friends/paginated?page=0&size=20
Authorization: Bearer {token}
```

### Configuration
**`application.properties`** (chat-service)
```properties
# Main Service
main-service.url=http://localhost:8081
```

---

## 4. API Endpoints

### Chat Service Endpoints

#### 1. Get Friends for Chat (Paginated)
```http
GET /api/friends/paginated?page=0&size=20
Authorization: Bearer {JWT_TOKEN}

Response: 200 OK
{
  "content": [
    {
      "userId": 2,
      "username": "Jane Smith",
      "email": "jane@example.com",
      "imageUrl": "https://example.com/avatar.jpg"
    }
  ],
  "pageNumber": 0,
  "pageSize": 20,
  "totalElements": 45,
  "totalPages": 3,
  "last": false,
  "first": true
}
```

#### 2. Send Private Message (WebSocket)
```javascript
// Connect to WebSocket
const socket = new SockJS('http://localhost:8086/ws');
const stompClient = Stomp.over(socket);

// Send private message
stompClient.send('/app/message.sendPrivateMessage', {}, JSON.stringify({
  chatId: '1_2',
  senderId: '1',
  receiverId: '2',
  content: 'Hello!',
  messageType: 'TEXT'
}));

// Subscribe to private messages
stompClient.subscribe('/user/1/private', (message) => {
  console.log('Received:', JSON.parse(message.body));
});
```

#### 3. Get Conversation History
```http
GET /messages/{senderId}/{receiverId}

Response: 200 OK
[
  {
    "messageId": "abc123",
    "chatId": "1_2",
    "senderId": "1",
    "receiverId": "2",
    "content": "Hello!",
    "messageType": "TEXT",
    "isSeen": false,
    "timestamp": "2025-10-17T10:30:00Z"
  }
]
```

### Main Service Endpoints

#### Get Friends Paginated
```http
GET /api/v1/friendship/get-friends-paginated?page=0&size=20
Authorization: Bearer {JWT_TOKEN}

Response: 200 OK
{
  "success": true,
  "message": "Friends retrieved",
  "data": { ... }
}
```

---

## 5. Configuration

### Prerequisites
1. **Redis Server**: Install and run Redis on `localhost:6379`
   ```bash
   # Install Redis (Ubuntu/Debian)
   sudo apt-get install redis-server

   # Start Redis
   redis-server

   # Verify Redis is running
   redis-cli ping
   # Should return: PONG
   ```

2. **MongoDB**: Running on `localhost:27017`

3. **PostgreSQL**: Running on `localhost:5432` (for main-service)

### Service Ports
- **chat-service**: 8086
- **main-service**: 8081
- **auth-service**: 8087

### Environment Variables
Ensure these are set in your main-service `.env` file:
```properties
PORT=8081
DATABASE_NAME=social_network
DATABASE_USERNAME=your_username
DATABASE_PASSWORD=your_password
JWT_SECRET_KEY=your_secret
JWT_EXPIRE_IN_MS=86400000
JWT_SINGING_KEY=your_signing_key
```

---

## 6. Frontend Integration Guide

### React Client Setup

Based on your React client at `/home/osama/HOME/OSAMA/React/chat_service`, here's how to integrate the new features:

#### 1. Install Dependencies
```bash
npm install sockjs-client stompjs axios
```

#### 2. Friends List Component

**`src/components/FriendsList.jsx`**
```javascript
import React, { useState, useEffect } from 'react';
import axios from 'axios';

const FriendsList = ({ token, onSelectFriend }) => {
  const [friends, setFriends] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    fetchFriends();
  }, [page]);

  const fetchFriends = async () => {
    setLoading(true);
    try {
      const response = await axios.get(
        `http://localhost:8086/api/friends/paginated?page=${page}&size=20`,
        {
          headers: { Authorization: `Bearer ${token}` }
        }
      );

      setFriends(response.data.content);
      setTotalPages(response.data.totalPages);
    } catch (error) {
      console.error('Error fetching friends:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="friends-list">
      <h2>Friends</h2>
      {loading ? (
        <p>Loading...</p>
      ) : (
        <>
          <ul>
            {friends.map(friend => (
              <li key={friend.userId} onClick={() => onSelectFriend(friend)}>
                <img src={friend.imageUrl || '/default-avatar.png'} alt={friend.username} />
                <span>{friend.username}</span>
              </li>
            ))}
          </ul>

          {/* Pagination */}
          <div className="pagination">
            <button onClick={() => setPage(p => Math.max(0, p - 1))} disabled={page === 0}>
              Previous
            </button>
            <span>Page {page + 1} of {totalPages}</span>
            <button onClick={() => setPage(p => p + 1)} disabled={page >= totalPages - 1}>
              Next
            </button>
          </div>
        </>
      )}
    </div>
  );
};

export default FriendsList;
```

#### 3. Private Chat Component

**`src/components/PrivateChat.jsx`**
```javascript
import React, { useState, useEffect, useRef } from 'react';
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';
import axios from 'axios';

const PrivateChat = ({ currentUserId, selectedFriend, token }) => {
  const [messages, setMessages] = useState([]);
  const [newMessage, setNewMessage] = useState('');
  const [connected, setConnected] = useState(false);
  const stompClientRef = useRef(null);

  // Generate chat ID (consistent ordering)
  const getChatId = (user1, user2) => {
    const ids = [user1, user2].sort((a, b) => a - b);
    return `${ids[0]}_${ids[1]}`;
  };

  const chatId = getChatId(currentUserId, selectedFriend.userId);

  useEffect(() => {
    // Load message history
    loadMessages();

    // Connect to WebSocket
    connectWebSocket();

    return () => {
      if (stompClientRef.current) {
        stompClientRef.current.disconnect();
      }
    };
  }, [selectedFriend]);

  const loadMessages = async () => {
    try {
      const response = await axios.get(
        `http://localhost:8086/messages/${currentUserId}/${selectedFriend.userId}`
      );
      setMessages(response.data);
    } catch (error) {
      console.error('Error loading messages:', error);
    }
  };

  const connectWebSocket = () => {
    const socket = new SockJS('http://localhost:8086/ws');
    const client = Stomp.over(socket);

    client.connect(
      { Authorization: `Bearer ${token}` },
      () => {
        setConnected(true);

        // Subscribe to private messages
        client.subscribe(`/user/${currentUserId}/private`, (message) => {
          const receivedMessage = JSON.parse(message.body);
          if (receivedMessage.senderId === selectedFriend.userId.toString()) {
            setMessages(prev => [...prev, receivedMessage]);
          }
        });
      },
      (error) => {
        console.error('WebSocket connection error:', error);
        setConnected(false);
      }
    );

    stompClientRef.current = client;
  };

  const sendMessage = () => {
    if (!newMessage.trim() || !connected) return;

    const messagePayload = {
      chatId: chatId,
      senderId: currentUserId.toString(),
      receiverId: selectedFriend.userId.toString(),
      content: newMessage,
      messageType: 'TEXT'
    };

    stompClientRef.current.send(
      '/app/message.sendPrivateMessage',
      {},
      JSON.stringify(messagePayload)
    );

    // Add message to local state
    setMessages(prev => [...prev, {
      ...messagePayload,
      timestamp: new Date().toISOString(),
      isSeen: false
    }]);

    setNewMessage('');
  };

  return (
    <div className="private-chat">
      <div className="chat-header">
        <h3>Chat with {selectedFriend.username}</h3>
        <span className={connected ? 'status-connected' : 'status-disconnected'}>
          {connected ? '● Connected' : '○ Disconnected'}
        </span>
      </div>

      <div className="messages-container">
        {messages.map((msg, index) => (
          <div
            key={index}
            className={`message ${msg.senderId === currentUserId.toString() ? 'sent' : 'received'}`}
          >
            <div className="message-content">{msg.content}</div>
            <div className="message-time">
              {new Date(msg.timestamp).toLocaleTimeString()}
            </div>
          </div>
        ))}
      </div>

      <div className="message-input">
        <input
          type="text"
          value={newMessage}
          onChange={(e) => setNewMessage(e.target.value)}
          onKeyPress={(e) => e.key === 'Enter' && sendMessage()}
          placeholder="Type a message..."
          disabled={!connected}
        />
        <button onClick={sendMessage} disabled={!connected}>
          Send
        </button>
      </div>
    </div>
  );
};

export default PrivateChat;
```

#### 4. Main Chat App

**`src/App.jsx`**
```javascript
import React, { useState } from 'react';
import FriendsList from './components/FriendsList';
import PrivateChat from './components/PrivateChat';
import './App.css';

function App() {
  const [token, setToken] = useState(localStorage.getItem('token'));
  const [currentUserId, setCurrentUserId] = useState(localStorage.getItem('userId'));
  const [selectedFriend, setSelectedFriend] = useState(null);

  return (
    <div className="app">
      <div className="sidebar">
        <FriendsList
          token={token}
          onSelectFriend={setSelectedFriend}
        />
      </div>

      <div className="main-content">
        {selectedFriend ? (
          <PrivateChat
            currentUserId={parseInt(currentUserId)}
            selectedFriend={selectedFriend}
            token={token}
          />
        ) : (
          <div className="no-chat-selected">
            <p>Select a friend to start chatting</p>
          </div>
        )}
      </div>
    </div>
  );
}

export default App;
```

#### 5. Styling Example

**`src/App.css`**
```css
.app {
  display: flex;
  height: 100vh;
}

.sidebar {
  width: 300px;
  border-right: 1px solid #ddd;
  overflow-y: auto;
}

.friends-list ul {
  list-style: none;
  padding: 0;
}

.friends-list li {
  display: flex;
  align-items: center;
  padding: 15px;
  cursor: pointer;
  border-bottom: 1px solid #eee;
}

.friends-list li:hover {
  background: #f5f5f5;
}

.friends-list img {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin-right: 10px;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.private-chat {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.chat-header {
  padding: 15px;
  border-bottom: 1px solid #ddd;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.messages-container {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}

.message {
  margin-bottom: 15px;
  max-width: 70%;
}

.message.sent {
  margin-left: auto;
  text-align: right;
}

.message.received {
  margin-right: auto;
}

.message-content {
  background: #007bff;
  color: white;
  padding: 10px 15px;
  border-radius: 15px;
  display: inline-block;
}

.message.received .message-content {
  background: #e9ecef;
  color: #000;
}

.message-time {
  font-size: 0.75em;
  color: #999;
  margin-top: 5px;
}

.message-input {
  display: flex;
  padding: 15px;
  border-top: 1px solid #ddd;
}

.message-input input {
  flex: 1;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 20px;
  margin-right: 10px;
}

.message-input button {
  padding: 10px 20px;
  background: #007bff;
  color: white;
  border: none;
  border-radius: 20px;
  cursor: pointer;
}

.pagination {
  display: flex;
  justify-content: space-between;
  padding: 15px;
}

.status-connected {
  color: green;
}

.status-disconnected {
  color: red;
}
```

---

## Testing

### 1. Test Redis Connection
```bash
# From chat-service directory
redis-cli
> PING
PONG
> KEYS chat:messages:*
```

### 2. Test Friends Endpoint
```bash
curl -X GET "http://localhost:8086/api/friends/paginated?page=0&size=20" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 3. Test Private Chat
1. Login two users
2. Connect both to WebSocket
3. Send message from User 1 to User 2
4. Verify User 2 receives message
5. Check Redis cache: `redis-cli KEYS chat:messages:*`

---

## Troubleshooting

### Redis Connection Issues
```bash
# Check if Redis is running
sudo systemctl status redis

# Start Redis
sudo systemctl start redis

# Check logs
tail -f /var/log/redis/redis-server.log
```

### Feign Client Errors
- Verify main-service is running on port 8081
- Check `main-service.url` in application.properties
- Ensure JWT token is valid

### WebSocket Connection Issues
- Check CORS configuration
- Verify auth-service is validating tokens correctly
- Check browser console for errors

---

## Performance Considerations

1. **Redis TTL**: Messages expire after 24 hours. Adjust in `RedisChatMessageRepository.java` if needed.

2. **Pagination Size**: Default is 20 friends per page. Adjust based on your needs.

3. **Connection Pooling**: Redis uses Jedis with default pool settings. Configure in `RedisConfig.java` for production.

4. **MongoDB Indexes**: Ensure indexes exist on `chatId` field:
```javascript
db.chatMessages.createIndex({ chatId: 1 })
```

---

## Future Enhancements

1. **Message Read Receipts**: Track when messages are seen
2. **Typing Indicators**: Show when user is typing
3. **File Attachments**: Support images, videos, files
4. **Group Chats**: Extend to support multi-user chats
5. **Message Search**: Full-text search in chat history
6. **Push Notifications**: Notify users of new messages
7. **Message Encryption**: End-to-end encryption for privacy

---

## Contact & Support

For questions or issues:
- Check logs: `tail -f services/chat-service/logs/chat-service.log`
- Review ELK stack (if configured): `http://localhost:5601`
- Consult main documentation: `README.md`

---

## Summary of File Changes

### New Files
- `chat-service/src/main/java/semsem/chatservice/config/RedisConfig.java`
- `chat-service/src/main/java/semsem/chatservice/repository/RedisChatMessageRepository.java`
- `chat-service/src/main/java/semsem/chatservice/client/MainServiceClient.java`
- `chat-service/src/main/java/semsem/chatservice/service/FriendsService.java`
- `chat-service/src/main/java/semsem/chatservice/service/impl/FriendsServiceImpl.java`
- `chat-service/src/main/java/semsem/chatservice/controller/FriendsController.java`
- `chat-service/src/main/java/semsem/chatservice/dto/response/AppUserForChatDto.java`
- `chat-service/src/main/java/semsem/chatservice/dto/response/MyApiResponse.java`
- `chat-service/src/main/java/semsem/chatservice/dto/response/PageResponse.java`

### Modified Files
- `chat-service/pom.xml` - Added Redis dependencies
- `chat-service/src/main/resources/application.properties` - Added Redis & main-service config
- `chat-service/src/main/java/semsem/chatservice/service/impl/ChatMessageServiceImpl.java` - Integrated Redis
- `main-service/src/main/java/com/app/server/service/FriendshipService.java` - Added pagination method
- `main-service/src/main/java/com/app/server/service/impl/FriendshipServiceImpl.java` - Implemented pagination
- `main-service/src/main/java/com/app/server/controller/FriendshipController.java` - Added paginated endpoint