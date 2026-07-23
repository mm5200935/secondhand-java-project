# Final Project of Advanced Programming Course

## Images of Project

### Login & SignUp
![Login Page](https://raw.githubusercontent.com/mm5200935/secondhand-java-project/refs/heads/main/docs/screenshots/login%20page.JPG "Login Page")

![SignUp Page](https://raw.githubusercontent.com/mm5200935/secondhand-java-project/refs/heads/main/docs/screenshots/sign%20up%20page.JPG "SignUp Page")

### Admin Panel
![Admin Dashboard](https://raw.githubusercontent.com/mm5200935/secondhand-java-project/refs/heads/main/docs/screenshots/admin%20panel/dashboard.JPG "Admin Dashboard")

![Admin Panel Features](https://raw.githubusercontent.com/mm5200935/secondhand-java-project/refs/heads/main/docs/screenshots/admin%20panel/classification%20management.JPG "Admin Panel Features")

![Admin Panel Features](https://raw.githubusercontent.com/mm5200935/secondhand-java-project/refs/heads/main/docs/screenshots/admin%20panel/classification%20management%20_%20edit.JPG "Admin Panel Features")

## Team Members
| # | Name | Tasks                                                                                                                             | Student Number | Email                                                       |
|----------|----------|-----------------------------------------------------------------------------------------------------------------------------------|----------|-------------------------------------------------------------|
| 1 | Mohammad Hasan Mahmoodian | Repository Management (Merge branches, Complete README), Completed API part and JWT in backend, Completed Admin Panel in FrontEnd | 40431051 | [m.mahmoodian@aut.ac.ir](mailto:m.mahmoodian@aut.ac.ir)     |
| 2 | Erfan Simiyari | Completed models, services, database, ... in backend, Complete User Panel Part in Frontend, Build Login page and Register page    | 40431420 | [erfan.simiyari@aut.ac.ir](mailto:erfan.simiyari@aut.ac.ir) |

## How to run project?
* Clone the repository.

### 1. Run Backend
* Open terminal in your preferred Java IDE (such as [IntelliJ IDEA](https://www.jetbrains.com/idea/)).
* Run `cd backend`.
* Run `mvn spring-boot:run`.

Read more about **backend and its features** [here](#).

### 2. Run Frontend
* Open a new terminal in the IDE.
* Run `cd frontend`.
* Run `mvn javafx:run`.
* You can use admin panel with username `admin` and password `admin123`.

Read more about **frontend and its features** [here](#).

## Database
In the project we used SQLite DB to save data.

## Backend & Frontend Connection
In the project we used API to connect frontend to backend, and frontend is not directly connected to database. Frontend sends data to api or get data from api and backend edits database.

## JWT
We used JWT to authenticate users in the project.
