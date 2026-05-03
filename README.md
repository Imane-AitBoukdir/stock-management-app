# Stock Management App

A full-stack stock management application built with **Spring Boot** (backend), **React** (frontend), and **MySQL** (database).

---

## Tech Stack

| Layer    | Technology          |
|----------|---------------------|
| Backend  | Java 17 + Spring Boot 3 |
| Frontend | React (Vite or CRA) |
| Database | MySQL 8             |
| Auth     | Spring Security + JWT |

---

## Project Structure

```
stock-management-app/
├── backend/       # Spring Boot REST API
├── frontend/      # React SPA
└── README.md
```

---

## Team & Branch Assignments

### Imane

#### `feature/setup-foundation` — Sprint 1

**What to implement:**
- Initialize the Spring Boot project (`pom.xml`, dependencies: Spring Web, Spring Data JPA, MySQL Driver, Lombok, Swagger/OpenAPI)
- Configure `application.properties`: datasource URL, username, password, Hibernate DDL auto
- Define the package structure: `controller`, `service`, `repository`, `model`, `config`
- Add a basic health-check endpoint `GET /api/health`

**Expected outputs:**
- Application starts without errors
- MySQL connection is established
- Swagger UI accessible at `http://localhost:8080/swagger-ui.html`

---

#### `feature/entities-model` — Sprint 1

**What to implement:**
- Create JPA entity classes:
  - `Product` (id, name, description, price, quantity, category)
  - `Category` (id, name)
  - `Supplier` (id, name, email, phone)
- Add appropriate JPA annotations (`@Entity`, `@Table`, `@Id`, `@GeneratedValue`, relationships)
- Enable Hibernate `spring.jpa.hibernate.ddl-auto=update` so tables are auto-created

**Expected outputs:**
- All three tables auto-created in MySQL on application startup
- No SQL errors in console

---

#### `feature/product-crud` — Sprint 1

**What to implement:**
- `ProductRepository` extending `JpaRepository<Product, Long>`
- `ProductService` with methods: `findAll`, `findById`, `save`, `update`, `deleteById`
- `ProductController` exposing REST endpoints:
  - `GET    /api/products`        — list all products
  - `GET    /api/products/{id}`   — get one product
  - `POST   /api/products`        — create product
  - `PUT    /api/products/{id}`   — update product
  - `DELETE /api/products/{id}`   — delete product
- Proper HTTP status codes and JSON responses

**Expected outputs:**
- All 5 endpoints tested and working via Postman or Swagger
- Returns correct JSON structure with status 200/201/204/404

---

#### `feature/finalization` — Sprint 4

**What to implement:**
- Integration of all features (backend + frontend + security)
- Bug fixes and edge-case handling
- Polish the React UI (loading states, error messages, empty states)
- Environment configuration (`.env` files, CORS settings for production)
- Final end-to-end testing

**Expected outputs:**
- Fully functional application running locally
- All features working together (CRUD + auth)
- Ready for demo or deployment

---

### Maryam

#### `feature/frontend-crud` — Sprint 2

**What to implement:**
- Initialize the React project (`npm create vite@latest frontend` or Create React App)
- Install dependencies: `axios`, `react-router-dom`
- Create pages/components:
  - `ProductList` — fetch and display all products from `GET /api/products`
  - `ProductForm` — reusable Add / Edit form (POST / PUT)
  - `DeleteProduct` — button with confirmation (DELETE)
- Configure `axios` base URL to point to `http://localhost:8080/api`
- Basic CSS styling or a UI library (Bootstrap / Tailwind)

**Expected outputs:**
- Product list displayed in a table
- Add, Edit, Delete actions functional and connected to the Spring Boot API
- App runs on `http://localhost:3000`

---

### Wijdane

#### `feature/security-jwt` — Sprint 3

**What to implement:**
- Add Spring Security dependency in `pom.xml`
- Create `User` entity and `UserRepository`
- Implement `UserDetailsService` for loading users from DB
- JWT utility class: token generation, validation, extraction of claims
- `JwtAuthenticationFilter` (extends `OncePerRequestFilter`)
- `SecurityConfig`: disable CSRF, set stateless session, configure public vs protected routes
- Auth endpoints:
  - `POST /api/auth/register` — register new user
  - `POST /api/auth/login`    — returns JWT token

**Expected outputs:**
- Unauthenticated requests to protected routes return `401 Unauthorized`
- Valid JWT in `Authorization: Bearer <token>` header grants access
- Login endpoint returns a valid JWT

---

## Branching Strategy

```
main ──────────────────────────────────────────────► (production-ready)
  └── develop ──────────────────────────────────────► (integration)
        ├── feature/setup-foundation    (Imane  - Sprint 1)
        ├── feature/entities-model      (Imane  - Sprint 1)
        ├── feature/product-crud        (Imane  - Sprint 1)
        ├── feature/frontend-crud       (Maryam - Sprint 2)
        ├── feature/security-jwt        (Wijdane- Sprint 3)
        └── feature/finalization        (Imane  - Sprint 4)
```

## Workflow Rules

1. **Work only on your assigned branch** — never push directly to `main` or `develop`
2. **When a feature is complete** → open a Pull Request (or merge) into `develop`
3. **`main` is updated only from `develop`** at the end of a sprint or when a stable version is ready
4. **Write meaningful commit messages**: `feat:`, `fix:`, `chore:`, `docs:`

---

## Setup Instructions

### Backend

```bash
cd backend

# Requirements: Java 17+, Maven, MySQL 8 running locally

# 1. Create the database
mysql -u root -p -e "CREATE DATABASE stockdb;"

# 2. Update src/main/resources/application.properties:
#    spring.datasource.url=jdbc:mysql://localhost:3306/stockdb
#    spring.datasource.username=root
#    spring.datasource.password=yourpassword

# 3. Run the application
mvn spring-boot:run
# API available at http://localhost:8080
# Swagger UI at  http://localhost:8080/swagger-ui.html
```

### Frontend

```bash
cd frontend

# Requirements: Node.js 18+

npm install
npm run dev    # or: npm start (if using CRA)
# App available at http://localhost:3000 (or :5173 for Vite)
```

---

## Git Quick Reference

```bash
# Start working on your branch
git checkout feature/your-branch-name
git pull origin feature/your-branch-name

# Save your work
git add .
git commit -m "feat: description of what you did"
git push origin feature/your-branch-name

# Merge feature into develop (when complete)
git checkout develop
git merge feature/your-branch-name
git push origin develop
```
