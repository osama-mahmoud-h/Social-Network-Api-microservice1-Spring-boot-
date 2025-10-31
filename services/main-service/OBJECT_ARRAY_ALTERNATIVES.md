# Clean Alternatives to Object[] in JPA Queries

## ❌ Problem: Object[] Approach (OLD CODE)

```java
// Repository
@Query(value = "SELECT u.* FROM user_profiles u ...", nativeQuery = true)
List<Object[]> findFriendSuggestions(@Param("userId") Long userId);

// Service
private AppUserResponseDto mapRawUserToResponseDto(Object[] row) {
    return new AppUserResponseDto(
        (Long) row[0],    // ❌ What is row[0]?
        (String) row[3] + " " + (String) row[4], // ❌ Fragile!
        (String) row[2],  // ❌ No IDE support
        null
    );
}
```

**Problems:**
- ❌ No compile-time safety
- ❌ No IDE autocomplete
- ❌ Runtime errors if query changes
- ❌ Hard to maintain
- ❌ Error-prone (wrong indices)

---

## ✅ Solution 1: Interface-Based Projection (IMPLEMENTED - BEST FOR NATIVE QUERIES)

### Benefits:
- ✅ Type-safe
- ✅ IDE autocomplete
- ✅ Minimal boilerplate
- ✅ Works with native queries
- ✅ Spring Data handles mapping automatically

### Implementation:

```java
// 1. Create projection interface
public interface UserSuggestionProjection {
    Long getUserId();
    String getEmail();
    String getFirstName();
    String getLastName();
    String getProfilePictureUrl();
}

// 2. Update repository to return projection
@Query(value = """
    SELECT u.user_id as userId, u.email as email,
           u.first_name as firstName, u.last_name as lastName,
           u.profile_picture_url as profilePictureUrl
    FROM user_profiles u ...
    """, nativeQuery = true)
List<UserSuggestionProjection> findFriendSuggestions(@Param("userId") Long userId);

// 3. Create mapper
@Component
public class UserProjectionMapper {
    public AppUserResponseDto toAppUserResponseDto(UserSuggestionProjection projection) {
        return new AppUserResponseDto(
            projection.getUserId(),      // ✅ Type-safe!
            projection.getFirstName() + " " + projection.getLastName(),
            projection.getEmail(),
            projection.getProfilePictureUrl()
        );
    }
}

// 4. Use in service
return friendshipServiceRepository.findFriendSuggestions(userId).stream()
        .map(userProjectionMapper::toAppUserResponseDto)  // ✅ Clean!
        .collect(Collectors.toSet());
```

**Key Rules:**
- Column alias in query (`as userId`) must match getter name (`getUserId()`)
- Spring automatically implements the interface at runtime
- No need to write implementation class

---

## ✅ Solution 2: DTO Constructor with JPQL (BEST FOR JPQL QUERIES)

### Benefits:
- ✅ Type-safe
- ✅ Direct mapping to DTO
- ✅ Single query
- ✅ Best performance

### Implementation:

```java
// 1. Create DTO with constructor
@Data
@AllArgsConstructor
public class UserSuggestionDto {
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String profilePictureUrl;
}

// 2. Use constructor in JPQL (NOT native SQL!)
@Query("""
    SELECT new com.app.server.dto.UserSuggestionDto(
        u.userId, u.firstName, u.lastName, u.email, u.profilePictureUrl
    )
    FROM UserProfile u
    JOIN Friendship f ON ...
    WHERE ...
    """)
List<UserSuggestionDto> findFriendSuggestions(@Param("userId") Long userId);
```

**⚠️ Limitation:** Only works with JPQL, NOT native SQL queries!

---

## ✅ Solution 3: Return Entity Directly (SIMPLEST WHEN POSSIBLE)

### Benefits:
- ✅ Simplest approach
- ✅ No mapping needed
- ✅ JPA handles everything

### Implementation:

```java
// Repository - return entity directly
@Query(value = "SELECT u.* FROM user_profiles u ...", nativeQuery = true)
List<UserProfile> findFriendSuggestions(@Param("userId") Long userId);

// Service - use existing mapper
return friendshipServiceRepository.findFriendSuggestions(userId).stream()
        .map(userMapper::mapToAppUserResponseDto)
        .collect(Collectors.toSet());
```

**When to use:**
- ✅ Query returns all/most entity columns
- ✅ You already have an entity-to-DTO mapper
- ❌ Avoid if query returns only few columns (fetches unnecessary data)

---

## ✅ Solution 4: JPA Tuple (TYPE-SAFE BUT VERBOSE)

### Benefits:
- ✅ Type-safe
- ✅ Works with native queries
- ✅ Access by column name

### Implementation:

```java
// Repository
@Query(value = """
    SELECT u.user_id as userId, u.email as email,
           u.first_name as firstName
    FROM user_profiles u ...
    """, nativeQuery = true)
List<Tuple> findFriendSuggestions(@Param("userId") Long userId);

// Service
return friendshipServiceRepository.findFriendSuggestions(userId).stream()
        .map(tuple -> new AppUserResponseDto(
            tuple.get("userId", Long.class),      // ✅ Type-safe
            tuple.get("firstName", String.class), // ✅ By name
            tuple.get("email", String.class),
            null
        ))
        .collect(Collectors.toSet());
```

**When to use:**
- One-off queries
- When you don't want to create projection interface

---

## ✅ Solution 5: @SqlResultSetMapping (MOST FLEXIBLE FOR COMPLEX NATIVE QUERIES)

### Benefits:
- ✅ Full control over mapping
- ✅ Works with complex native queries
- ✅ Can map to multiple entities

### Implementation:

```java
// 1. Define mapping on entity
@Entity
@SqlResultSetMapping(
    name = "UserSuggestionMapping",
    classes = @ConstructorResult(
        targetClass = AppUserResponseDto.class,
        columns = {
            @ColumnResult(name = "userId", type = Long.class),
            @ColumnResult(name = "email", type = String.class),
            @ColumnResult(name = "firstName", type = String.class),
            @ColumnResult(name = "lastName", type = String.class)
        }
    )
)
public class UserProfile { ... }

// 2. Use in repository
@Query(value = """
    SELECT u.user_id as userId, u.email as email,
           u.first_name as firstName, u.last_name as lastName
    FROM user_profiles u ...
    """, nativeQuery = true)
List<AppUserResponseDto> findFriendSuggestions(@Param("userId") Long userId);
```

**When to use:**
- Complex native queries
- Need to map to multiple entities
- Performance-critical queries

---

## 📊 Comparison Matrix

| Approach | Native SQL? | JPQL? | IDE Support | Boilerplate | Flexibility |
|----------|-------------|-------|-------------|-------------|-------------|
| **Object[]** (OLD) | ✅ | ✅ | ❌ | Low | High |
| **Interface Projection** ⭐ | ✅ | ✅ | ✅ | Low | Medium |
| **DTO Constructor** | ❌ | ✅ | ✅ | Low | Low |
| **Return Entity** | ✅ | ✅ | ✅ | None | Low |
| **JPA Tuple** | ✅ | ✅ | ⚠️ | Medium | Medium |
| **@SqlResultSetMapping** | ✅ | ✅ | ✅ | High | High |

---

## 🎯 Recommendations

### Use Interface Projection (Solution 1) when:
- ✅ Working with native SQL queries
- ✅ Query returns subset of columns
- ✅ Want type safety with minimal boilerplate
- **👉 This is what we implemented!**

### Use DTO Constructor (Solution 2) when:
- ✅ Working with JPQL (not native SQL)
- ✅ Want direct DTO mapping
- ✅ Performance is critical

### Use Return Entity (Solution 3) when:
- ✅ Query returns full entity
- ✅ You already have entity mappers
- ✅ Want simplest solution

### Use Tuple (Solution 4) when:
- ✅ One-off query
- ✅ Don't want to create projection interface

### Use @SqlResultSetMapping (Solution 5) when:
- ✅ Very complex native queries
- ✅ Need maximum flexibility
- ✅ Mapping to multiple entities

---

## 📝 Migration Checklist

If you have other methods returning `Object[]`, follow these steps:

1. **Identify all Object[] queries**
   ```bash
   grep -r "List<Object\[\]>" --include="*.java"
   ```

2. **For each query:**
   - [ ] Create projection interface (or reuse existing)
   - [ ] Update SELECT to include column aliases
   - [ ] Change return type to projection interface
   - [ ] Update service to use mapper
   - [ ] Remove old `mapRawUserToResponseDto` methods
   - [ ] Test the changes

3. **Benefits after migration:**
   - ✅ Type-safe codebase
   - ✅ IDE autocomplete everywhere
   - ✅ Easier to maintain
   - ✅ Fewer runtime errors
   - ✅ Better developer experience

---

## 🔍 Finding Other Object[] Usage

```bash
# Find all Object[] returns in repositories
grep -r "List<Object\[\]>" services/main-service/src/main/java/com/app/server/repository/

# Find all Object[] mapping methods
grep -r "Object\[\] row" services/main-service/src/main/java/com/app/server/
```

Would you like me to help migrate other `Object[]` queries in your codebase?