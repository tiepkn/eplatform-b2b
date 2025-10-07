# 🚀 B2B E-commerce Microservices Platform

A modern, scalable B2B e-commerce platform built with Spring Boot 3.2.5, Java 21, and microservices architecture.

## 📋 Overview

This project implements a complete B2B e-commerce solution with the following microservices:

- **API Gateway** - Centralized entry point and load balancer
- **Discovery Server** - Service registry and discovery (Eureka)
- **Config Server** - Centralized configuration management
- **Auth Service** - Authentication and authorization
- **Catalog Service** - Product catalog management
- **Inventory Service** - Stock and reservation management
- **Order Service** - Order processing and management
- **Payment Service** - Payment processing

## 🏗️ Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   API Gateway   │────│  Config Server  │────│ Discovery Server│
│   (Port 8080)   │    │   (Port 8888)   │    │   (Port 8761)   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         └───────────────────────┼───────────────────────┘
                                 │
         ┌───────────────────────┼───────────────────────┐
         │                       │                       │
┌────────▼────────┐    ┌────────▼────────┐    ┌────────▼────────┐
│   Auth Service  │    │ Catalog Service │    │Inventory Service│
│   (Port 8100)   │    │  (Port 8090)    │    │  (Port 8110)    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
┌────────▼────────┐    ┌────────▼────────┐
│  Order Service  │    │Payment Service  │
│   (Port 8120)   │    │  (Port 8130)    │
└─────────────────┘    └─────────────────┘
```

## 🛠️ Technology Stack

### Core Technologies
- **Java 21** - Latest LTS version
- **Spring Boot 3.2.5** - Latest Spring Boot version
- **Spring Cloud 2023.0.1** - Microservices ecosystem

### Data & Messaging
- **PostgreSQL** - Primary database
- **Apache Kafka** - Event-driven communication
- **Redis** - Caching layer (planned)

### Service Communication
- **REST APIs** - HTTP/JSON communication
- **gRPC** - High-performance inter-service calls
- **Spring Cloud OpenFeign** - Declarative HTTP clients

### Infrastructure & DevOps
- **Docker** - Containerization
- **Kubernetes** - Container orchestration (planned)
- **Maven** - Build and dependency management

## 🚀 Quick Start

### Prerequisites
- Java 21+
- Maven 3.8+
- PostgreSQL 15+
- Apache Kafka

### Running Services

1. **Start Infrastructure:**
```bash
mvn spring-boot:run -pl discovery-server
mvn spring-boot:run -pl config-server
```

2. **Start Business Services:**
```bash
mvn spring-boot:run -pl inventory-service
mvn spring-boot:run -pl catalog-service
mvn spring-boot:run -pl order-service
mvn spring-boot:run -pl payment-service
mvn spring-boot:run -pl api-gateway
```

3. **Verify Services:**
- Eureka Dashboard: http://localhost:8761
- API Gateway Health: http://localhost:8080/actuator/health

## 📚 Documentation

- [Development Setup](docs/development.md)
- [Deployment Guide](docs/deployment.md)
- [API Documentation](docs/api-documentation.md)
- [Troubleshooting](docs/troubleshooting.md)

## 🧪 Testing

Run the complete test suite:
```bash
mvn test
```

Run tests for specific service:
```bash
mvn test -pl inventory-service
```

## 🔧 Configuration

Each service can be configured via:
- **application.yml** - Base configuration
- **application-dev.yml** - Development overrides
- **application-prod.yml** - Production overrides
- **Config Server** - Centralized configuration

## 📦 Project Structure

```
📁 eplatform-b2b/
├── 📄 pom.xml (Parent POM)
├── 📁 docs/ (Documentation)
├── 📁 common/ (Shared DTOs and events)
├── 📁 services/
│   ├── 📁 inventory/ (Stock management)
│   ├── 📁 order/ (Order processing)
│   ├── 📁 catalog/ (Product catalog)
│   ├── 📁 payment/ (Payment processing)
│   └── 📁 auth/ (Authentication)
└── 📁 infrastructure/
    ├── 📁 discovery-server/ (Eureka)
    ├── 📁 config-server/ (Spring Cloud Config)
    └── 📁 api-gateway/ (Spring Cloud Gateway)
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass
6. Submit a pull request

## 📄 License

This project is licensed under the MIT License.

## 🆘 Support

For support and questions:
- Create an issue in the repository
- Contact the development team

---

**Built with ❤️ using Spring Boot and modern Java**
