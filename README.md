# E-Commerce Store API

RESTful backend for an online store, built with Java 21, Spring Boot, Spring Security (JWT) and MySQL.

## Features
- JWT authentication with role-based access (`ROLE_USER`, `ROLE_ADMIN`)
- Products and categories (admin-managed), with search, category filter and pagination
- Shopping cart with stock validation
- Checkout with transactional stock deduction and row-level locking
- Request validation and consistent JSON error responses
- OpenAPI / Swagger UI

## Tech Stack
Java 21 · Spring Boot 4 · Spring Security · Spring Data JPA / Hibernate · MySQL 8 · Maven

## Getting Started
1. Create the database: `CREATE DATABASE ecommerce_db;`
2. Copy `.env.example` to `.env` and fill in your values.
3. Run: `./mvnw spring-boot:run`
4. Open Swagger UI: http://localhost:9090/swagger-ui/index.html

## Configuration
| Variable | Description |
|---|---|
| `DB_USERNAME` / `DB_PASSWORD` | Database credentials |
| `JWT_SECRET` | Base64 secret, at least 32 bytes |
| `JWT_EXPIRATION_MS` | Token lifetime (default 24h) |

## API Overview
| Method | Endpoint | Access |
|---|---|---|
| POST | `/api/auth/register`, `/api/auth/login` | Public |
| GET | `/api/products`, `/api/products/{id}`, `/api/categories` | Public |
| POST/PUT/DELETE | `/api/products`, `/api/categories` | Admin |
| GET/POST/DELETE | `/api/cart`, `/api/cart/add`, `/api/cart/items/{productId}` | User |
| POST/GET | `/api/orders/checkout`, `/api/orders` | User |

## Tests
`./mvnw test`