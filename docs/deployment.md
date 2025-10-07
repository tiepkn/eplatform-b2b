# 🚀 Deployment Guide

This guide covers deployment strategies for the B2B E-commerce platform.

## 📋 Deployment Environments

### Development Environment
- **Purpose:** Development and testing
- **Instances:** Single instance per service
- **Database:** Local PostgreSQL
- **Monitoring:** Basic health checks

### Staging Environment
- **Purpose:** Pre-production testing
- **Instances:** 1-2 instances per service
- **Database:** Separate staging database
- **Monitoring:** Health checks + basic metrics

### Production Environment
- **Purpose:** Live production traffic
- **Instances:** Multiple instances with load balancing
- **Database:** Production PostgreSQL cluster
- **Monitoring:** Comprehensive monitoring and alerting

## 🐳 Docker Deployment

### Build Images

```bash
# Build all services
mvn clean package -DskipTests

# Build Docker images
docker build -f docker/Dockerfile -t eplatform/inventory-service:latest ./inventory-service
docker build -f docker/Dockerfile -t eplatform/order-service:latest ./order-service
# ... build other services
```

### Using Docker Compose

```bash
# Development environment
docker-compose -f docker/docker-compose.dev.yml up -d

# Production environment
docker-compose -f docker/docker-compose.prod.yml up -d
```

### Docker Compose Files

#### `docker-compose.dev.yml`
```yaml
version: '3.8'
services:
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: demo_db
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: dev_password

  kafka:
    image: confluentinc/cp-kafka:7.4.0
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:9092
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181

  discovery-server:
    image: eplatform/discovery-server:latest
    ports:
      - "8761:8761"

  # ... other services
```

## ☁️ Kubernetes Deployment

### Prerequisites
- Kubernetes cluster (GKE, EKS, AKS, or local)
- kubectl configured
- Helm 3.x

### Deploy with Helm

```bash
# Install dependencies
helm repo add bitnami https://charts.bitnami.com/bitnami
helm repo update

# Deploy PostgreSQL
helm install postgres bitnami/postgresql \
  --set auth.postgresPassword=prod_password \
  --set primary.persistence.size=50Gi

# Deploy Kafka
helm install kafka bitnami/kafka \
  --set persistence.size=100Gi

# Deploy application
helm install eplatform ./helm/eplatform \
  --set image.tag=latest \
  --set replicaCount=3
```

### Kubernetes Manifests Structure

```
📁 k8s/
├── 📁 base/ (base configurations)
│   ├── 📄 kustomization.yaml
│   ├── 📄 namespace.yaml
│   └── 📄 configmap.yaml
├── 📁 overlays/
│   ├── 📁 development/
│   ├── 📁 staging/
│   └── 📁 production/
└── 📁 services/
    ├── 📁 inventory/
    ├── 📁 order/
    └── 📁 payment/
```

## 🔧 Configuration Management

### Externalized Configuration

Services pull configuration from Config Server:

```bash
# Config Server URL
http://config-server:8888

# Configuration files
inventory-service.yml
inventory-service-dev.yml
inventory-service-prod.yml
```

### Secrets Management

Use Kubernetes secrets for sensitive data:

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: app-secrets
type: Opaque
data:
  db-password: <base64-encoded-password>
  kafka-password: <base64-encoded-password>
```

## 📊 Monitoring & Logging

### Health Checks

Configure health endpoints in each service:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: when-authorized
```

### Metrics Collection

```bash
# Prometheus metrics endpoint
http://service:port/actuator/prometheus

# Custom metrics for business logic
counter.order.placed.total
gauge.inventory.reserved.items
```

### Centralized Logging

```yaml
# Logstash configuration
input {
  kafka {
    topics => ["application-logs"]
    bootstrap_servers => "kafka:9092"
  }
}

output {
  elasticsearch {
    hosts => ["elasticsearch:9200"]
  }
}
```

## 🚀 CI/CD Pipeline

### GitHub Actions Example

```yaml
name: Deploy to Production
on:
  push:
    branches: [main]

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3

      - name: Build and test
        run: mvn clean verify

      - name: Build Docker images
        run: docker build -t eplatform/${{ github.event.repository.name }}:latest

      - name: Deploy to Kubernetes
        run: |
          kubectl apply -f k8s/overlays/production/
          kubectl rollout status deployment/inventory-service
```

## 🔒 Security Considerations

### Network Security
- Use TLS/SSL for all communications
- Implement API rate limiting
- Configure security groups/firewall rules

### Authentication & Authorization
- JWT tokens for API authentication
- Role-based access control (RBAC)
- API key management for service-to-service auth

### Data Protection
- Encrypt sensitive data at rest
- Use TLS for data in transit
- Implement data backup strategies

## 📈 Scaling Strategies

### Horizontal Scaling
```bash
# Scale deployments
kubectl scale deployment inventory-service --replicas=5
kubectl scale deployment order-service --replicas=3
```

### Auto-scaling
```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: inventory-service-hpa
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: inventory-service
  minReplicas: 2
  maxReplicas: 10
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
```

## 🛠️ Database Migration

### Flyway Migration Strategy

```sql
-- V1__Initial_schema.sql
CREATE TABLE product_stock (
    id BIGSERIAL PRIMARY KEY,
    sku VARCHAR(255) NOT NULL UNIQUE,
    available INTEGER NOT NULL,
    reserved INTEGER NOT NULL DEFAULT 0,
    version BIGINT
);

-- V2__Add_indexes.sql
CREATE INDEX idx_stock_sku ON product_stock(sku);
```

### Running Migrations

```bash
# Via application startup
mvn spring-boot:run -pl inventory-service

# Manual migration
mvn flyway:migrate -pl inventory-service
```

## 🚨 Troubleshooting

### Common Deployment Issues

#### Service Discovery Problems
```bash
# Check Eureka server logs
kubectl logs deployment/discovery-server

# Verify service registration
curl http://discovery-server:8761/eureka/apps
```

#### Database Connection Issues
```bash
# Check database connectivity
kubectl exec deployment/postgres -- pg_isready -h localhost

# Check application logs
kubectl logs deployment/inventory-service
```

#### Kafka Issues
```bash
# Check Kafka broker status
kubectl exec deployment/kafka -- kafka-broker-api-versions

# List topics
kubectl exec deployment/kafka -- kafka-topics --list --bootstrap-server localhost:9092
```

## 📋 Deployment Checklist

### Pre-deployment
- [ ] All tests pass
- [ ] Database migrations ready
- [ ] Configuration reviewed
- [ ] Security scan completed

### During Deployment
- [ ] Backup current state
- [ ] Deploy in stages (canary deployment)
- [ ] Monitor health endpoints
- [ ] Verify service integration

### Post-deployment
- [ ] Verify all services healthy
- [ ] Test critical user journeys
- [ ] Monitor error rates and performance
- [ ] Clean up old deployments

## 🔄 Rollback Strategy

```bash
# Quick rollback
kubectl rollout undo deployment/inventory-service

# Rollback to specific revision
kubectl rollout undo deployment/inventory-service --to-revision=2

# Check rollout history
kubectl rollout history deployment/inventory-service
```

## 📞 Support

For deployment issues:
1. Check service logs: `kubectl logs deployment/<service-name>`
2. Verify configuration: `kubectl describe pod/<pod-name>`
3. Check events: `kubectl get events --sort-by=.metadata.creationTimestamp`

---

**Remember:** Always test deployments in staging before production! 🚀
