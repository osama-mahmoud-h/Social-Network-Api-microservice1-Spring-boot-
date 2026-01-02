# Secure File Serving & Processing Guide

This guide explains how to use the enhanced file serving system with security controls, large file streaming, and image processing capabilities.

## Table of Contents
1. [Architecture Overview](#architecture-overview)
2. [Security Features](#security-features)
3. [Large File Streaming (Range Requests)](#large-file-streaming-range-requests)
4. [Usage Examples](#usage-examples)
5. [File Processing](#file-processing)
6. [API Endpoints](#api-endpoints)

---

## Architecture Overview

### Components

```
┌─────────────────────┐
│   FilesController   │ ← HTTP Range Request Support
└──────────┬──────────┘
           │
           ├──→ FileAccessService ← Security Validation
           │    └──→ FileRepository (Access Control Queries)
           │
           └──→ FilesStorageService (MinioStorageServiceImpl)
                ├──→ load() - Full file download
                ├──→ loadRange() - Partial content (optimized)
                ├──→ getFileSize() - Metadata only
                └──→ fileExists() - Existence check
```

### Two Approaches for File Access

#### 1. **Direct Presigned URLs** (Current Default - Recommended for most use cases)
- **Flow**: Client → API → Get presigned URL → Client downloads directly from MinIO
- **Pros**: High performance, CDN-friendly, no bandwidth through app server
- **Cons**: Less control over access after URL is generated
- **Use for**: Images, videos, public files

#### 2. **Proxied Through App Server** (New Implementation - Security & Control)
- **Flow**: Client → API → App validates → App streams from MinIO → Client
- **Pros**: Full security control, access logging, on-the-fly processing
- **Cons**: Uses app server bandwidth, slower for large files
- **Use for**: Sensitive files, files requiring processing, access auditing

---

## Security Features

### Access Control Logic

Files are accessible if ANY of these conditions are met:

1. **Ownership**: User owns the post containing the file
2. **Friendship**: User is friends (ACCEPTED status) with post author
3. **Public**: Currently all posts are public (controlled by `OR 1=1` in query)
4. **Admin**: Users with `ROLE_ADMIN` can access all files

### Implementation Details

**Repository Layer** (`FileRepository.java:30-47`):
```java
@Query(value = """
    SELECT CASE WHEN COUNT(p.post_id) > 0 THEN true ELSE false END
    FROM posts p
    JOIN post_files pf ON p.post_id = pf.post_id
    JOIN files f ON pf.file_id = f.file_id
    WHERE f.file_url = :fileUrl
    AND (
        p.author_id = :userId
        OR EXISTS (
            SELECT 1 FROM friendships fr
            WHERE ((fr.user_id = :userId AND fr.friend_id = p.author_id)
                OR (fr.friend_id = :userId AND fr.user_id = p.author_id))
            AND fr.status = 'ACCEPTED'
        )
        OR 1=1
    )
    """, nativeQuery = true)
Boolean canUserAccessFile(@Param("fileUrl") String fileUrl, @Param("userId") Long userId);
```

**Service Layer** (`FileAccessServiceImpl.java:28-46`):
- Validates input parameters
- Checks admin role first (bypass for admins)
- Delegates to repository for access check
- Logs access attempts for audit trail

---

## Large File Streaming (Range Requests)

### What are HTTP Range Requests?

Range requests allow clients to download files in chunks, enabling:
- **Progressive loading**: Start displaying content before full download
- **Resume downloads**: Continue interrupted downloads
- **Seek in media**: Jump to specific positions in videos/audio
- **Pagination**: Load large files in manageable chunks

### How It Works

```
Client Request:
GET /api/v1/files/posts/12345-video.mp4
Range: bytes=0-1048575

Server Response:
HTTP/1.1 206 Partial Content
Content-Range: bytes 0-1048575/10485760
Content-Length: 1048576
Accept-Ranges: bytes

[First 1MB of data...]
```

### Implementation

**Controller** (`FilesController.java:82-155`):
```java
if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
    return handleRangeRequest(file, rangeHeader, fileSize, inline);
}
```

**MinIO Optimization** (`MinioStorageServiceImpl.java:177-198`):
```java
public InputStream loadRange(String filename, long offset, long length) {
    return minioClient.getObject(
        GetObjectArgs.builder()
            .bucket(minioProperties.getBucketName())
            .object(filename)
            .offset(offset)
            .length(length)
            .build()
    );
}
```

---

## Usage Examples

### 1. Basic File Download (Authenticated)

```bash
curl -X GET "http://localhost:8083/api/v1/files/posts/12345-document.pdf" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -o document.pdf
```

### 2. Display Image Inline (Browser)

```bash
curl -X GET "http://localhost:8083/api/v1/files/posts/12345-image.jpg?inline=true" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 3. Force Download (Attachment)

```bash
curl -X GET "http://localhost:8083/api/v1/files/posts/12345-image.jpg?inline=false" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -o image.jpg
```

### 4. Range Request (First 1MB)

```bash
curl -X GET "http://localhost:8083/api/v1/files/posts/12345-video.mp4" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Range: bytes=0-1048575" \
  -o video_chunk.mp4
```

### 5. Resume Download (Continue from byte 5000000)

```bash
curl -X GET "http://localhost:8083/api/v1/files/posts/12345-video.mp4" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Range: bytes=5000000-" \
  -o video_continuation.mp4
```

### 6. JavaScript/Frontend Examples

**Basic Image Display:**
```javascript
const token = localStorage.getItem('jwt');
const imageUrl = 'http://localhost:8083/api/v1/files/posts/12345-image.jpg';

fetch(imageUrl, {
  headers: {
    'Authorization': `Bearer ${token}`
  }
})
  .then(response => response.blob())
  .then(blob => {
    const imgElement = document.getElementById('myImage');
    imgElement.src = URL.createObjectURL(blob);
  });
```

**Progressive Video Loading:**
```javascript
const video = document.getElementById('myVideo');
video.src = 'http://localhost:8083/api/v1/files/posts/12345-video.mp4';
video.addEventListener('loadstart', () => {
  // Browser automatically handles range requests
  console.log('Video loading with range requests');
});
```

**Download with Progress:**
```javascript
async function downloadWithProgress(fileUrl, token) {
  const response = await fetch(fileUrl, {
    headers: { 'Authorization': `Bearer ${token}` }
  });

  const contentLength = response.headers.get('Content-Length');
  const total = parseInt(contentLength, 10);
  let loaded = 0;

  const reader = response.body.getReader();
  const chunks = [];

  while (true) {
    const { done, value } = await reader.read();
    if (done) break;

    chunks.push(value);
    loaded += value.length;

    const progress = (loaded / total) * 100;
    console.log(`Downloaded: ${progress.toFixed(2)}%`);
  }

  return new Blob(chunks);
}
```

---

## File Processing

### Available Processing Operations

The `FileProcessingService` provides image manipulation capabilities:

#### 1. Resize Image

```java
@Autowired
private FileProcessingService fileProcessingService;

public void resizeUserUpload(MultipartFile file) {
    // Resize to max 1920x1080, maintaining aspect ratio
    MultipartFile resized = fileProcessingService.resizeImage(file, 1920, 1080);

    // Save to storage
    String objectName = filesStorageService.save(resized);
}
```

#### 2. Create Thumbnail

```java
// Create 300x300 thumbnail
MultipartFile thumbnail = fileProcessingService.createThumbnail(file, 300);
String thumbnailUrl = filesStorageService.save(thumbnail, FileCategory.THUMBNAIL);
```

#### 3. Compress Image

```java
// Compress to 85% quality
MultipartFile compressed = fileProcessingService.compressImage(file, 0.85f);
```

#### 4. Add Watermark

```java
MultipartFile watermarked = fileProcessingService.addWatermark(file, "© MyApp 2025");
```

#### 5. Convert Format

```java
// Convert PNG to JPEG
MultipartFile jpeg = fileProcessingService.convertImageFormat(file, "jpg");
```

#### 6. Optimize for Web (Recommended)

```java
// Automatically resize + compress for web display
MultipartFile optimized = fileProcessingService.optimizeForWeb(file);
String url = filesStorageService.save(optimized);
```

### Example: Process Image Before Upload

```java
@PostMapping("/upload-profile-picture")
public ResponseEntity<?> uploadProfilePicture(@RequestParam("file") MultipartFile file) {
    Long userId = SecurityUtils.getCurrentUserId();

    // Validate
    if (!fileProcessingService.isValidImage(file)) {
        throw new CustomRuntimeException("Invalid image file", HttpStatus.BAD_REQUEST);
    }

    // Get dimensions
    int[] dimensions = fileProcessingService.getImageDimensions(file);
    log.info("Original dimensions: {}x{}", dimensions[0], dimensions[1]);

    // Process: resize, compress, add watermark
    MultipartFile processed = fileProcessingService.resizeImage(file, 800, 800);
    processed = fileProcessingService.addWatermark(processed, "© MyApp");

    // Save to MinIO
    String objectName = filesStorageService.save(processed, FileCategory.PROFILE_PICTURE);

    // Update user profile
    profileService.updateProfilePicture(userId, objectName);

    return ResponseEntity.ok("Profile picture updated");
}
```

---

## API Endpoints

### GET /api/v1/files/{filename}

**Description**: Download or stream a file with security validation and range request support.

**Parameters**:
- `filename` (path): File object name (e.g., `posts/12345-image.jpg`)
- `inline` (query, optional): Display inline (`true`) or force download (`false`). Default: `true`

**Headers**:
- `Authorization`: Bearer JWT token (required)
- `Range` (optional): Byte range for partial content (e.g., `bytes=0-1023`)

**Responses**:

| Code | Description | Headers |
|------|-------------|---------|
| 200 | Full file content | `Content-Length`, `Accept-Ranges: bytes` |
| 206 | Partial content (range request) | `Content-Range`, `Content-Length` |
| 401 | Unauthorized (missing/invalid token) | - |
| 403 | Forbidden (no access to file) | - |
| 404 | File not found | - |
| 416 | Range not satisfiable | `Content-Range: bytes */size` |

**Example Responses**:

**Full File (200 OK):**
```
HTTP/1.1 200 OK
Content-Type: image/jpeg
Content-Length: 524288
Content-Disposition: inline; filename="12345-image.jpg"
Accept-Ranges: bytes
Cache-Control: private, max-age=3600
```

**Partial Content (206 Partial Content):**
```
HTTP/1.1 206 Partial Content
Content-Type: image/jpeg
Content-Length: 1024
Content-Range: bytes 0-1023/524288
Content-Disposition: inline; filename="12345-image.jpg"
```

---

## Performance Considerations

### When to Use Each Approach

| Scenario | Recommended | Reason |
|----------|-------------|--------|
| Public images/videos for posts | Presigned URLs | Performance, CDN caching |
| Profile pictures | Presigned URLs | High traffic, cacheable |
| Private documents | App server proxy | Security, access control |
| Large video files (streaming) | App server proxy with ranges | Seek support, bandwidth control |
| Files requiring watermark | App server proxy | On-the-fly processing |
| Download auditing needed | App server proxy | Logging every access |

### Optimization Tips

1. **Use presigned URLs for static content**: Set in `FileMapper.mapFileToFileResponseDto()`
2. **Cache processed images**: Store thumbnails/resized versions separately
3. **Add CDN**: Put CloudFront/CloudFlare in front of presigned URLs
4. **Implement HEAD requests**: Use `MinioStorageServiceImpl.getFileSize()` to get metadata without downloading
5. **Lazy load images**: Use `loading="lazy"` attribute in HTML
6. **Progressive JPEG**: Configure image processing to use progressive encoding

---

## Security Best Practices

1. **Always validate file access**: Never expose direct MinIO URLs without validation
2. **Limit presigned URL expiration**: Set reasonable expiry (default: configured in `MinioProperties`)
3. **Log access attempts**: Monitor failed access attempts for security breaches
4. **Sanitize filenames**: Prevent path traversal attacks (handled by `FileUtils.generateFileName()`)
5. **Validate file types**: Check MIME types and magic bytes, not just extensions
6. **Rate limit downloads**: Consider adding rate limiting for download endpoints
7. **Scan uploads for malware**: Integrate virus scanning before storing files

---

## Troubleshooting

### "Forbidden" Error (403)

**Cause**: User doesn't have access to the file.

**Check**:
1. Is the user logged in? (Valid JWT token)
2. Does the file belong to a post the user can access?
3. Is the user friends with the post author?
4. Check logs for access denial reason

### Range Request Not Working

**Cause**: Client or proxy not supporting range requests.

**Solution**:
1. Ensure client sends `Range: bytes=start-end` header
2. Check reverse proxy (nginx/Apache) doesn't strip Range headers
3. Verify MinIO version supports range requests

### Poor Performance

**Cause**: Large files being proxied through app server.

**Solution**:
1. Use presigned URLs for frequently accessed files
2. Implement caching (Redis/CDN)
3. Optimize images before uploading
4. Consider separate file serving service

---

## Future Enhancements

- [ ] Add video thumbnail generation (requires FFmpeg)
- [ ] Implement progressive image loading (LQIP - Low Quality Image Placeholders)
- [ ] Add support for multipart range requests
- [ ] Integrate CDN for presigned URL caching
- [ ] Implement virus scanning on upload
- [ ] Add file versioning support
- [ ] Rate limiting on download endpoints
- [ ] Metrics/analytics for file access patterns