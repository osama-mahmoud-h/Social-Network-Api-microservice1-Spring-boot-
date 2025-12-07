# OAuth2 Social Login Implementation Guide

## Overview

The auth-service now supports OAuth2 social login with Google, Facebook, and GitHub. Users can register and authenticate using their existing social media accounts.

## Architecture

### Components

1. **CustomOAuth2UserService** (`service/CustomOAuth2UserService.java`)
   - Handles OAuth2 user authentication
   - Creates new users or links existing accounts
   - Extracts user information from OAuth2 providers

2. **CustomOAuth2User** (`service/CustomOAuth2User.java`)
   - Wrapper class for OAuth2 user data
   - Bridges Spring Security OAuth2User with our User entity

3. **OAuth2AuthenticationSuccessHandler** (`security/OAuth2AuthenticationSuccessHandler.java`)
   - Handles successful OAuth2 authentication
   - Generates JWT tokens
   - Redirects to frontend with token and user info

4. **OAuth2AuthenticationFailureHandler** (`security/OAuth2AuthenticationFailureHandler.java`)
   - Handles OAuth2 authentication failures
   - Redirects to frontend with error message

5. **SecurityConfig** (`config/SecurityConfig.java`)
   - Configures OAuth2 login endpoints
   - Integrates custom handlers and services

## Setup Instructions

### 1. Configure OAuth2 Providers

#### Google OAuth2

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing one
3. Navigate to **APIs & Services** → **Credentials**
4. Click **Create Credentials** → **OAuth 2.0 Client ID**
5. Configure OAuth consent screen:
   - User Type: External
   - App name: Your app name
   - User support email: Your email
   - Developer contact: Your email
6. Create OAuth 2.0 Client ID:
   - Application type: Web application
   - Name: Auth Service OAuth2
   - Authorized redirect URIs:
     - `http://localhost:8087/login/oauth2/code/google`
     - `https://yourdomain.com/login/oauth2/code/google` (production)
7. Copy **Client ID** and **Client Secret**

#### Facebook OAuth2

1. Go to [Facebook Developers](https://developers.facebook.com/)
2. Click **My Apps** → **Create App**
3. Select app type: **Consumer**
4. Fill in app details and create app
5. Add **Facebook Login** product
6. Configure Facebook Login settings:
   - Valid OAuth Redirect URIs:
     - `http://localhost:8087/login/oauth2/code/facebook`
     - `https://yourdomain.com/login/oauth2/code/facebook` (production)
7. Go to **Settings** → **Basic**
8. Copy **App ID** (Client ID) and **App Secret** (Client Secret)
9. Make app live (toggle at top of dashboard)

#### GitHub OAuth2

1. Go to [GitHub Settings](https://github.com/settings/developers)
2. Click **OAuth Apps** → **New OAuth App**
3. Fill in application details:
   - Application name: Your app name
   - Homepage URL: `http://localhost:8087`
   - Authorization callback URL:
     - `http://localhost:8087/login/oauth2/code/github`
4. Click **Register application**
5. Copy **Client ID**
6. Generate a new **Client Secret** and copy it

### 2. Update Environment Variables

Add the following to your `.env` file:

```bash
# OAuth2 Providers
GOOGLE_CLIENT_ID=your-google-client-id.apps.googleusercontent.com
GOOGLE_CLIENT_SECRET=your-google-client-secret

FACEBOOK_CLIENT_ID=your-facebook-app-id
FACEBOOK_CLIENT_SECRET=your-facebook-app-secret

GITHUB_CLIENT_ID=your-github-client-id
GITHUB_CLIENT_SECRET=your-github-client-secret

# OAuth2 Redirect URLs (Your Frontend URLs)
OAUTH2_SUCCESS_REDIRECT_URL=http://localhost:3000/oauth/callback
OAUTH2_FAILURE_REDIRECT_URL=http://localhost:3000/login?error=oauth_failed
```

### 3. Database Schema

The User entity already includes OAuth2 fields:
- `oauth_provider` (ENUM: GOOGLE, FACEBOOK, GITHUB, LOCAL)
- `oauth_provider_id` (Provider's unique user ID)
- `email_verified` (Boolean, auto-set to true for OAuth2 users)

No additional database migrations needed!

## OAuth2 Flow

### 1. Initiate OAuth2 Login

**Frontend redirects user to:**

```
# Google Login
GET http://localhost:8087/oauth2/authorization/google

# Facebook Login
GET http://localhost:8087/oauth2/authorization/facebook

# GitHub Login
GET http://localhost:8087/oauth2/authorization/github
```

### 2. User Authenticates with Provider

User is redirected to the OAuth2 provider (Google/Facebook/GitHub) and authenticates.

### 3. Provider Redirects Back

Provider redirects to: `http://localhost:8087/login/oauth2/code/{provider}`

### 4. Spring Security Processes Callback

- **CustomOAuth2UserService** is invoked
- User info is fetched from provider
- New user is created OR existing user is linked

### 5. Success Handler Generates JWT

- **OAuth2AuthenticationSuccessHandler** is invoked
- JWT access and refresh tokens are generated
- Device info is saved
- User is redirected to frontend

### 6. Frontend Receives Token

**Redirect URL format:**
```
http://localhost:3000/oauth/callback?token=<JWT>&userId=<ID>&email=<EMAIL>&firstName=<NAME>&lastName=<NAME>
```

Frontend should:
1. Extract token from URL query params
2. Store token in localStorage/sessionStorage
3. Remove query params from URL
4. Use token for subsequent API requests

## API Endpoints

### OAuth2 Login Initiation

```http
GET /oauth2/authorization/google
GET /oauth2/authorization/facebook
GET /oauth2/authorization/github
```

These endpoints are handled by Spring Security and redirect to the provider.

### Existing Endpoints (Still Available)

```http
POST /api/auth/register     # Traditional email/password registration
POST /api/auth/login        # Traditional email/password login
POST /api/auth/logout       # Logout (revokes token)
POST /api/auth/validate     # Validate JWT token
```

## User Account Linking

### Scenario 1: New User
- User logs in with OAuth2 for the first time
- New account is created automatically
- Email is marked as verified
- Default role: USER

### Scenario 2: Existing User (Email Match)
- User previously registered with email/password
- User logs in with OAuth2 using same email
- OAuth provider is linked to existing account
- Email is marked as verified
- User can now login with either method

### Scenario 3: Multiple OAuth Providers
- Currently, only one OAuth provider per account
- First OAuth login links the provider
- If user tries different OAuth provider with same email, existing account is returned

## Testing

### 1. Start the Auth Service

```bash
cd services/auth-service
mvn spring-boot:run
```

### 2. Test OAuth2 Login

Open browser and navigate to:
```
http://localhost:8087/oauth2/authorization/google
```

You should be redirected to Google login page.

### 3. Verify Database

After successful login, check the `users` table:
```sql
SELECT user_id, email, first_name, last_name, oauth_provider, oauth_provider_id, email_verified
FROM users
WHERE oauth_provider IS NOT NULL;
```

### 4. Test JWT Token

Use the token returned in the redirect URL to make authenticated requests:

```bash
curl -H "Authorization: Bearer <YOUR_TOKEN>" \
     http://localhost:8087/api/auth/devices?userId=<USER_ID>
```

## Frontend Integration Example

### React/Next.js Example

```javascript
// Login Button
<button onClick={() => window.location.href = 'http://localhost:8087/oauth2/authorization/google'}>
  Login with Google
</button>

// Callback Page (pages/oauth/callback.js)
import { useEffect } from 'react';
import { useRouter } from 'next/router';

export default function OAuthCallback() {
  const router = useRouter();

  useEffect(() => {
    const { token, userId, email, firstName, lastName, error } = router.query;

    if (error) {
      console.error('OAuth login failed:', error);
      router.push('/login?error=' + error);
      return;
    }

    if (token) {
      // Store token
      localStorage.setItem('authToken', token);
      localStorage.setItem('userId', userId);
      localStorage.setItem('userEmail', email);

      // Redirect to dashboard
      router.push('/dashboard');
    }
  }, [router.query]);

  return <div>Processing login...</div>;
}
```

### Vue.js Example

```javascript
// OAuthCallback.vue
<template>
  <div>Processing login...</div>
</template>

<script>
export default {
  mounted() {
    const urlParams = new URLSearchParams(window.location.search);
    const token = urlParams.get('token');
    const userId = urlParams.get('userId');
    const error = urlParams.get('error');

    if (error) {
      console.error('OAuth login failed:', error);
      this.$router.push('/login?error=' + error);
      return;
    }

    if (token) {
      localStorage.setItem('authToken', token);
      localStorage.setItem('userId', userId);
      this.$router.push('/dashboard');
    }
  }
}
</script>
```

## Security Considerations

### 1. HTTPS in Production
- Always use HTTPS in production
- Update redirect URIs in provider console to use `https://`

### 2. CORS Configuration
- Update CORS settings in SecurityConfig if needed
- Restrict allowed origins in production

### 3. Token Security
- JWT tokens are sensitive - never expose in logs
- Use HTTP-only cookies as alternative to query params
- Implement token rotation for refresh tokens

### 4. Email Verification
- OAuth2 users have `email_verified=true` automatically
- Ensure provider returns verified emails
- Some providers allow unverified emails

### 5. Account Takeover Prevention
- Current implementation links accounts by email
- Consider additional verification for account linking
- Implement rate limiting on OAuth endpoints

## Troubleshooting

### Issue: "redirect_uri_mismatch" error

**Solution:** Ensure redirect URI in provider console matches exactly:
```
http://localhost:8087/login/oauth2/code/{provider}
```

### Issue: "Invalid client" error

**Solution:** Check that:
1. Client ID and Secret are correct in `.env`
2. OAuth app is enabled/published in provider console
3. Credentials haven't expired

### Issue: User not created in database

**Solution:**
1. Check auth-service logs for errors
2. Verify database connection
3. Check OAuth2 provider returns email in user info

### Issue: Frontend not receiving token

**Solution:**
1. Check `OAUTH2_SUCCESS_REDIRECT_URL` in `.env`
2. Verify success handler is invoked (check logs)
3. Ensure frontend route `/oauth/callback` exists

## Production Deployment

### 1. Update Redirect URIs

In each provider's console, add production redirect URIs:
```
https://api.yourdomain.com/login/oauth2/code/google
https://api.yourdomain.com/login/oauth2/code/facebook
https://api.yourdomain.com/login/oauth2/code/github
```

### 2. Update Environment Variables

```bash
OAUTH2_SUCCESS_REDIRECT_URL=https://yourdomain.com/oauth/callback
OAUTH2_FAILURE_REDIRECT_URL=https://yourdomain.com/login?error=oauth_failed
```

### 3. Enable HTTPS

Ensure your Spring Boot application is configured for HTTPS or behind a reverse proxy (nginx, traefik).

### 4. Security Hardening

- Enable rate limiting on OAuth endpoints
- Implement CSRF protection
- Use secure cookie flags
- Monitor for suspicious OAuth attempts

## Additional Resources

- [Spring Security OAuth2 Docs](https://docs.spring.io/spring-security/reference/servlet/oauth2/login/core.html)
- [Google OAuth2 Guide](https://developers.google.com/identity/protocols/oauth2)
- [Facebook Login Guide](https://developers.facebook.com/docs/facebook-login/web)
- [GitHub OAuth Guide](https://docs.github.com/en/developers/apps/building-oauth-apps/authorizing-oauth-apps)
