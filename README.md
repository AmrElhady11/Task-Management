# Task-Management

A simple **Task Management (To-Do)**  built with **Spring Boot**.  
It includes **JWT Authentication** to secure the APIs and associates tasks with users.
## Index

- [Technologies Used](#technologies-used)
- [System Features](#system-features-and-use-cases)




## Technologies Used
    - **Java 17**
    - **Spring Boot 3**
      - Spring Web
      - Spring Data JPA
      - Spring Security (JWT)
    - OpenAPI and Swagger UI Documentation
    - **MYSQL**
    - **Lombok**
    - **Hibernate**

## Entity Relationship Diagram

![Entity Relationship Diagram](https://github.com/AmrElhady11/Task-Management/blob/master/assests/ERD.png)


## System Features and Use-Cases


- User registration & login
- Authentication with **JWT Tokens**
- Full CRUD operations for tasks:
  - Create / Read / Update / Delete
- Each user can only manage their own tasks
- Session management via **JWT Authentication Filter**
- Database integration with **Spring Data JPA**


##  Screenshots

### Swagger - Authentication Endpoints

![Swagger Authentication Endpoints](https://github.com/AmrElhady11/Task-Management/blob/master/assests/ScreenShot1.jpeg)

In addition to **Register, Login, and Task APIs**, the app also provides a **Logout API**:

- `POST /api/auth/logout` → Invalidate the JWT token and log out the user.

### Swagger - Account Endpoints

![Swagger Account Endpoints](https://github.com/AmrElhady11/Task-Management/blob/master/assests/ScreenShot2.jpeg)

### Swagger - Tasks Endpoints

![Swagger Tasks Endpoints](https://github.com/AmrElhady11/Task-Management/blob/master/assests/ScreenShot3.jpeg)





