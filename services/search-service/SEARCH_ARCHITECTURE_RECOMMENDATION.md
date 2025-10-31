# Search Architecture Recommendation

## Recommended Approach: Hybrid (IDs + Basic Data)

### Implementation Strategy

#### 1. **Search Service Returns:**
- **PostIds** (for subsequent enrichment)
- **Basic searchable data** (content, createdAt, author name)
- **Rank/score** (Elasticsearch relevance score)

#### 2. **Client/API Gateway:**
- Takes postIds from search-service
- Calls main-service to enrich with:
  - Personalized data (myReactionType)
  - Aggregated counts (commentsCount, reactionsCount)
  - Files (if needed)

### Example Flow:

```
1. User searches "microservices"
   ↓
2. search-service returns:
   {
     "results": [
       {"postId": 123, "content": "...", "author": {...}, "score": 0.95},
       {"postId": 456, "content": "...", "author": {...}, "score": 0.87}
     ]
   }
   ↓
3. Client calls main-service: POST /api/v1/posts/enrich
   Body: {"postIds": [123, 456], "userId": currentUser}
   ↓
4. main-service returns enriched data:
   {
     "123": {"commentsCount": 15, "reactionsCount": 42, "myReactionType": "LIKE", "files": [...]},
     "456": {"commentsCount": 8, "reactionsCount": 23, "myReactionType": null, "files": [...]}
   }
   ↓
5. Client merges data and displays
```

## Implementation Options:

### Option A: Return IDs Only (Simplest)
**Best for:** When you need full post details anyway

```java
// search-service returns only IDs
{
  "postIds": [123, 456, 789],
  "total": 3
}
```

Then client fetches from main-service:
```java
GET /api/v1/posts/batch?ids=123,456,789&userId=currentUser
```

### Option B: Return IDs + Search Snippets (Better UX)
**Best for:** Search results page with snippets

```java
// search-service returns IDs + minimal display data
{
  "results": [
    {
      "postId": 123,
      "snippet": "Learn about microservices architecture...",
      "author": {"name": "John Doe", "avatar": "..."},
      "createdAt": "2024-01-15T10:30:00Z",
      "score": 0.95
    }
  ]
}
```

Client can display results immediately, then lazy-load counts/reactions on demand.

### Option C: Return IDs + Call Main-Service in Search-Service
**Best for:** Simplified client logic (more backend work)

Search-service internally calls main-service to enrich data before returning to client.

## What to Store in Elasticsearch:

### ✅ Store (Searchable + Display):
- `postId` (for fetching full data)
- `content` (searchable)
- `authorId`, `author.name`, `author.avatar` (display)
- `createdAt`, `updatedAt` (filtering, sorting)
- `tags` (if you add tags later)

### ❌ Don't Store:
- `commentsCount`, `reactionsCount` (changes frequently)
- `myReactionType` (personalized)
- `files` (rarely searched, better fetched from main-service)
- `comments` (separate index if needed)

## Recommended Implementation:

### Step 1: Update Search Response DTO
```java
@Builder
@Data
public class PostSearchResultDto {
    private Long postId;           // For fetching full data
    private String contentSnippet; // Highlighted search snippet
    private AppUserResponseDto author;
    private Instant createdAt;
    private Float relevanceScore;  // Elasticsearch score
}
```

### Step 2: Create Enrichment Endpoint in Main-Service
```java
@PostMapping("/api/v1/posts/enrich")
public Map<Long, PostEnrichmentDto> enrichPosts(
    @RequestBody Set<Long> postIds,
    @RequestHeader("X-User-Id") Long userId
) {
    // Fetch counts, reactions, files for given postIds
    // Return map: postId -> enrichment data
}
```

### Step 3: Client Merges Data
```javascript
// 1. Search
const searchResults = await searchService.search("microservices");

// 2. Enrich
const postIds = searchResults.map(r => r.postId);
const enrichments = await mainService.enrichPosts(postIds, currentUserId);

// 3. Merge
const fullPosts = searchResults.map(result => ({
    ...result,
    ...enrichments[result.postId]
}));
```

## Recommendation Summary:

For your use case, I recommend **Option B (IDs + Search Snippets)**:

1. **Search-service** returns postIds + basic display data (fast from ES)
2. **Client** displays results immediately
3. **Client** lazy-loads enrichment data (counts, reactions) when needed
4. **Main-service** provides enrichment endpoint for personalized data

This gives you:
- ⚡ Fast search results
- 📊 Fresh aggregated data
- 👤 Personalized content (reactions)
- 🔄 Easy to maintain (minimal sync overhead)