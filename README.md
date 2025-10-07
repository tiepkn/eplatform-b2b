# E-Platform B2B Monorepo (Spring Boot Microservices)

This monorepo contains a B2B e-commerce microservices system built with Java 21, Spring Boot 3.3.x, and Spring Cloud 2023.0.x.

## 🎯 Quick Start

### Using Development Scripts (Recommended)
```bash
# Start all services
./scripts/dev-start.sh

# Start specific service only
./scripts/dev-start.sh inventory

# Check health of all services
./scripts/health-check.sh

# Run all tests
./scripts/test.sh

# Stop all services
./scripts/dev-stop.sh
```

## Modules
- **common**: Shared DTOs, error handling, utilities
- **discovery-server**: Eureka service discovery (port 8761)
- **config-server**: Spring Cloud Config Server (port 8888, native profile by default)
- **api-gateway**: Spring Cloud Gateway (port 8080)
- **auth-service**: Authentication/authorization, JWT resource server (port 8100)
- **catalog-service**: Product catalog (port 8090)
- **inventory-service**: Inventory management (port 8110)
- **order-service**: Orders and carts (port 8120)
- **payment-service**: Payment integration stub (port 8130)

## 🛠️ Technology Stack

- **Java 21** - Latest LTS version
- **Spring Boot 3.2.5** - Latest Spring Boot version
- **Spring Cloud 2023.0.1** - Microservices ecosystem
- **PostgreSQL** - Primary database
- **Apache Kafka** - Event-driven communication
- **Docker** - Containerization
- **Comprehensive Testing** - Unit, Integration, Contract tests

## Requirements
- Java 21
- Maven 3.9+
- PostgreSQL (optional for some services)
- Kafka (optional for messaging)

## Build
```bash
mvn clean install -DskipTests
```

## Run Services

### Option 1: Using Scripts (Recommended)
```bash
# Start all services
./scripts/dev-start.sh

# Start specific service
./scripts/dev-start.sh inventory-service

# Check service health
./scripts/health-check.sh
```

### Option 2: Manual Start
1. **discovery-server** (port 8761)
2. **config-server** (port 8888)
3. **api-gateway** (port 8080)
4. **Other services** (inventory:8110, catalog:8090, order:8120, payment:8130, auth:8100)

## 🧪 Testing

```bash
# Run all tests
./scripts/test.sh

# Run tests for specific service
./scripts/test.sh inventory-service

# Run integration tests only
./scripts/test.sh inventory-service --integration
```

## 🐳 Docker Support

```bash
# Build all Docker images
./scripts/docker-build.sh

# Start with Docker Compose
docker-compose -f docker/docker-compose.yml up -d

# Development environment with hot reload
docker-compose -f docker/docker-compose.dev.yml up -d
```

## 📚 Documentation

- [🏗️ Architecture Guide](docs/architecture.md) - System architecture and design decisions
- [🛠️ Development Setup](docs/development.md) - Complete development environment setup
- [🚀 Deployment Guide](docs/deployment.md) - Production deployment strategies
- [🧪 Testing Guide](docs/testing.md) - Testing strategies and practices
- [🔧 Troubleshooting](docs/troubleshooting.md) - Common issues and solutions

## Development
- Spring Boot DevTools can be added as needed
- Add dependencies per service domain
- Environment-specific configurations supported

## Notes
- Config Server runs with native profile by default
- Messaging (Kafka) and DB (PostgreSQL) integration ready
- Comprehensive test coverage implemented
- Docker containerization support included
