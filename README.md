# finance_syfe
# 💰 Personal Finance Manager

A Spring Boot application to help users manage their personal finances, including income/expense tracking, savings goals, and financial reports.

---

## 🚀 Features

- 🔐 User Registration and Login (Session-based)
- 💸 Track Income and Expense Transactions
- 🗂 Manage Custom and Default Categories
- 🎯 Create and Monitor Savings Goals
- 📊 View Monthly and Yearly Financial Reports

---

## 🛠️ Tech Stack

- Java 17
- Spring Boot 3 (Web, Validation, Session)
- Maven
- H2 In-Memory Database
- RESTful API

---

## 📦 Setup Instructions

### 1. Clone the repository

```bash
git clone https://github.com/your-username/personal-finance-manager.git
cd personal-finance-manager
```

### 2. Build the project

```bash
./mvnw clean install
```

### 3. Run the application

```bash
./mvnw spring-boot:run
```

### 4. Access the application

- API Base URL: `http://localhost:8080/api`
- H2 Console: `http://localhost:8080/h2-console`  
  - JDBC URL: `jdbc:h2:mem:testdb`
  - User: `sa`, Password: (leave blank)

---

## 🔐 Authentication

Session-based login using `HttpSession`. After login, authenticated APIs use `@SessionAttribute("user")`.

---

## 🧪 API Documentation

### 👤 Auth APIs

| Method | Endpoint         | Description              |
|--------|------------------|--------------------------|
| POST   | `/api/auth/register` | Register a new user     |
| POST   | `/api/auth/login`    | Login user              |
| POST   | `/api/auth/logout`   | Logout user             |

---

### 💸 Transaction APIs

| Method | Endpoint                    | Description                          |
|--------|-----------------------------|--------------------------------------|
| POST   | `/api/transactions`         | Create a new transaction             |
| GET    | `/api/transactions`         | Get transactions with optional filters |
| PUT    | `/api/transactions/{id}`    | Update transaction by ID             |
| DELETE | `/api/transactions/{id}`    | Delete transaction by ID             |

**Query Parameters (optional):**
- `startDate`: YYYY-MM-DD  
- `endDate`: YYYY-MM-DD  
- `category`: Category name

---

### 📁 Category APIs

| Method | Endpoint                  | Description                         |
|--------|---------------------------|-------------------------------------|
| GET    | `/api/categories`         | Get all categories (default & custom) |
| POST   | `/api/categories`         | Create a custom category            |
| DELETE | `/api/categories/{name}`  | Delete a custom category by name    |

---

### 🎯 Savings Goals APIs

| Method | Endpoint               | Description                         |
|--------|------------------------|-------------------------------------|
| POST   | `/api/goals`           | Create a new savings goal           |
| GET    | `/api/goals`           | Get all savings goals               |
| GET    | `/api/goals/{id}`      | Get a specific savings goal         |
| PUT    | `/api/goals/{id}`      | Update a savings goal               |
| DELETE | `/api/goals/{id}`      | Delete a savings goal               |

---

### 📊 Reports APIs

| Method | Endpoint                          | Description                |
|--------|-----------------------------------|----------------------------|
| GET    | `/api/reports/monthly/{year}/{month}` | Get monthly report     |
| GET    | `/api/reports/yearly/{year}`      | Get yearly report          |

---

## 🧱 Design Decisions

- Used Spring Boot for fast development with REST APIs.
- Session-based authentication for simplicity and security in a monolithic setup.
- Layered architecture: Controllers → Services → Repositories.
- PostgreSQL used for development; can be replaced with H2/MySQL.

---

## 🧼 License

MIT License
