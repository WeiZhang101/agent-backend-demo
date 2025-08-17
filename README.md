# Agent Backend Demo

A Spring Boot-based backend demonstration project showcasing best practices for modern Java web applications.

## 📋 Project Overview

This is a backend API demonstration project built with Spring Boot 3.5.0, integrating modern technology stacks such as JPA data access, PostgreSQL database, and Flyway database migration.

## 🛠 Technology Stack

- **Java**: 21
- **Spring Boot**: 3.5.0
- **Database**: PostgreSQL (Production) / H2 (Testing)
- **ORM**: Spring Data JPA
- **Database Migration**: Flyway
- **Build Tool**: Gradle 8.13
- **Containerization**: Docker Compose
- **Development Tools**: Lombok

## 📋 Prerequisites

Before getting started, ensure your development environment meets the following requirements:

- **Java**: JDK 21 or higher
- **Docker**: For running PostgreSQL database
- **Git**: For cloning the project

## 🚀 Quick Start

### 1. Clone the Repository

```bash
git clone git@github.com:WeiZhang101/agent-backend-demo.git
cd agent-backend-demo
git checkout spdd-practice
```

### 2. Database Initialization

Start PostgreSQL database using Docker Compose:

```bash
docker-compose -f compose.yaml up -d
```

This will start a PostgreSQL container with the following configuration:
- Database name: `agent-backend`
- Username: `postgres`
- Password: `postgres`
- Port: `5432`

### 3. Create Package Structure

Create the project package structure:
- Create controller package
- Create dto package
- ..., etc.

### 4. Check out the story in the requirement folder
- **Background**: Context and rationale
- **Business Value**: Expected benefits and impact
- **Scope In**: What will be included
- **Scope Out**: What will be excluded
- **Acceptance Criteria (ACs)**: Using Given-When-Then format, with each AC having a descriptive subheading

### 5. Configuration File Generation

Generate configuration files based on prompts (e.g., `@0000-202502252021-[Init]-[Feat]-Set-up-database.md`)

### 6. API Creation Prompt Generation

Based on business context and the "API-CREATION-TEMPLATE.md" in the src/main/resources/prompts/template, generate prompt files in the `@/prompts implementation` folder using `@create-agent.md`

### 7. Field Type Fine-tuning

Update models with corresponding properties, adjusting the implementation prompt accordingly

### 8. Package Information Updates

Based on current package structure, update corresponding package information in implementation prompts

### 9. Database Migration (Optional)

Generate DB migration prompts based on EntityPO and `@DB-MIGRATION-TEMPLATE.md` in the `@/implementation` folder

### 10. Code Generation

Follow implementation prompts to generate corresponding code in existing package structure (excluding tests). When fixing lint issues, maintain the original implementation approach.

### 11. Test Prompt Generation

Based on implementation details, generate test prompt files using the "TEST-SCENARIOS-TEMPLATE-EN.md" in src/main/resources/prompts/template

### 12. Test Code Generation

Generate corresponding test code based on the generated test prompts

### 13. Project Verification

Run the project and verify all scenarios work correctly

### 14. Build and Run Application

```bash
./gradlew build
./gradlew bootRun
```

The application will start at `http://localhost:8080`.

## 🔧 Development Guide

### Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── org/tw/agent_backend_demo/
│   │       └── AgentBackendDemoApplication.java  # Main application class
│   └── resources/
│       ├── application.properties                # Application configuration
│       ├── db/migration/                        # Flyway database migration scripts
│       ├── static/                              # Static resources
│       └── templates/                           # Template files
└── test/
    └── java/                                    # Test code
```

### Database Configuration

The project supports two database configurations:

1. **Development Environment**: PostgreSQL (via Docker Compose)
2. **Testing Environment**: H2 in-memory database

### Adding Database Migration

Create Flyway migration scripts in the `src/main/resources/db/migration/` directory:

```
V1__Create_initial_tables.sql
V2__Add_user_table.sql
```

### Running Tests

```bash
./gradlew test
```

## 🐳 Docker Deployment

### Build Docker Image

```bash
./gradlew bootBuildImage
```

### Deploy with Docker Compose

```bash
docker-compose up
```

## 📝 API Documentation

After starting the application, you can access the API through:

- Application: `http://localhost:8080`
- Health Check: `http://localhost:8080/actuator/health` (if Actuator is enabled)

## 🔍 Troubleshooting

### Database Connection Issues

If you encounter database connection problems, please check:

1. Whether Docker containers are running: `docker ps`
2. Whether PostgreSQL port is occupied: `lsof -i :5432`
3. Whether database configuration is correct

### Build Failures

If the build fails, try:

1. Clean build cache: `./gradlew clean`
2. Rebuild: `./gradlew build --refresh-dependencies`

## 🤝 Contributing

1. Fork the project
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the [MIT License](LICENSE).

## 📞 Contact

For questions or suggestions, please contact us through:

- Project Issues: [GitHub Issues](../../issues)

---

**Note**: This is a demonstration project for learning and development reference only. Please ensure appropriate security configuration and performance optimization before using in production environment. 