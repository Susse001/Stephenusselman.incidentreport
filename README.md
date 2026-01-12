# Stephenusselman.incidentreport

# Incident Service

A simple Spring Boot REST API for managing incidents, backed by DynamoDB.  
This project demonstrates a full-stack backend deployed on AWS Elastic Beanstalk.

---

## Features

- Create, retrieve, and search incidents
- Query by severity or category
- Pagination support
- DynamoDB integration with GSIs for efficient queries
- AWS-ready configuration with profiles for local vs production
- Deployed backend live on AWS Elastic Beanstalk

---

## Tech stack

- Java 17 with Spring Boot 3.5.9
- Jakarta Validation + Lombok
- AWS Elastic Beanstalk & DynamoDB
---

## API Endpoints

Base URL: "http://incidentservice-susse-env.eba-vipmqwyp.us-east-2.elasticbeanstalk.com/api/incidents
Local URL: http://localhost:8080/api/incident

| Method | Endpoint | Description |
|--------|---------|-------------|
| POST | `/` | Create a new incident |
| GET | `/{id}` | Retrieve an incident by ID |
| GET | `/` | Search incidents by `severity` or `category` (paginated) |

Example POST Request:

POST /api/incidents
Content-Type: application/json

{
  "description": "Database connection timeout",
  "reportedBy": "Stephen Usselman",
  "severity": "HIGH",
  "category": "DATABASE"
}

Example GET Request:

GET /api/incidents?severity=HIGH&limit=10

--
