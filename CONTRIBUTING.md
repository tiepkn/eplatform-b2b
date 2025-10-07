# 📝 Contributing Guide

Welcome to the B2B E-commerce Microservices Platform! We welcome contributions from everyone.

## 🚀 Quick Start for Contributors

### 1. Development Setup
```bash
# Fork and clone the repository
git clone https://github.com/your-username/eplatform-b2b.git
cd eplatform-b2b

# Start development environment
./scripts/dev-start.sh

# Run tests
./scripts/test.sh
```

### 2. Create Feature Branch
```bash
git checkout -b feature/amazing-feature
```

### 3. Make Changes
- Follow existing code style and patterns
- Add tests for new functionality
- Update documentation if needed

### 4. Test Your Changes
```bash
# Run all tests
./scripts/test.sh

# Run specific service tests
./scripts/test.sh inventory-service

# Check service health
./scripts/health-check.sh
```

## 📋 Contribution Guidelines

### Code Standards
- **Java 21** with modern syntax
- **Spring Boot 3.2.5** conventions
- **Domain-Driven Design** patterns
- **Comprehensive testing** (unit + integration)

### Commit Conventions
```
feat: add new inventory reservation feature
fix: resolve inventory deadlock issue
docs: update API documentation
test: add integration tests for order service
refactor: improve reservation pattern implementation
```

### Pull Request Process
1. **Create feature branch** from `main`
2. **Make focused changes** with clear purpose
3. **Add tests** for new functionality
4. **Update documentation** if needed
5. **Run full test suite** before submitting
6. **Request review** from maintainers

## 🏗️ Project Structure

```
📁 eplatform-b2b/
├── 📁 services/ (Business logic services)
│   ├── inventory-service/ (Stock management)
│   ├── order-service/ (Order processing)
│   └── payment-service/ (Payment handling)
├── 📁 infrastructure/ (Infrastructure services)
│   ├── discovery-server/ (Eureka)
│   ├── config-server/ (Spring Cloud Config)
│   └── api-gateway/ (Spring Cloud Gateway)
├── 📁 common/ (Shared code)
└── 📁 docs/ (Documentation)
```

## 🧪 Testing Requirements

### Unit Tests
- **Coverage**: Minimum 80% for new code
- **Mocking**: Use Mockito for external dependencies
- **Assertions**: Use AssertJ for fluent assertions

### Integration Tests
- **Database**: Use test containers or embedded DB
- **Kafka**: Use embedded Kafka for testing
- **HTTP**: Use MockMvc for REST API testing

### Example Test Structure
```java
@Test
void shouldReserveStockSuccessfully() {
    // Given
    ProductStock stock = new ProductStock("TEST-SKU", 100);

    // When
    boolean result = stock.reserve(10);

    // Then
    assertThat(result).isTrue();
    assertThat(stock.getAvailable()).isEqualTo(90);
}
```

## 📚 Documentation Requirements

### Code Documentation
- **JavaDoc** for public APIs
- **README.md** for each service
- **Architecture decisions** documented

### API Documentation
- **OpenAPI/Swagger** specs for REST endpoints
- **gRPC service** definitions documented
- **Integration patterns** explained

## 🔧 Development Workflow

### Adding New Service
1. Create service in appropriate folder (`services/` or `infrastructure/`)
2. Add module to parent POM
3. Create Dockerfile if needed
4. Add tests and documentation
5. Update deployment scripts

### Modifying Existing Service
1. Update code following existing patterns
2. Add comprehensive tests
3. Update documentation
4. Test integration with other services

## 🚨 Code Review Checklist

### Functionality
- [ ] Code works as expected
- [ ] Tests pass consistently
- [ ] No breaking changes

### Quality
- [ ] Follows existing code style
- [ ] Proper error handling
- [ ] Logging implemented
- [ ] Security considerations addressed

### Testing
- [ ] Unit tests added/updated
- [ ] Integration tests added/updated
- [ ] Test coverage maintained

### Documentation
- [ ] Code documented with JavaDoc
- [ ] README updated if needed
- [ ] API documentation updated

## 🐛 Bug Reports

### Reporting Bugs
1. **Check existing issues** for similar problems
2. **Create detailed bug report** with:
   - Steps to reproduce
   - Expected vs actual behavior
   - Environment details
   - Relevant logs

### Bug Fix Process
1. **Reproduce** the issue locally
2. **Create failing test** that demonstrates the bug
3. **Fix the issue** with minimal changes
4. **Verify fix** with comprehensive tests

## 🚀 Feature Requests

### Proposing Features
1. **Open discussion** in GitHub issues
2. **Provide detailed specification** including:
   - Use case and requirements
   - API design if applicable
   - Integration points with existing services

### Feature Implementation
1. **Break down** into manageable tasks
2. **Implement incrementally** with tests
3. **Update documentation** as features are added

## 🔒 Security Guidelines

### Code Security
- **Input validation** for all endpoints
- **SQL injection prevention** with JPA
- **XSS protection** for web content
- **Secure defaults** for all configurations

### Infrastructure Security
- **Container security** with non-root users
- **Network policies** for service communication
- **Secret management** for sensitive data
- **TLS encryption** for external communications

## 📞 Getting Help

### Resources
- **Documentation**: Check `docs/` folder
- **Issues**: Search existing GitHub issues
- **Discussions**: Use GitHub Discussions for questions

### Contact
- **Maintainers**: @project-maintainers
- **Architecture**: @architecture-team
- **Security**: @security-team

---

Thank you for contributing to our B2B e-commerce platform! 🎉
