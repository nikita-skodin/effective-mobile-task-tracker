# Task Tracker API

Task Tracker API is a RESTful API for task management, built using Java 17, Spring Boot, and PostgreSQL. This project supports easy deployment via Docker Compose.

## Prerequisites

Before you begin, ensure that the following tools are installed on your computer:

- [Git](https://git-scm.com/)
- [Docker](https://www.docker.com/)
- [Docker Compose](https://docs.docker.com/compose/)

## How to Run the Project

### 1. Clone the Repository

Clone the repository with the source code to your local machine:

```bash
git clone https://github.com/nikita-skodin/effective-mobile-task-tracker.git
```

Go to the project folder

```bash
cd effective-mobile-task-tracker
```

### 2. Build the Project

Build the project to create a `.jar` file. Make sure you are using Maven:

```bash
./mvnw clean package -DskipTests
```

Or, if Maven is installed globally:

```bash
mvn clean package -DskipTests
```

### 3. Run the Project Using Docker Compose

Use Docker Compose to start the application and the PostgreSQL database:

```bash
docker-compose up --build
```

### Additional Information

For convenience, a Postman collection is included in the root of the project. You can use it to test the API endpoints easily. Import the collection into Postman and start testing!