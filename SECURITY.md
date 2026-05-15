# Security Implementation Guide

## 1. JWT Authentication

The application now uses JWT (JSON Web Tokens) for stateless authentication.

### Register a new user:

```
POST /api/auth/register
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "securePassword123"
}

Response (201 Created):
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "username": "john_doe",
  "message": "User registered successfully"
}
```

### Login:

```
POST /api/auth/login
Content-Type: application/json

{
  "username": "john_doe",
  "password": "securePassword123"
}

Response (200 OK):
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "username": "john_doe",
  "message": "Login successful"
}
```

### Use the token:

All authenticated endpoints require the JWT token in the Authorization header:

```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

## 2. HTTPS/SSL Configuration

To enable HTTPS, follow these steps:

### Option A: Generate Self-Signed Certificate (Development)

```bash
keytool -genkey -alias tomcat -storetype PKCS12 -keyalg RSA -keysize 2048 -keystore keystore.p12 -validity 365
```

When prompted:

- Enter keystore password: `changeit`
- First and last name: Your domain name (e.g., localhost)
- Organizational unit: Your organization
- Organization: Your organization
- City/Locality: Your city
- State/Province: Your state
- Country: Your country code

### Option B: Use Let's Encrypt (Production)

1. Obtain a certificate from Let's Encrypt
2. Convert to PKCS12 format:

```bash
openssl pkcs12 -export -in certificate.crt -inkey private.key -out keystore.p12
```

### Place keystore in your project:

```
src/main/resources/keystore.p12
```

### Update application.yaml:

```yaml
server:
  ssl:
    enabled: true
    key-store: classpath:keystore.p12
    key-store-password: changeit
    key-store-type: PKCS12
  port: 8443
```

Then access via: `https://localhost:8443`

## 3. Input Validation

All input is validated using Jakarta Validation annotations:

### Book Model Validations:

- **title**: Required, 1-200 characters
- **author**: Required, 1-100 characters
- **publishedYear**: Between 1000-2100
- **price**: >= 0
- **pages**: >= 1

### Rating Validations:

- **rating**: Between 1-5

### User Validations:

- **username**: 3-50 characters, unique
- **email**: Valid email format, unique
- **password**: Minimum 6 characters

Invalid requests return 400 Bad Request with detailed error messages:

```json
{
  "status": 400,
  "message": "Validation failed",
  "errors": {
    "title": "Title is required",
    "author": "Author must be between 1 and 100 characters"
  }
}
```

## 4. Endpoint Security

All endpoints require authentication (JWT token) except:

- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `GET /swagger-ui/**` - API documentation
- `GET /v3/api-docs/**` - OpenAPI spec

## 5. Password Security

Passwords are encrypted using BCrypt with a strength of 12 (default).

## 6. Environment Variables

Configure these in your `.env` file or system environment:

```env
# JWT Configuration
JWT_SECRET=YourSecretKeyHere
JWT_EXPIRATION=86400000

# SSL Configuration
SSL_KEYSTORE_PASSWORD=changeit

# Database
SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/books
SPRING_DATASOURCE_USERNAME=books
SPRING_DATASOURCE_PASSWORD=yourpassword
```

## 7. Testing Authentication

### Using curl:

```bash
# Register
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"test","email":"test@test.com","password":"password123"}'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"password123"}'

# Access protected endpoint
curl -X GET http://localhost:8080/books \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### Using Swagger UI:

1. Go to `http://localhost:8080/swagger-ui/index.html`
2. Register or login via `/api/auth/register` or `/api/auth/login`
3. Copy the token from response
4. Click the lock icon in Swagger UI
5. Enter: `Bearer YOUR_TOKEN_HERE`
