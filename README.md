# 🍎 EcoHuerto API

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
- [📲 Notifications](#-notifications)
- [🖼 Media Management](#-media-management)
- [🩺 Health & Observability](#-health--observability)
- [🌱 Seed Data](#-seed-data)

---

## 🛠 Tech Stack

- **Java 21**
- **Spring Boot 3.5.x**
- Spring Web, Validation, Security, Data JPA
- JWT (`java-jwt`)
- Flyway
- H2 (dev) / PostgreSQL (prod)
- OpenAPI / Swagger (with Bearer auth)
- Cloudinary (image hosting)
- Twilio SDK / WhatsApp Meta Graph API
- Maven

---

## 📂 Project Structure

```text
src/main/java/com/huerto/api
├── application
│   ├── commands
│   └── usecases (auth, customer, order, product, variety, notification)
├── domain
│   ├── models
│   ├── valueobjects
│   ├── enums
│   ├── events
│   ├── ports
│   └── exceptions
└── infrastructure
    ├── adapters/in/web        (REST controllers, event listeners)
    ├── adapters/out           (JPA, Cloudinary, WhatsApp adapters)
    └── config                 (Security, JWT, CORS, Swagger, Twilio, Cloudinary)
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
- Swagger enabled: http://localhost:8080/swagger-ui/index.html

### prod
- PostgreSQL
- Swagger disabled
- JWT via env variables

---

## 🐳 Environment Variables

| Variable | Description |
|----------|------------|
| `SPRING_PROFILES_ACTIVE` | Active profile (`prod`) |
| `DB_NAME` | Database name |
| `DB_USER` | Database user |
| `DB_PASSWORD` | Database password |
| `JWT_SECRET` | JWT signing secret |
| `JWT_EXPIRATION_MS` | Token duration in ms |
| `CLOUDINARY_CLOUD_NAME` | Cloudinary cloud name |
| `CLOUDINARY_API_KEY` | Cloudinary API key |
| `CLOUDINARY_API_SECRET` | Cloudinary API secret |
| `TWILIO_ACCOUNT_SID` | Twilio account SID |
| `TWILIO_AUTH_TOKEN` | Twilio auth token |
| `TWILIO_WHATSAPP_FROM` | Twilio WhatsApp sender number |
| `WHATSAPP_TOKEN` | Meta Graph API token (if using `meta` profile) |
| `WHATSAPP_PHONE_ID` | Meta WhatsApp phone ID |
| `ADMIN_PHONE` | Admin WhatsApp number for order notifications |
| `FRONTEND_URL` | Frontend base URL (used in WhatsApp notifications) |

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

API available at: http://localhost:8080

---

## 🔐 Authentication

JWT Bearer token required for protected endpoints:

```
Authorization: Bearer <token>
```

Obtain a token via `POST /api/v1/auth/login/customer` or `POST /api/v1/auth/login/admin`.

Two roles are supported: **ADMIN** and **CUSTOMER**.

---

## 🌍 Public Endpoints

```
GET  /api/v1/health
POST /api/v1/auth/**
GET  /api/v1/products
GET  /api/v1/products/{id}
GET  /api/v1/varieties
```

---

## 🛤 Main Endpoints

### Auth
| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/v1/auth/register` | Register new customer |
| POST | `/api/v1/auth/login/customer` | Customer login |
| POST | `/api/v1/auth/login/admin` | Admin login |

### Products
| Method | Path | Role | Description |
|--------|------|------|-------------|
| GET | `/api/v1/products` | Public | List available products (paginated) |
| GET | `/api/v1/products/{id}` | Public | Get product details |
| POST | `/api/v1/products` | ADMIN | Create product |
| PUT | `/api/v1/products/{id}` | ADMIN | Update product |
| PATCH | `/api/v1/products/{id}/stock` | ADMIN | Update stock |
| PATCH | `/api/v1/products/{id}/availability` | ADMIN | Toggle availability |
| DELETE | `/api/v1/products/{id}` | ADMIN | Delete product |
| GET | `/api/v1/admin/products` | ADMIN | List all products including unavailable |
| PATCH | `/api/v1/admin/products/{id}/image` | ADMIN | Upload product image |
| DELETE | `/api/v1/admin/products/{id}/image` | ADMIN | Delete product image |

### Varieties
| Method | Path | Role | Description |
|--------|------|------|-------------|
| GET | `/api/v1/varieties` | Public | List all varieties (paginated) |
| POST | `/api/v1/varieties` | ADMIN | Create variety |
| DELETE | `/api/v1/varieties/{id}` | ADMIN | Delete variety |
| PATCH | `/api/v1/admin/varieties/{id}/image` | ADMIN | Upload variety image |
| DELETE | `/api/v1/admin/varieties/{id}/image` | ADMIN | Delete variety image |

### Orders
| Method | Path | Role | Description |
|--------|------|------|-------------|
| POST | `/api/v1/orders` | AUTH | Create order |
| GET | `/api/v1/orders` | ADMIN | List all orders with filters (paginated) |
| GET | `/api/v1/orders/{id}` | AUTH | Get order details |
| GET | `/api/v1/orders/my` | AUTH | List current user's orders (paginated) |
| PATCH | `/api/v1/orders/{id}/confirm` | ADMIN | Confirm order |
| PATCH | `/api/v1/orders/{id}/ready` | ADMIN | Mark ready for pickup |
| PATCH | `/api/v1/orders/{id}/deliver` | ADMIN | Mark as delivered |
| PATCH | `/api/v1/orders/{id}/cancel` | AUTH | Cancel order |
| PATCH | `/api/v1/orders/{id}/revert` | ADMIN | Revert to pending |
| GET | `/api/v1/admin/orders/stats` | ADMIN | Order statistics by status |

### Customers
| Method | Path | Role | Description |
|--------|------|------|-------------|
| GET | `/api/v1/customers` | ADMIN | List all customers (paginated) |
| GET | `/api/v1/customers/{id}` | AUTH | Get customer details |
| PUT | `/api/v1/customers/{id}` | AUTH | Update customer |
| POST | `/api/v1/customers` | ADMIN | Create customer |
| DELETE | `/api/v1/customers/{id}` | ADMIN | Delete customer |

---

## 📲 Notifications

WhatsApp notifications are sent automatically on order events and can also be triggered manually by admins.

### Automatic Notifications
- **New order**: Admin is notified when a customer places an order (includes order link)
- **Order status change**: Customer is notified when the order status changes

### Manual Broadcast (Admin)
| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/v1/admin/notifications` | Send manual notification to a customer with optional image (`multipart/form-data`) |
| GET | `/api/v1/admin/notifications` | Get notification history filtered by delivery status (paginated) |

### WhatsApp Providers

Two providers are supported, selectable via Spring profile:

| Profile | Provider | Notes |
|---------|----------|-------|
| `meta` | Meta WhatsApp Cloud API (Graph v18.0) | Template-based messages, image support |
| *(default)* | Twilio SDK | Direct text + media attachment |

### Retry Mechanism
Failed notifications are retried automatically by a scheduler:
- Max **3 attempts**
- Configurable retry delay (default **300 seconds**)
- Final state: `PERMANENTLY_FAILED` after max retries

---

## 🖼 Media Management

Images are stored and served via **Cloudinary**.

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/v1/admin/media` | Upload notification media (JPEG/PNG, max 5 MB) |

Product and variety images are managed through their respective admin endpoints (see [Products](#products) and [Varieties](#varieties)).

---

## 🩺 Health & Observability

- Health check: `GET /api/v1/health`
- Global exception handler for consistent error responses
- Swagger UI (dev only): http://localhost:8080/swagger-ui/index.html

---

## 🌱 Seed Data

Managed via Flyway migrations:

- `V1__schema.sql` — Full schema: tables, indexes, constraints
- `V2__seed_data.sql` — Default admin and customer users, initial product catalog, order statuses
