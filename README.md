# Organization Scoring Service

## Description

A service for automated organization scoring based on Camunda DMN decisions, with scoring results stored in Elasticsearch.

## Technologies

- Java 11
- Spring Boot 2.7
- Camunda BPM 7.17
- Elasticsearch 7.17
- H2 Database (for Camunda)
- Docker + Docker Compose
- OpenAPI (Swagger)

## Prerequisites

- Docker and Docker Compose installed
- Maven installed (optional, for manual build)

## Quick Start

### 1. Clone the Repository

```bash
git clone https://github.com/your-repo/test_task.git
cd test_task
```

### 2. Build the Project

```bash
mvn clean package
```

### 3. Start the Application

```bash
docker-compose up --build
```

## Accessing Services

| Service            | URL                                                   | Credentials              |
|--------------------|-------------------------------------------------------|--------------------------|
| Camunda WebApp     | [http://localhost:8080/api/v1](http://localhost:8080/api/v1) | Username: `username`<br>Password: `password` |
| Swagger UI         | [http://localhost:8080/api/v1/swagger-ui/index.html](http://localhost:8080/api/v1/swagger-ui/index.html) | -                        |
| Elasticsearch      | [http://localhost:9200](http://localhost:9200)        | -                        |

## Stopping the Application

To stop and remove the containers:

```bash
docker-compose down
```

## Admin User Configuration

The Camunda admin user is automatically created at application startup with the following credentials:

| Field        | Value      |
|--------------|------------|
| Username (ID)| `username` |
| Password     | `password` |
| First Name   | Test       |
| Last Name    | User       |

These credentials are configurable in `application.yaml` under the `camunda.bpm.admin-user` section.

## API Endpoints

- **POST /scoring**
    - Submit organization data for scoring evaluation.

- **GET /scoring/{orgName}**
    - Retrieve scoring results by organization name.

API documentation and testing interface are available via the Swagger UI.

