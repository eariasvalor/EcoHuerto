# 🍎 Huerto API

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.x-brightgreen)
![Build](https://img.shields.io/badge/build-Maven-blue)
![License](https://img.shields.io/badge/license-TBD-lightgrey)

REST API for managing catalog products, customers, and orders for a fruit/produce store.

Built with **Java 21 + Spring Boot 3**, following **Hexagonal Architecture (Ports & Adapters)**, secured with **JWT**, and using **Flyway** for database migrations.

---

## 📑 Table of Contents

- [🛠 Tech Stack](#-tech-stack)
- [📂 Project Structure](#-project-structure)
- [📋 Requirements](#-requirements)
- [⚙️ Profiles](#️-profiles)
- [🐳 Environment Variables](#-environment-variables)
- [🚀 Getting Started](#-getting-started)
- [🔐 Authentication](#-authentication)
- [🌍 Public Endpoints](#-public-endpoints)
- [🛤 Main Endpoints](#-main-endpoints)
- [🩺 Health & Observability](#-health--observability)
- [🌱 Seed Data](#-seed-data)
- [📄 License](#-license)

---

## 🛠 Tech Stack

- **Java 21**
- **Spring Boot 3.5.x**
- Spring Web, Validation, Security, Data JPA
- JWT (`java-jwt`)
- Flyway
- H2 (dev) / PostgreSQL (prod)
- OpenAPI / Swagger
- Maven

---

## 📂 Project Structure

```text
src/main/java/com/huerto/api
├── application
├── domain
└── infrastructure
    ├── adapters/in/web
    ├── adapters/out
    └── config
```

---

## 📋 Requirements

- JDK 21
- Maven 3.9+ (or `./mvnw`)
- Docker (optional)

---

## ⚙️ Profiles

### dev (default)
- H2 in-memory DB
- Flyway migrations on startup
- Swagger enabled:
  http://localhost:8080/swagger-ui/index.html

### prod
- PostgreSQL
- Swagger disabled
- JWT via env variables

---

## 🐳 Environment Variables

| Variable | Description |
|----------|------------|
| SPRING_PROFILES_ACTIVE | prod |
| DB_NAME | Database name |
| DB_USER | Database user |
| DB_PASSWORD | Database password |
| JWT_SECRET | JWT secret |
| JWT_EXPIRATION_MS | Token duration |

---

## 🚀 Getting Started

### Run locally

```bash
./mvnw spring-boot:run
```

### Run tests

```bash
./mvnw test
```

### Run with Docker

```bash
docker compose up --build
```

API available at:
http://localhost:8080

---

## 🔐 Authentication

Use JWT Bearer token:

```
Authorization: Bearer <token>
```

---

## 🌍 Public Endpoints

- GET /api/v1/health
- POST /api/v1/auth/**
- GET /api/v1/products
- GET /api/v1/products/{id}
- GET /api/v1/varieties

---

## 🛤 Main Endpoints

### Auth
- POST /api/v1/auth/register
- POST /api/v1/auth/login/customer
- POST /api/v1/auth/login/admin

### Products
- GET /api/v1/products
- POST /api/v1/products (ADMIN)
- PATCH /api/v1/products/{id}/stock (ADMIN)
- GET /api/v1/admin/products (ADMIN)

### Varieties
- GET /api/v1/varieties
- POST /api/v1/varieties (ADMIN)

### Customers
- GET /api/v1/customers/{id}
- GET /api/v1/customers (ADMIN)

### Orders
- POST /api/v1/orders
- GET /api/v1/orders/my
- PATCH /api/v1/orders/{id}/confirm
- GET /api/v1/admin/orders/stats

---

## 🩺 Health & Observability

- Health: GET /api/v1/health
- GlobalExceptionHandler for consistent errors

---

## 🌱 Seed Data

- Default users (ADMIN + CUSTOMER)
- Initial product catalog
- Order statuses

---

## 📄 License

TBD
