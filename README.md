# Spot B.Tech Admission Backend

Backend system developed to support the **B.Tech Spot Round admission process**, including student registration, document management, payment tracking, merit-list processing, seat-matrix generation, and automated seat allocation.

The system was developed to reduce manual processing and provide a structured backend for managing Spot Round admission data and allocation workflows.

## Overview

The application provides backend services for:

* Student Spot Round registration
* Candidate data management
* Document submission and tracking
* Payment record management
* Merit-list processing
* Seat-matrix generation
* Eligibility validation
* Automated seat allocation
* Admission data management

## Tech Stack

| Technology                  | Purpose                            |
| --------------------------- | ---------------------------------- |
| Java                        | Backend development                |
| Spring Boot                 | REST API and application framework |
| PostgreSQL                  | Persistent data storage            |
| Spring Data JPA / Hibernate | Database access                    |
| Maven                       | Build and dependency management    |
| Docker                      | Containerization                   |

## Key Features

### Spot Round Registration

Handles student registration and maintains candidate information required during the Spot Round admission process.

### Payment Management

Maintains payment records and payment status associated with student registrations.

### Document Management

Handles submitted admission documents and associates them with the corresponding candidate records.

### Merit List Processing

Processes candidate merit information and prepares the required data for the Spot Round allocation process.

### Seat Matrix Generation

Generates and maintains seat availability based on the configured courses, categories, and admission requirements.

### Seat Allocation

Implements the admission rules and eligibility conditions required for Spot Round seat allocation.

The allocation process considers factors including:

* Candidate merit
* Category
* Seat availability
* Candidate eligibility
* Course and branch preferences
* Applicable admission rules

## System Flow

```text
Student Registration
        |
        v
Document Submission
        |
        v
Payment Verification
        |
        v
Merit List Processing
        |
        v
Seat Matrix Generation
        |
        v
Eligibility Validation
        |
        v
Seat Allocation
        |
        v
Final Admission Data
```

## Architecture

The backend follows a layered architecture that separates request handling, business logic, and database operations.

```text
Client
  |
  v
REST Controllers
  |
  v
Service Layer
  |
  v
Repository Layer
  |
  v
PostgreSQL
```

This separation keeps admission logic independent from API and database implementation details.

## Project Structure

```text
src/
└── main/
    ├── java/
    │   └── ...
    └── resources/
        ├── application.properties
        └── ...
```

The application follows a standard Spring Boot structure with controllers, services, entities, repositories, and supporting components.

## Database

The application uses **PostgreSQL** for persistent storage.

Major data areas include:

* Student registrations
* Merit lists
* Payment records
* Submitted documents
* Candidate information
* Seat availability
* Allocation records

## Prerequisites

Before running the project locally, install:

* Java 17 or higher
* Maven 3.8+
* PostgreSQL
* Docker (optional)

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/Harshmahajan29/Spot_Btech_Backend.git
cd Spot_Btech_Backend
```

### 2. Configure PostgreSQL

Create a PostgreSQL database for the application.

Example:

```sql
CREATE DATABASE spot_btech;
```

Configure the database connection in your local `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/spot_btech
spring.datasource.username=<your-username>
spring.datasource.password=<your-password>

spring.jpa.hibernate.ddl-auto=update
```

Use your actual database configuration locally.

### 3. Build the Application

```bash
mvn clean install
```

### 4. Run the Application

```bash
mvn spring-boot:run
```

Or run the main Spring Boot application class directly from your IDE.

## Docker

The application can also be containerized using Docker.

Build the image:

```bash
docker build -t spot-btech-backend .
```

Run the container:

```bash
docker run -p 8080:8080 spot-btech-backend
```

Database configuration should be supplied through environment variables or an external configuration rather than being hardcoded into the Docker image.

## Configuration and Security

Sensitive configuration should not be committed to the repository.

Do not commit:

* Database passwords
* Production database URLs
* Payment gateway credentials
* API keys
* Access tokens
* Other production secrets

Use environment variables or a local configuration file for sensitive values.

A template configuration file can be provided as:

```text
application.properties.example
```

while the actual:

```text
application.properties
```

remains excluded through `.gitignore`.

## Admission Allocation

The seat allocation module processes candidates according to the configured admission rules.

Conceptually:

```text
Candidate Merit
       |
       v
Eligibility Check
       |
       v
Category / Reservation Rules
       |
       v
Available Seats
       |
       v
Course Preferences
       |
       v
Seat Allocation
```

The allocation process updates seat availability as seats are assigned to eligible candidates.

## Deployment

The backend is designed to support containerized deployment using Docker.

Production configuration should be supplied externally through environment variables or deployment-level secrets rather than being stored in source control.

## Project Purpose

The project was developed to support the college's B.Tech Spot Round admission workflow and automate parts of:

* Candidate registration
* Payment tracking
* Merit processing
* Seat-matrix generation
* Eligibility validation
* Seat allocation

The system aims to reduce repetitive manual processing and provide a centralized backend for Spot Round admission operations.

## Author

**Harsh Mahajan**

B.Tech Information Technology
Walchand College of Engineering, Sangli
