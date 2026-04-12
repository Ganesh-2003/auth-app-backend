# Auth App Backend

A Spring Boot authentication backend application that provides user authentication and role-based access control (RBAC) with support for multiple authentication providers (Local, Google, GitHub, Facebook).

## 📋 Table of Contents

- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [Database Setup](#database-setup)
- [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
- [Project Status](#project-status)
- [Contributing](#contributing)

---

## 🛠️ Tech Stack

| Technology | Version | Purpose |
|-----------|---------|---------|
| **Java** | 25 | Programming Language |
| **Spring Boot** | 4.0.5 | Framework |
| **Spring Data JPA** | Latest | ORM |
| **Spring Security** | Latest | Authentication & Authorization |
| **MySQL** | 8.0+ | Database |
| **Lombok** | Latest | Boilerplate Reduction |
| **Jakarta Persistence** | Latest | JPA Implementation |

---

## 📋 Prerequisites

Before you begin, ensure you have the following installed:

- **Java 25** or higher
- **MySQL 8.0** or higher
- **Maven 3.6.0** or higher
- **Git**

### Verify Installation

```bash
java -version
mysql --version
mvn -version
```

---

## 📁 Project Structure

```
auth-app-backend/
│
├── src/main/java/com/app/auth/auth_app_backend/
│   ├── AuthAppBackendApplication.java          # Application Entry Point
│   │
│   ├── entities/                               # JPA Entity Models
│   │   ├── User.java                           # User entity
│   │   ├── Role.java                           # Role entity
│   │   └── Provider.java                       # Authentication providers enum
│   │
│   ├── repositories/                           # Data Access Layer (DAO)
│   │   ├── UserRepository.java                 # User repository (to be implemented)
│   │   └── RoleRepository.java                 # Role repository (to be implemented)
│   │
│   ├── services/                               # Business Logic Layer
│   │   ├── AuthService.java                    # Authentication service (to be implemented)
│   │   └── UserService.java                    # User management service (to be implemented)
│   │
│   ├── controllers/                            # REST API Controllers
│   │   ├── AuthController.java                 # Auth endpoints (to be implemented)
│   │   └── UserController.java                 # User endpoints (to be implemented)
│   │
│   ├── dtos/                                   # Data Transfer Objects
│   │   ├── LoginRequest.java                   # Login DTO (to be implemented)
│   │   ├── SignupRequest.java                  # Signup DTO (to be implemented)
│   │   └── UserResponse.java                   # User response DTO (to be implemented)
│   │
│   ├── config/                                 # Configuration Classes
│   │   └── SecurityConfig.java                 # Spring Security configuration (to be implemented)
│   │
│   └── helpers/                                # Utility Classes (to be implemented)
│
├── src/main/resources/
│   ├── application.yaml                        # Main configuration
│   ├── application-dev.yaml                    # Development profile
│   ├── application-prod.yaml                   # Production profile
│   └── application-qa.yaml                     # QA profile
│
├── pom.xml                                     # Maven configuration
└── HELP.md                                     # Additional help

```

---

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/your-repo/auth-app-backend.git
cd auth-app-backend
```

### 2. Install Dependencies

```bash
mvn clean install
```

### 3. Create MySQL Database

```sql
-- Create database
CREATE DATABASE auth_db;

-- Switch to database
USE auth_db;
```

### 4. Configure Database Connection

Edit `src/main/resources/application-dev.yaml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/auth_db
    username: root
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver

  jpa:
    hibernate:
      ddl-auto: create-drop  # Options: create, create-drop, validate, update, none
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
        format_sql: true
```

---

## ⚙️ Configuration

### Environment Profiles

The application supports multiple profiles:

- **dev** - Development environment
- **qa** - Quality Assurance environment  
- **prod** - Production environment

Set active profile in `application.yaml`:

```yaml
spring:
  application:
    name: auth-app-backend
  profiles:
    active: dev  # Change to qa or prod as needed
```

### Server Configuration

Default server port: `8082`

```yaml
server:
  port: 8082
```

---

## 🗄️ Database Setup

### Database Tables

The application will automatically create the following tables:

#### Users Table
```sql
CREATE TABLE users (
  user_id BINARY(16) PRIMARY KEY,
  user_email VARCHAR(300) UNIQUE NOT NULL,
  user_name VARCHAR(500),
  user_password VARCHAR(255),
  user_image VARCHAR(255),
  user_enable BOOLEAN DEFAULT true,
  created_at TIMESTAMP,
  updated_at TIMESTAMP,
  user_gender VARCHAR(50),
  user_address VARCHAR(500),
  provider VARCHAR(50) DEFAULT 'LOCAL'
);
```

#### Roles Table
```sql
CREATE TABLE roles (
  role_id BINARY(16) PRIMARY KEY,
  role_name VARCHAR(100) UNIQUE NOT NULL
);
```

#### User Roles Junction Table
```sql
CREATE TABLE user_roles (
  user_id BINARY(16) NOT NULL,
  role_id BINARY(16) NOT NULL,
  PRIMARY KEY (user_id, role_id),
  FOREIGN KEY (user_id) REFERENCES users(user_id),
  FOREIGN KEY (role_id) REFERENCES roles(role_id)
);
```

---

## ▶️ Running the Application

### Using Maven

```bash
# Development profile
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"

# QA profile
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=qa"

# Production profile
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=prod"
```

### Using Java JAR

```bash
# Build the application
mvn clean package

# Run the JAR
java -jar target/auth-app-backend-0.0.1-SNAPSHOT.jar
```

### Using IDE

1. Open project in IntelliJ IDEA or Eclipse
2. Right-click `AuthAppBackendApplication.java`
3. Select Run 'AuthAppBackendApplication'

---

## 📚 API Documentation

### Base URL
```
http://localhost:8082/api
```

### Authentication Endpoints (To Be Implemented)

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/auth/register` | Register a new user |
| `POST` | `/auth/login` | User login |
| `POST` | `/auth/logout` | User logout |
| `POST` | `/auth/refresh` | Refresh authentication token |

### User Endpoints (To Be Implemented)

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/users` | Get all users |
| `GET` | `/users/{id}` | Get user by ID |
| `PUT` | `/users/{id}` | Update user |
| `DELETE` | `/users/{id}` | Delete user |
| `GET` | `/users/{id}/roles` | Get user roles |

---

## 🔐 Authentication Providers

The application supports authentication via:

- **LOCAL** - Email and password
- **GOOGLE** - Google OAuth
- **GITHUB** - GitHub OAuth
- **FACEBOOK** - Facebook OAuth

---

## 📊 Entity Relationships

### User ↔ Role (Many-to-Many)

```
User (1) ──── (M) Role
         user_roles
```

Each user can have multiple roles, and each role can be assigned to multiple users.

---

## 📝 Current Implementation Status

### ✅ Completed

- [x] Project setup with Spring Boot 4.0.5
- [x] User entity with UUID primary key
- [x] Role entity with enum provider
- [x] User-Role many-to-many relationship
- [x] JPA with MySQL connectivity
- [x] Lombok integration for boilerplate reduction
- [x] Multi-profile configuration (dev, qa, prod)

### 🔄 In Progress / To Do

- [ ] Repositories (UserRepository, RoleRepository)
- [ ] Services (AuthService, UserService)
- [ ] Controllers (AuthController, UserController)
- [ ] DTOs (LoginRequest, SignupRequest, UserResponse)
- [ ] Security Configuration (JWT, OAuth2)
- [ ] API endpoints
- [ ] Unit tests
- [ ] Integration tests
- [ ] Documentation

---

## 🐛 Troubleshooting

### Issue: Cannot Connect to MySQL

**Solution:**
```bash
# Verify MySQL is running
mysql -u root -p

# Check connection in application.yaml
spring.datasource.url: jdbc:mysql://localhost:3306/auth_db
```

### Issue: Tables Not Created

**Solution:**
```yaml
# In application.yaml, ensure:
spring.jpa.hibernate.ddl-auto: create
```

### Issue: Cannot Resolve Column 'user_id'

**Solution:**
- Ensure Role.java has `@Entity` and `@Table` annotations
- Verify all entities have `@Column` annotations for fields
- Check `@ManyToMany` and `@JoinTable` mappings

### Issue: Port 8082 Already in Use

**Solution:**
```yaml
# Change port in application.yaml
server.port: 8083
```

---

## 📧 Support

For issues or questions, please create an issue on the repository or contact the development team.

---

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## 🎯 Roadmap

- [ ] JWT token implementation
- [ ] OAuth2 integration for Google, GitHub, Facebook
- [ ] Email verification
- [ ] Password reset functionality
- [ ] User profile management
- [ ] Role-based access control (RBAC)
- [ ] Audit logging
- [ ] API rate limiting
- [ ] Swagger/OpenAPI documentation
- [ ] Docker support

---

**Last Updated:** April 12, 2026
**Version:** 0.0.1-SNAPSHOT
