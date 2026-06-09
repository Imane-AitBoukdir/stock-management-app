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

### Sprint 2 Delivery - Entities and CRUD Interface

**Objective:**
Complete the stock model and deliver a functional CRUD application with Spring Boot + React, without authentication/security.

**Backend completed:**
- `Product`, `Category`, and `Supplier` JPA entities.
- `Category` to `Product`: one-to-many / many-to-one relation.
- `Product` to `Supplier`: many-to-many relation using join table `product_suppliers`.
- CRUD repositories: `ProductRepository`, `CategoryRepository`, `SupplierRepository`.
- CRUD services: `ProductService`, `CategoryService`, `SupplierService`.
- REST controllers: `ProductController`, `CategoryController`, `SupplierController`.
- Health endpoint: `GET /api/health`.

**Backend endpoints:**

```text
GET    /api/products
GET    /api/products/{id}
POST   /api/products
PUT    /api/products/{id}
DELETE /api/products/{id}

GET    /api/categories
GET    /api/categories/{id}
POST   /api/categories
PUT    /api/categories/{id}
DELETE /api/categories/{id}

GET    /api/suppliers
GET    /api/suppliers/{id}
POST   /api/suppliers
PUT    /api/suppliers/{id}
DELETE /api/suppliers/{id}
```

**Product JSON example:**

```json
{
  "name": "Laptop",
  "description": "HP EliteBook",
  "price": 8500,
  "quantity": 10,
  "categoryId": 1,
  "supplierIds": [1, 2]
}
```

**DTO architecture added:**
- Controllers no longer expose JPA entities directly.
- Request DTOs are used for incoming JSON:
  - `ProductRequestDTO`
  - `CategoryRequestDTO`
  - `SupplierRequestDTO`
- Response DTOs are used for API output:
  - `ProductResponseDTO`
  - `CategoryResponseDTO`
  - `SupplierResponseDTO`
- `StockMapper` converts between DTOs and JPA entities.
- Product creation/update uses relation ids (`categoryId`, `supplierIds`) instead of nested JPA objects.

**Frontend completed:**
- React + Vite application.
- React Router navigation: `/products`, `/products/:id`, `/categories`, `/suppliers`.
- Axios API layer: `productApi.js`, `categoryApi.js`, `supplierApi.js`, `axiosConfig.js`.
- Reusable components: `DataTable`, `FormModal`, `SearchBar`, `ConfirmDeleteModal`, `Navbar`.
- CRUD pages: `ProductsPage`, `ProductDetailPage`, `CategoriesPage`, `SuppliersPage`.
- Basic responsive CSS styling.

**Sprint 2 verification:**

```bash
# Backend
cd backend
mvn test
mvn spring-boot:run

# Frontend
cd frontend
npm install
npm run build
npm run dev
```

**Expected result:**
- Backend runs on `http://localhost:8080`.
- Frontend runs on `http://localhost:5173`.
- Products, categories, and suppliers can be listed, created, edited, deleted, and searched.
- Product detail page shows category and suppliers.
- No Spring Security or JWT is required for Sprint 2.

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

## How to Run the Project

### Prerequisites

| Tool      | Version  | Purpose                      |
|-----------|----------|------------------------------|
| Java      | 17+      | Spring Boot backend runtime  |
| Maven     | 3.8+     | Backend dependency management|
| MySQL     | 8+       | Application database         |
| Node.js   | 18+      | React frontend runtime       |
| npm       | 9+       | Frontend dependency management|

### Step 1 — Create the MySQL database

```bash
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS stockdb;"
```

### Step 2 — Configure the backend environment

Copy the example environment file and edit it with your credentials:

```bash
cd backend
cp .env.example .env
```

Edit `.env` with your values:

```properties
DB_URL=jdbc:mysql://localhost:3306/stockdb?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
DB_USERNAME=root
DB_PASSWORD=your_mysql_password
APP_CORS_ALLOWED_ORIGINS=http://localhost:5173,http://localhost:3000
APP_JWT_SECRET=replace-with-a-base64-encoded-secret-at-least-32-bytes-long
APP_JWT_EXPIRATION_MS=86400000
```

> **Note:** If you prefer not to use a `.env` file, `application.properties` already contains safe defaults for local development. You only need to ensure your MySQL password matches (`mysql` by default).

### Step 3 — Start the backend

```bash
cd backend
mvn spring-boot:run
```

The backend starts on **http://localhost:8080**. You can verify it is running:

| URL                                    | Description            |
|----------------------------------------|------------------------|
| `http://localhost:8080/api/health`      | Health check endpoint  |
| `http://localhost:8080/swagger-ui.html` | Swagger API docs       |

### Step 4 — Configure the frontend environment (optional)

```bash
cd frontend
cp .env.example .env
```

The `.env` file contains:

```properties
VITE_API_BASE_URL=http://localhost:8080/api
```

> This is only needed if your backend runs on a different host or port. The default value is already set in `axiosConfig.js`.

### Step 5 — Start the frontend

```bash
cd frontend
npm install
npm run dev
```

The frontend starts on **http://localhost:5173**.

### Step 6 — Use the application

1. Open **http://localhost:5173** in your browser.
2. You will be redirected to the **login page**.
3. Click **Register** to create your first user account (username + password).
4. After registration, you are automatically logged in and redirected to the **Products** dashboard.
5. From the sidebar, navigate between **Products**, **Categories**, and **Suppliers** to manage your stock.

### Quick start (all commands)

```bash
# Terminal 1 — Backend
cd backend
mvn spring-boot:run

# Terminal 2 — Frontend
cd frontend
npm install
npm run dev
```

### One-click backend startup (Windows)

A convenience script is provided to check prerequisites, create the database, and start the backend in one command:

```bash
# From the project root
start-backend.bat
```

The script will:
- Verify that Java, Maven, and MySQL are installed
- Load credentials from `backend/.env` if it exists
- Create the `stockdb` database if it does not exist
- Start the Spring Boot server

### Running tests

```bash
cd backend
mvn test
```

### Building the frontend for production

```bash
cd frontend
npm run build    # Output in frontend/dist/
npm run preview  # Preview the production build locally
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
