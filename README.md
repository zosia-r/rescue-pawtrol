# Rescue Pawtrol

Web application for shelter operations: animal registry, kennel occupancy and medical records.

## Repository Overview

This repository contains a full-stack application:

- Frontend: Vue 3
- Backend: Spring Boot (Java), Spring Security (JWT)
- Database: PostgreSQL

## Project Structure

```text
.
├── backend/    # Spring Boot REST API
├── frontend/   # Vue single-page app
└── docker-compose.yml
```

## Run With Docker

Start all services:

```bash
docker compose up --build
```

Services and ports:

- Frontend: http://localhost
- Backend API: http://localhost:8080
- PostgreSQL: localhost:5434

Stop services:

```bash
docker compose down
```

Stop services and remove database volume:

```bash
docker compose down -v
```

## Authentication

- Login endpoint: `POST /api/auth/login`
- Other API endpoints require JWT Bearer token
- Frontend stores token in local storage under `jwt_token`

## Main API Areas

- `/api/auth` - authentication
- `/api/animals` - animals and kennel assignment
- `/api/kennels` - kennel data
- `/api/medical-records` - medical records

## Notes

The project is a work in progress, expect updates and improvements over time.

## Authors
💗 Alicja Rembisz 
💗 Zofia Różańska