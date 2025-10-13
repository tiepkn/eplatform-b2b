# 🧪 Testing Guide

This guide covers testing strategies and practices for the B2B E-commerce platform.

## 📋 Testing Strategy

### Test Categories

#### 1. **Unit Tests** (`src/test/java/.../unit/`)
- **Purpose**: Test individual classes and methods in isolation
- **Tools**: JUnit 5, Mockito, AssertJ
- **Coverage**: Minimum 80% for business logic

#### 2. **Integration Tests** (`src/test/java/.../integration/`)
- **Purpose**: Test component interactions (database, Kafka, HTTP)
- **Tools**: Spring Boot Test, TestContainers, Embedded Kafka
- **Scope**: Service boundaries and external dependencies

#### 3. **Contract Tests** (`src/test/java/.../contract/`)
- **Purpose**: Verify API contracts between services
- **Tools**: Spring Cloud Contract, Pact
- **Scope**: Service-to-service communication

## 🚀 Running Tests

### All Tests
```bash
# Run complete test suite
./scripts/test.sh

# With coverage report
mvn test -Pcoverage
```

### Service-Specific Tests
```bash
# Test specific service
./scripts/test.sh inventory-service
./scripts/test.sh product-service
./scripts/test.sh supplier-service

# With integration tests
./scripts/test.sh inventory-service --integration
./scripts/test.sh product-service --integration
./scripts/test.sh supplier-service --integration
```

### Manual Test Commands
```bash
# Unit tests only
mvn test -pl services/inventory-service -Dtest="**/*Test"
mvn test -pl services/product-service -Dtest="**/*Test"
mvn test -pl services/supplier-service -Dtest="**/*Test"

# Integration tests only
mvn test -pl services/inventory-service -Dtest="**/*IntegrationTest"
mvn test -pl services/product-service -Dtest="**/*IntegrationTest"
mvn test -pl services/supplier-service -Dtest="**/*IntegrationTest"

# Verify tests (compile + run)
mvn verify -pl services/inventory-service
```

## 📁 Test Structure

```
📁 services/inventory-service/src/test/java/com/eplatform/b2b/inventory/
├── 📁 unit/ (Individual class testing)
│   ├── InventoryReservationServiceTest.java
│   ├── ProductStockTest.java
│   └── ReservationRepositoryTest.java
├── 📁 integration/ (Component integration testing)
│   ├── InventoryServiceIntegrationTest.java
│   └── KafkaIntegrationTest.java
└── 📁 resources/ (Test data and configurations)
    ├── application-test.yml
    └── test-data.sql
```

## 🛠️ Testing Tools & Libraries

### Core Testing
- **JUnit 5**: Testing framework
- **AssertJ**: Fluent assertions
- **Mockito**: Mocking framework

### Spring Boot Testing
- **@SpringBootTest**: Full application context
- **@WebMvcTest**: Web layer testing
- **@DataJpaTest**: JPA repository testing

### Integration Testing
- **TestContainers**: Database and message broker testing
- **Embedded Kafka**: Kafka testing
- **WireMock**: HTTP service mocking

## 🎯 Testing Best Practices

### Unit Tests
```java
@Test
void shouldReserveStockSuccessfully() {
    // Given - Setup test data
    ProductStock stock = new ProductStock("TEST-SKU", 100);

    // When - Execute method under test
    boolean result = stock.reserve(10);

    // Then - Verify expected behavior
    assertThat(result).isTrue();
    assertThat(stock.getAvailable()).isEqualTo(90);
    assertThat(stock.getReserved()).isEqualTo(0);
}
```

### Integration Tests
```java
@SpringBootTest
@Testcontainers
class InventoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
}
```

### Test Organization
- **One assertion per test**: Clear and focused
- **Descriptive test names**: Explain what is being tested
- **Arrange-Act-Assert**: Clear test structure
- **Test data builders**: Reduce test setup boilerplate

## 🔍 Test Coverage

### Measuring Coverage
```bash
# Generate coverage report
mvn test -Pcoverage

# Coverage report location
open services/inventory-service/target/site/jacoco/index.html
```

### Coverage Goals
- **Overall**: > 80%
- **Business Logic**: > 90%
- **Error Handling**: > 85%
- **New Features**: > 95%

## 🐛 Debugging Tests

### Common Issues

#### Flaky Tests
- Use proper test isolation
- Avoid shared state between tests
- Use `@DirtiesContext` when needed

#### Slow Tests
- Use in-memory databases for unit tests
- Mock external dependencies
- Parallel test execution where possible

#### Database Tests
- Use `@DataJpaTest` for repository tests
- Clean database state between tests
- Use test-specific configurations

## 📊 Test Reporting

### CI/CD Integration
```bash
# Fail build on test failures
mvn test

# Generate test reports for CI
mvn surefire-report:report

# Integration with SonarQube
mvn sonar:sonar
```

### Test Metrics
- **Test Success Rate**: Track over time
- **Test Execution Time**: Identify slow tests
- **Coverage Trends**: Monitor coverage changes

## 🚨 Testing Guidelines

### DO ✅
- Write tests for all business logic
- Test error conditions and edge cases
- Use descriptive test names
- Keep tests focused and isolated
- Mock external dependencies

### DON'T ❌
- Write tests that depend on external systems
- Create tests with complex setup
- Test private methods directly
- Skip testing error scenarios
- Write tests that are slow to execute

## 📚 Resources

- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
- [Spring Boot Testing Guide](https://spring.io/guides/gs/testing-web/)
- [TestContainers Documentation](https://testcontainers.com/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)

---

**Happy Testing! 🧪**
