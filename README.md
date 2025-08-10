# Beauty Salon Management System

A comprehensive beauty salon management application built with modern technologies.

## Technology Stack

- **Frontend**: React 18 with TypeScript
- **Backend**: Java 24 with Spring Boot 3.5
- **Database**: Apache Cassandra
- **Containerization**: Docker & Docker Compose

## Features

- Customer management
- Appointment scheduling
- Service management
- Staff management
- Payment tracking
- Dashboard analytics

## Getting Started

### Prerequisites

- Docker and Docker Compose
- Node.js 18+ (for local development)
- Java 24 (for local development)

### Running the Application

1. Clone the repository
2. Run with Docker Compose:
   ```bash
   docker-compose up -d
   ```

3. Access the application:
   - Frontend: http://localhost:3000
   - Backend API: http://localhost:8080
   - Cassandra: localhost:9042

## Project Structure

```
beauty-salon-app/
├── frontend/          # React application
├── backend/           # Spring Boot application
├── database/          # Cassandra schema and scripts
├── docker-compose.yml # Docker orchestration
└── README.md
```

## Development

Each service can be run independently for development purposes. See individual README files in each directory for specific instructions.
