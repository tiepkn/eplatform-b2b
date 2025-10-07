# Multi-stage Dockerfile for Spring Boot microservices
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copy Maven files for better layer caching
COPY pom.xml ./
COPY common/pom.xml ./common/
COPY discovery-server/pom.xml ./discovery-server/
COPY config-server/pom.xml ./config-server/
COPY api-gateway/pom.xml ./api-gateway/
COPY auth-service/pom.xml ./auth-service/
COPY catalog-service/pom.xml ./catalog-service/
COPY inventory-service/pom.xml ./inventory-service/
COPY order-service/pom.xml ./order-service/
COPY payment-service/pom.xml ./payment-service/

# Download dependencies (cached if pom.xml doesn't change)
RUN apk add --no-cache maven && \
    mvn dependency:go-offline -B --no-transfer-progress

# Copy source code
COPY . .

# Build application
ARG SERVICE_NAME=inventory-service
RUN mvn clean package -pl ${SERVICE_NAME} -am -DskipTests -B --no-transfer-progress

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

LABEL maintainer="B2B E-commerce Team"

# Create non-root user for security
RUN addgroup -g 1001 -S appuser && \
    adduser -S appuser -u 1001 -G appuser

# Install curl for health checks
RUN apk add --no-cache curl

WORKDIR /app

# Copy JAR from builder stage
ARG SERVICE_NAME=inventory-service
COPY --from=builder /app/${SERVICE_NAME}/target/*.jar app.jar

# Change ownership to non-root user
RUN chown -R appuser:appuser /app

USER appuser

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Expose port
EXPOSE 8080

# JVM optimization for containers
ENV JAVA_OPTS="-XX:MaxRAMFraction=1 -XX:+UseContainerSupport -XX:+UseG1GC -XX:+UseStringDeduplication"

# Run application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
