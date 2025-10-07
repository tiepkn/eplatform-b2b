# 🔧 Troubleshooting Guide

Common issues and solutions for the B2B E-commerce platform.

## 🚨 Common Issues

### 1. Build Failures

#### Maven Dependency Issues
```bash
# Clear Maven cache
rm -rf ~/.m2/repository/com/eplatform/b2b

# Force update dependencies
mvn clean install -U -DskipTests
```

#### Compilation Errors
```bash
# Check Java version
java -version

# Verify Maven version
mvn -version

# Clean and rebuild
mvn clean compile
```

### 2. Service Startup Issues

#### Port Already in Use
```bash
# Check which process uses the port
lsof -i :8080

# Kill the process
kill -9 <PID>

# Alternative: Use different port
mvn spring-boot:run -pl inventory-service -Dserver.port=8111
```

#### Database Connection Issues
```bash
# Check if PostgreSQL is running
pg_isready -h localhost -p 5432

# Start PostgreSQL if needed
brew services start postgresql

# Check database exists
psql -h localhost -U postgres -l
```

#### Kafka Connection Issues
```bash
# Check if Kafka is running
nc -vz localhost 9092

# List Kafka topics
kafka-topics --bootstrap-server localhost:9092 --list

# Start Kafka if needed
brew services start kafka
```

### 3. Service Discovery Issues

#### Eureka Registration Problems
```bash
# Check Eureka dashboard
open http://localhost:8761

# Verify service configuration
curl http://localhost:8761/eureka/apps

# Check service logs for registration errors
tail -f logs/inventory-service.log | grep -i eureka
```

#### Service Communication Issues
```bash
# Test service health
curl http://localhost:8110/actuator/health

# Check service logs for connection errors
tail -f logs/inventory-service.log | grep -i connection
```

### 4. Testing Issues

#### Test Failures
```bash
# Run tests with debug output
mvn test -X -pl inventory-service

# Run specific failing test
mvn test -pl inventory-service -Dtest=InventoryReservationServiceTest#testName

# Check test logs
tail -f logs/test.log
```

#### Database Test Issues
```bash
# Use in-memory database for tests
mvn test -pl inventory-service -Dspring.profiles.active=test

# Check test database connectivity
psql -h localhost -U postgres -d test_db
```

### 5. Docker Issues

#### Build Failures
```bash
# Clean Docker cache
docker system prune -f

# Rebuild specific service
docker build --no-cache -t eplatform/inventory-service ./services/inventory-service

# Check Docker logs
docker logs <container-name>
```

#### Container Networking Issues
```bash
# Check container networks
docker network ls

# Inspect container network
docker network inspect eplatform-network

# Restart containers
docker-compose -f docker/docker-compose.yml down && docker-compose -f docker/docker-compose.yml up -d
```

## 🔍 Debugging Tools

### Log Analysis
```bash
# Monitor all service logs
find logs/ -name "*.log" -exec tail -f {} +

# Search for errors across logs
grep -r "ERROR" logs/

# Monitor specific service
tail -f logs/inventory-service.log | grep -E "(ERROR|WARN|Exception)"
```

### Database Debugging
```bash
# Connect to database
psql -h localhost -U postgres -d demo_db

# Check table schemas
\d inventory.product_stock
\d orders.orders

# Monitor active connections
SELECT * FROM pg_stat_activity;
```

### Kafka Debugging
```bash
# List all topics
kafka-topics --bootstrap-server localhost:9092 --list

# Describe specific topic
kafka-topics --bootstrap-server localhost:9092 --describe --topic order.placed

# Consume messages for debugging
kafka-console-consumer --bootstrap-server localhost:9092 --topic order.placed --from-beginning
```

## 🚀 Performance Issues

### Memory Issues
```bash
# Check JVM memory usage
jps -l
jstat -gc <PID>

# Adjust JVM memory
mvn spring-boot:run -pl inventory-service -Dspring-boot.run.jvmArguments="-Xmx1g -Xms512m"
```

### Slow Startup
```bash
# Enable startup profiling
mvn spring-boot:run -pl inventory-service -Ddebug

# Check for blocking operations in logs
tail -f logs/inventory-service.log | grep -E "(blocked|waiting|lock)"
```

## 🛠️ Development Tools

### IDE Configuration

#### IntelliJ IDEA
```xml
<!-- Add to workspace.xml -->
<component name="RunManager">
  <configuration name="All Services" type="Compound">
    <toRun name="discovery-server" />
    <toRun name="config-server" />
    <toRun name="inventory-service" />
    <!-- Add other services -->
  </configuration>
</component>
```

#### VS Code
```json
{
  "java.test.config": {
    "workingDirectory": "${workspaceFolder}",
    "vmargs": ["-Xmx2g"]
  }
}
```

### Hot Reloading Setup
```bash
# Enable DevTools in POM
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-devtools</artifactId>
  <scope>runtime</scope>
  <optional>true</optional>
</dependency>

# Restart services automatically on changes
mvn spring-boot:run -pl inventory-service -Dspring-boot.run.jvmArguments="-Dspring.devtools.restart.enabled=true"
```

## 📊 Monitoring & Health Checks

### Health Check Endpoints
```bash
# Check all service health
./scripts/health-check.sh

# Individual service health
curl http://localhost:8110/actuator/health
curl http://localhost:8120/actuator/health
```

### Application Metrics
```bash
# Prometheus metrics
curl http://localhost:8110/actuator/prometheus

# Application info
curl http://localhost:8110/actuator/info

# Environment details
curl http://localhost:8110/actuator/env
```

## 🔒 Security Issues

### Authentication Problems
```bash
# Check auth service logs
tail -f logs/auth-service.log

# Test authentication endpoint
curl -X POST http://localhost:8100/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"test"}'
```

### Authorization Issues
```bash
# Check for 403 errors in logs
grep -r "403" logs/

# Verify JWT token format
curl -H "Authorization: Bearer <token>" http://localhost:8110/api/v1/inventory
```

## 🌐 Network Issues

### Service-to-Service Communication
```bash
# Test direct service calls
curl http://localhost:8110/api/v1/inventory/available/PRODUCT-001

# Check service discovery
curl http://localhost:8761/eureka/apps/INVENTORY-SERVICE

# Test load balancer routing
curl http://localhost:8080/api/v1/inventory/available/PRODUCT-001
```

### External Service Integration
```bash
# Test payment service
curl http://localhost:8130/actuator/health

# Check gRPC communication
# Look for gRPC connection logs in payment-service logs
```

## 📝 Logging Issues

### Log Configuration Problems
```bash
# Check logback configuration
cat services/inventory-service/src/main/resources/logback-spring.xml

# Test log levels
curl -X POST http://localhost:8110/actuator/loggers/com.eplatform.b2b.inventory \
  -H "Content-Type: application/json" \
  -d '{"configuredLevel":"DEBUG"}'
```

### Missing Logs
```bash
# Check if log files are created
ls -la logs/

# Verify file permissions
chmod 644 logs/*.log

# Check disk space
df -h
```

## 🗄️ Database Issues

### Schema Problems
```bash
# Check current schema
psql -h localhost -U postgres -d demo_db -c "\dn"

# Verify table structure
psql -h localhost -U postgres -d demo_db -c "\d inventory.product_stock"

# Check for constraint violations
psql -h localhost -U postgres -d demo_db -c "SELECT * FROM inventory.product_stock WHERE available < 0;"
```

### Connection Pool Issues
```bash
# Check HikariCP stats
curl http://localhost:8110/actuator/metrics/hikaricp.connections

# Monitor connection pool
curl http://localhost:8110/actuator/metrics/hikaricp.connections.active
```

## 🚨 Emergency Procedures

### Service Outage
```bash
# Check all service health
./scripts/health-check.sh

# Restart failed services
./scripts/dev-stop.sh
./scripts/dev-start.sh

# Check system resources
top -p $(cat pids/*.pid 2>/dev/null | head -5)
```

### Data Corruption
```bash
# Create database backup before any fixes
pg_dump -h localhost -U postgres demo_db > backup_$(date +%Y%m%d_%H%M%S).sql

# Restore from backup if needed
psql -h localhost -U postgres demo_db < backup_YYYYMMDD_HHMMSS.sql
```

## 📞 Getting Help

### Quick Diagnostics
```bash
# Run comprehensive health check
./scripts/health-check.sh

# Check all logs for errors
find logs/ -name "*.log" -exec grep -l "ERROR\|Exception" {} \;

# Verify system resources
echo "=== CPU ===" && top -bn1 | head -5
echo "=== Memory ===" && free -h
echo "=== Disk ===" && df -h
```

### Support Channels
- **GitHub Issues**: Report bugs and feature requests
- **Documentation**: Check docs/ folder for detailed guides
- **Team Chat**: Contact development team for urgent issues

---

**Remember:** Always check logs first when troubleshooting! 🔍
