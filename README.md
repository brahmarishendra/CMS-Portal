# CMS-Portal: College and School Management Portal

Collage and School management portal using Vue.js and Java JDBC (JPA and DTO), MySQL.

---

## 🚀 Tech Stack

### Backend
- **Core Language:** Java 17
- **Framework:** Spring Boot 3.x / 4.x
- **ORM & Data Access:** Spring Data JPA / Hibernate & JDBC (JPA and DTO patterns)
- **Database:** MySQL
- **Build System:** Gradle
- **Boilerplate Reduction:** Lombok

### Frontend
- **Framework:** Vue 3
- **Build Tool:** Vite
- **Language:** TypeScript
- **State/Types:** TypeScript compiler configurations for Vite & Node
- **Styling:** Vanilla CSS / custom styling

---

## 📁 Repository Structure

```text
├── SMS/                     # Spring Boot Backend
│   ├── src/                 # Backend source code
│   │   ├── main/java/       # Controller, Service, POJO, JPA classes
│   │   └── main/resources/  # application.properties & resources
│   ├── build.gradle         # Gradle project configuration
│   └── gradlew              # Gradle wrapper script
│
├── Frontend/                # Vue.js Frontend
│   └── sms/                 # Main Vue application
│       ├── src/             # Frontend source code (components, views, styles)
│       ├── package.json     # NPM package dependencies
│       └── vite.config.ts   # Vite configuration
│
└── README.md                # Project documentation
```

---

## 🛠️ Getting Started

### Prerequisites
- **Java Development Kit (JDK) 17** or higher
- **Node.js** (v20+ recommended)
- **MySQL Database Server**

---

### 1. Backend Setup

1. **Create MySQL Database**:
   Log into MySQL and create a database named `my_school_db`:
   ```sql
   CREATE DATABASE my_school_db;
   ```

2. **Configure Database Connection**:
   Open [SMS/src/main/resources/application.properties](file:///c:/Users/vikrant%20mahar/Downloads/SMS/SMS/src/main/resources/application.properties) and update the database username/password if needed:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/my_school_db
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

3. **Run the Spring Boot Application**:
   Navigate to the `SMS` directory and execute:
   ```bash
   # On Windows
   .\gradlew.bat bootRun

   # On Linux/macOS
   ./gradlew bootRun
   ```

---

### 2. Frontend Setup

1. **Navigate to the frontend folder**:
   ```bash
   cd Frontend/sms
   ```

2. **Install Dependencies**:
   ```bash
   npm install
   ```

3. **Run the Development Server**:
   ```bash
   npm run dev
   ```
   Open your browser and navigate to `http://localhost:5173/` (or the port specified by Vite).

---

## 💡 Key Features

1. **Authentication (Sign In / Registration)**: Secure custom login for Admins, Staff, and Students.
2. **Student Management**: Dynamic student enrollment, profiles, and active lists.
3. **Staff Management**: Detailed staff member details and department categorization.
4. **Fees Tracking**: Logs student fees payment status.
5. **Results / Academic Grades**: Record and view subject-wise results for students.
6. **Staff Salary Management**: Track employee payroll and salaries.

