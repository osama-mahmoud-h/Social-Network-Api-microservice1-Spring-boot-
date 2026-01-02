# File Serving Testing Guide

## Summary of Changes

### 1. FilesController.java - Fixed Two Critical Issues

#### Issue #1: Range Request Parsing Bug
**Problem:** The code couldn't handle suffix-range requests like `bytes=-500` (last 500 bytes).

**Fixed:**
- Lines 159-253: Complete rewrite of `handleRangeRequestOptimized()`
- Now supports ALL valid HTTP Range formats:
  - `bytes=0-1023` (specific range) ✓
  - `bytes=1000-` (from offset to end) ✓
  - `bytes=-500` (last N bytes) ✓ **NEW**

#### Issue #2: File URL Security Vulnerability
**Problem:** `/api/v1/files/url` endpoint returned direct MinIO pre-signed URLs that could be shared with unauthenticated users.

**Fixed:**
- Lines 117-149: Changed to return server-proxied URLs
- Old: `https://minio:9000/bucket/file.jpg?signature=xyz` (insecure)
- New: `/api/v1/files?filename=posts/file.jpg&inline=true` (secure)
- All access now goes through authentication layer

### 2. test-file-viewer.html - Better Error Handling

**Added:**
- Automatic detection of partial content (HTTP 206 responses)
- Clear warning when trying to display partial PDFs/images
- Better debugging with console logs
- "Load Full File Instead" button when partial content is detected

---

## How to Test File Serving

### Step 1: Ensure Services are Running

```bash
# Check MinIO is running
docker ps | grep minio

# Check main-service is running
curl http://localhost:8083/actuator/health
```

### Step 2: Open the Test Page

Open `test-file-viewer.html` in your browser:
```bash
# From project root
xdg-open services/main-service/test-file-viewer.html
# or
firefox services/main-service/test-file-viewer.html
```

### Step 3: Test Different Scenarios

#### ✅ Test 1: Full File Load (Recommended for PDFs)
1. **Uncheck** "Enable Range Request"
2. Enter file path: `posts/your-file.pdf`
3. Click **"Load Full File"**
4. Result: Should display complete PDF

#### ✅ Test 2: File Information (No Download)
1. Click **"Get File Info"**
2. Result: Shows file size, Content-Type, and whether range requests are supported

#### ✅ Test 3: Range Request for Video Streaming
1. Upload a video file to test
2. **Check** "Enable Range Request"
3. Set range: 0 to 1048575 (first 1MB)
4. Click **"Load with Range"**
5. Result: Video should start playing from the beginning

#### ⚠️ Test 4: Range Request for PDF (Will Show Warning)
1. **Check** "Enable Range Request"
2. Click **"First 1KB"** preset button
3. Click **"Load with Range"**
4. Result: Shows warning that partial PDFs can't be displayed
5. Click **"Load Full File Instead"** to load complete PDF

---

## Understanding Range Requests

### When to Use Range Requests
✅ **Good for:**
- Video streaming (seeking, progressive loading)
- Audio streaming
- Resume interrupted downloads
- Large file pagination

❌ **Not good for:**
- PDFs (need complete file to render)
- Images (need complete file to display)
- Small files (<1MB)

### HTTP Status Codes
- **200 OK**: Full file returned
- **206 Partial Content**: Range request successful
- **416 Range Not Satisfiable**: Invalid range (e.g., start > file size)

---

## Common Issues & Solutions

### Issue: "PDF document is damaged"
**Cause:** You're using range requests to load a partial PDF.

**Solution:**
1. Disable "Enable Range Request" checkbox
2. Click "Load Full File"
3. PDFs require the complete file to render

---

### Issue: Authentication Failed
**Cause:** JWT token expired or invalid.

**Solution:**
1. Login to get a fresh JWT token
2. Update line 432 in `test-file-viewer.html` with new token
3. Or modify the HTML to read token from localStorage

---

### Issue: File Not Found (404)
**Cause:** File doesn't exist in MinIO or wrong path.

**Solution:**
1. Check MinIO console: `http://localhost:9001`
2. Login with credentials from `.env` (minioadmin/minioadmin)
3. Verify file exists in `social-network-files` bucket
4. Ensure path matches exactly (e.g., `posts/uuid.pdf`)

---

### Issue: Forbidden (403)
**Cause:** User doesn't have permission to access the file.

**Solution:**
1. Check `FileAccessService.canUserAccessFile()` logic
2. Verify the file belongs to the authenticated user
3. For admin testing, ensure user has ADMIN role

---

## Testing with cURL

### Get Full File
```bash
curl -H "Authorization: Bearer YOUR_JWT_TOKEN" \
     "http://localhost:8083/api/v1/files?filename=posts/file.pdf&inline=true" \
     --output downloaded.pdf
```

### Get File with Range Request
```bash
# Get first 1KB
curl -H "Authorization: Bearer YOUR_JWT_TOKEN" \
     -H "Range: bytes=0-1023" \
     "http://localhost:8083/api/v1/files?filename=posts/file.pdf&inline=true" \
     --output partial.pdf

# Get last 1KB
curl -H "Authorization: Bearer YOUR_JWT_TOKEN" \
     -H "Range: bytes=-1024" \
     "http://localhost:8083/api/v1/files?filename=posts/file.pdf&inline=true" \
     --output partial-end.pdf
```

### Get Secure File URL
```bash
curl -H "Authorization: Bearer YOUR_JWT_TOKEN" \
     "http://localhost:8083/api/v1/files/url?fileName=posts/file.pdf"

# Response:
# {
#   "statusCode": 200,
#   "message": "Secure file URL generated successfully",
#   "data": "/api/v1/files?filename=posts/file.pdf&inline=true"
# }
```

---

## Browser Console Debugging

Open browser DevTools (F12) and check:

1. **Network Tab:**
   - Request URL
   - Request Headers (Range, Authorization)
   - Response Status (200, 206, 403, 404)
   - Response Headers (Content-Range, Content-Length)

2. **Console Tab:**
   - Request/response logs from JavaScript
   - Any error messages

Example console output:
```
Request URL: http://localhost:8083/api/v1/files?filename=posts/file.pdf&inline=true
Request Headers: {Authorization: "Bearer ...", Range: "bytes=0-1023"}
Response Status: 206 (Partial Content)
Content-Range: bytes 0-1023/524288
```

---

## Production Recommendations

1. **Enable CORS** if frontend is on different domain
2. **Add rate limiting** to prevent abuse
3. **Implement caching** headers for static content
4. **Use CDN** for frequently accessed files
5. **Monitor MinIO** metrics and storage usage
6. **Set up file size limits** in FileAccessService
7. **Consider pre-signed URLs** for direct MinIO access (with short expiry)

---

## Next Steps

1. Test with your actual files in MinIO
2. Verify authentication works with real JWT tokens
3. Test file upload → download flow end-to-end
4. Monitor logs for any errors:
   ```bash
   # Watch main-service logs
   tail -f services/main-service/logs/application.log

   # Or if running with Docker
   docker logs -f main-service
   ```

---

## Need Help?

Check the logs for detailed error messages:
- FilesController logs at `DEBUG` level show detailed range information
- FileAccessService logs show permission checks
- MinioStorageServiceImpl logs show MinIO operations

Enable debug logging in `application.yml`:
```yaml
logging:
  level:
    com.app.server.controller.FilesController: DEBUG
    com.app.server.service.FileService: DEBUG
    com.app.server.utils.fileStorage.impl.MinioStorageServiceImpl: DEBUG
```