# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build and Run Commands
- Backend: `cd backend && ./mvnw spring-boot:run -Dspring-boot.run.profiles=simple`
- Frontend: `cd frontend && npm install && npm start`
- Build all: `invoke build` or `python tasks.py build`
- Docker build: `docker compose up --build`
- Run tests: `cd backend && ./mvnw test`
- Run single test: `cd backend && ./mvnw test -Dtest=TestClassName#testMethodName`

## Code Style Guidelines
- Java: Standard Java conventions with Spring Boot best practices, Java 17 features
- TypeScript/React: Functional components with hooks, TypeScript interfaces for types
- Package structure: Follow existing module structure in de.greenstones.gsmr.msc.*
- Error handling: Use ApplicationException for backend errors
- Schema definitions: Follow builder pattern shown in ConfigTypeBuilder
- Frontend components: Follow React Function Component pattern with React Bootstrap styling