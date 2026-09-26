# Spot B.Tech Admission Backend

Backend system for managing the **Spot Round admission process** for B.Tech programs, including student registration, document handling, payment tracking, merit processing, and automated seat allocation.

## Overview

The system was developed to support the college's Spot Round admission workflow and reduce manual processing of student data and seat allocation.

It provides backend services for:

* Student Spot Round registration
* Document submission and management
* Payment record management
* Merit-list processing
* Seat-matrix generation
* Automated seat allocation based on admission rules
* Candidate and admission data management

## Tech Stack

* **Java**
* **Spring Boot**
* **PostgreSQL**
* **REST APIs**
* **Maven**
* **Docker**

## Key Features

### Spot Registration

Handles student registration and maintains candidate information required for the Spot Round admission process.

### Payment Management

Maintains payment records and payment status associated with student registrations.

### Document Management

Stores and manages submitted admission documents and their associated candidate records.

### Merit List Processing

Processes candidate merit information and generates the required data for Spot Round allocation.

### Seat Allocation

Implements the admission rules and eligibility conditions required for Spot Round seat allocation.

The allocation system considers factors such as:

* Candidate merit
* Category
* Seat availability
* Eligibility
* Course/branch preferences
* Applicable admission rules

### Seat Matrix Generation

Generates the available seat matrix required by the allocation process and keeps track of seat availability during allocation.

## System Flow

```text
Student Registration
        ↓
Document Submission
        ↓
Payment Verification
        ↓
Merit List Processing
        ↓
Seat Matrix Generation
        ↓
Eligibility & Allocation Rules
        ↓
Seat Allocation
        ↓
Final Admission Data
```

## Project Structure

```text
src/
└── main/
    ├── java/
    │   └── ...
    └── resources/
        └── application.properties
```

The backend follows a layered architecture separating API handling, business logic, and data access.

## Database

The application uses **PostgreSQL** for persistent storage.

Major data areas include:

* Student registrations
* Merit lists
* Payment records
* Documents
* Candidate information
* Seat availability
* Allocation records

## Running Locally

### Prerequisites

Make sure the following are installed:

* Java 17+
* Maven
* PostgreSQL

### Clone the Repository

```bash
git clone https://github.com/Harshmahajan29/Spot_Btech_Backend.git
cd Spot_Btech_Backend
```

### Configure Database

Create a PostgreSQL database and configure the database connection in your local configuration.

Do **not** commit production database credentials or payment credentials t
