# 🛠️ Development Setup Guide

This guide will help you set up the development environment for the B2B E-commerce platform.

## 📋 Prerequisites

### Required Software
- **Java 21** or higher
- **Maven 3.8+**
- **PostgreSQL 15+**
- **Apache Kafka**
- **Docker** (optional, for containerized development)

### Development Tools (Recommended)
- **IntelliJ IDEA** or **Eclipse**
- **Postman** or **curl** for API testing
- **pgAdmin** for database management

## 🚀 Quick Setup

### 1. Clone Repository
```bash
git clone <repository-url>
cd eplatform-b2b
```

### 2. Install Dependencies
```bash
mvn clean install -DskipTests
```

### 3. Setup Database

#### Option A: Using Docker (Recommended)
```bash
# Start PostgreSQL and Kafka
docker-compose -f docker/docker-compose.dev.yml up -d

# Verify services are running
docker ps
```

#### Option B: Local Installation
```bash
# PostgreSQL
createdb demo_db

# Kafka (using Confluent Platform)
# Download and start Kafka server on port 9092
```

### 4. Start Services

Start services in order:

```bash
# Terminal 1: Service Discovery
mvn spring-boot:run -pl discovery-server

# Terminal 2: Configuration Server
mvn spring-boot:run -pl config-server

# Terminal 3: Inventory Service (needs database)
mvn spring-boot:run -pl inventory-service

# Terminal 4: Catalog Service
mvn spring-boot:run -pl catalog-service

# Terminal 5: Order Service
mvn spring-boot:run -pl order-service

# Terminal 6: Payment Service
mvn spring-boot:run -pl payment-service

# Terminal 7: Product Service
mvn spring-boot:run -pl product-service

# Terminal 8: Supplier Service
mvn spring-boot:run -pl supplier-service

# Terminal 9: API Gateway
mvn spring-boot:run -pl api-gateway
```

### 5. Verify Setup

#### Check Eureka Dashboard
- URL: http://localhost:8761
- Expected: All services should be registered and show "UP" status

#### Check Service Health
```bash
# API Gateway
curl http://localhost:8080/actuator/health

# Inventory Service
curl http://localhost:8110/actuator/health

# Order Service
curl http://localhost:8120/actuator/health

# Product Service
curl http://localhost:8140/actuator/health

# Supplier Service
curl http://localhost:8150/actuator/health
```

## 🔧 Configuration

### Environment Variables

Create `.env` file in project root:

```env
# Database
DB_HOST=localhost
DB_PORT=5432
DB_NAME=demo_db
DB_USERNAME=postgres
DB_PASSWORD=your_password

# Kafka
KAFKA_BROKERS=localhost:9092

# Services
EUREKA_HOST=localhost
EUREKA_PORT=8761
CONFIG_SERVER_HOST=localhost
CONFIG_SERVER_PORT=8888
```

### Service Profiles

Services use Spring profiles for different environments:

- **dev**: Development (default)
- **test**: Testing
- **prod**: Production

Activate profile:
```bash
mvn spring-boot:run -pl inventory-service -Dspring-boot.run.profiles=dev
```

## 🧪 Testing

### Run All Tests
```bash
mvn test
```

### Run Tests for Specific Service
```bash
mvn test -pl inventory-service
```

### Run Integration Tests Only
```bash
mvn test -pl inventory-service -Dtest=*IntegrationTest
```

## 🐛 Debugging

### Common Issues

#### 1. Port Already in Use
```bash
# Find process using port
lsof -i :8080

# Kill process
kill -9 <PID>
```

#### 2. Database Connection Issues
```bash
# Check PostgreSQL status
pg_isready -h localhost -p 5432

# Check database exists
psql -h localhost -U postgres -l
```

#### 3. Kafka Connection Issues
```bash
# Check Kafka is running
nc -vz localhost 9092

# List topics
kafka-topics --bootstrap-server localhost:9092 --list
```

### Enable Debug Logging

Add to `application.yml`:
```yaml
logging:
  level:
    com.eplatform.b2b: DEBUG
    org.springframework.web: DEBUG
```

## 📚 Development Workflow

### 1. Create Feature Branch
```bash
git checkout -b feature/new-functionality
```

### 2. Make Changes
- Follow existing code style
- Add tests for new functionality
- Update documentation if needed

### 3. Run Tests
```bash
mvn test
```

### 4. Build Project
```bash
mvn clean compile
```

### 5. Submit Pull Request
- Ensure all tests pass
- Update CHANGELOG.md if needed
- Request code review

## 🔍 Monitoring

### Health Endpoints
- **API Gateway:** http://localhost:8080/actuator/health
- **Inventory:** http://localhost:8110/actuator/health
- **Order:** http://localhost:8120/actuator/health

### Useful Endpoints
- **Metrics:** `/actuator/metrics`
- **Info:** `/actuator/info`
- **Config Props:** `/actuator/configprops`
- **Environment:** `/actuator/env`

## 🚀 Hot Reloading

For faster development, use Spring Boot DevTools:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <scope>runtime</scope>
    <optional>true</optional>
</dependency>
```

Enable in IDE or run with:
```bash
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.devtools.restart.enabled=true"
```

## 📝 Best Practices

### Code Organization
- Follow package structure: `com.eplatform.b2b.service-name`
- Use domain-driven design patterns
- Write comprehensive unit tests
- Document public APIs

### Git Practices
- Use meaningful commit messages
- Create feature branches for new work
- Keep branches up to date with main
- Use pull requests for code review

### Database Practices
- Use migrations for schema changes
- Write database tests
- Monitor query performance
- Use connection pooling

---

Happy coding! 🎉
