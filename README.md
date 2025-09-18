# Task-Management

A simple **Task Management (To-Do)**  built with **Spring Boot**.  
It includes **JWT Authentication** to secure the APIs and associates tasks with users.
## Index

- [Technologies Used](#technologies-used)
- [System Features](#system-features-and-use-cases)
- [Entity Relationship Diagram](#Entity-Relationship-Diagram)
- [Screenshots](#Screenshots)
- [Quick Used](#Quick-Start)





## Technologies Used
    - Java 17
    - Java Mail Sender
    - Spring Boot 3
      - Spring Web
      - Spring Data JPA
      - Spring Security (JWT)
    - OpenAPI and Swagger UI Documentation
    - MYSQL
    - Lombok
    - Hibernate
    - Junit 5

## Entity Relationship Diagram

![Entity Relationship Diagram](https://github.com/AmrElhady11/Task-Management/blob/master/assests/ERD.png)


## System Features and Use-Cases


- User registration & login
- Authentication with **JWT Tokens**
- Email verification using **OTP (One-Time Password)** sent to the user’s email
- Full CRUD operations for tasks:
  - Create / Read / Update / Delete
- Each user can only manage their own tasks
- Session management via **JWT Authentication Filter**
- Database integration with **Spring Data JPA**


##  Screenshots

### Swagger - Authentication Endpoints

![Swagger Authentication Endpoints](https://github.com/AmrElhady11/Task-Management/blob/master/assests/ScreenShot1.jpeg)

In addition to **Register, Login**, the app also provides a **Logout API**:

- `GET /api/auth/logout` → Invalidate the JWT token and log out the user.

### Swagger - Account Endpoints

![Swagger Account Endpoints](https://github.com/AmrElhady11/Task-Management/blob/master/assests/ScreenShot2.jpeg)

### Swagger - Tasks Endpoints

![Swagger Tasks Endpoints](https://github.com/AmrElhady11/Task-Management/blob/master/assests/ScreenShot3.jpeg)


## Quick Start

### Clone the Repository
```bash
git clone https://github.com/AmrElhady11/Task-Management.git
cd Task-Management

2. Database Setup
sql-- Login to MySQL
mysql -u root -p

-- Create database
CREATE DATABASE task_management_db;

3. Configure Application
Update src/main/resources/application.properties:
properties# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/task_management_db
spring.datasource.username=taskuser
spring.datasource.password=yourpassword

#Email Configuration
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${MAIL_USERNAME:your-email@gmail.com}
spring.mail.password=${MAIL_PASSWORD:your-app-password}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# Server Configuration
server.port=8080
4. Build and Run
bash# Install dependencies
mvn clean install

# Run the application
mvn spring-boot:run
The application will start at http://localhost:8080




