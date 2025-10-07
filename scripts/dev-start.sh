#!/bin/bash

# Script to start all services for development
# Usage: ./scripts/dev-start.sh [service-name]

set -e

echo "🚀 Starting B2B E-commerce Platform Services"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to check if port is in use
check_port() {
    local port=$1
    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null ; then
        echo -e "${RED}❌ Port $port is already in use${NC}"
        return 1
    fi
    return 0
}

# Function to start a service
start_service() {
    local service=$1
    local port=$2
    local schema=${3:-""}

    echo -e "${YELLOW}🔄 Starting $service...${NC}"

    if ! check_port $port; then
        echo -e "${RED}❌ Cannot start $service - port $port is in use${NC}"
        return 1
    fi

    # Create necessary directories
    local service_name=$(basename $service)
    mkdir -p "logs/services" "pids/services"

    # Start service in background
    if [ "$service" = "services/inventory-service" ] && [ -n "$schema" ]; then
        SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/demo_db?currentSchema=$schema" \
        mvn spring-boot:run -pl $service > "logs/services/$service_name.log" 2>&1 &
    else
        mvn spring-boot:run -pl $service > "logs/services/$service_name.log" 2>&1 &
    fi

    local pid=$!
    echo $pid > "pids/services/$service_name.pid"
    echo -e "${GREEN}✅ Started $service (PID: $pid) on port $port${NC}"
    sleep 3
}

# Create necessary directories
mkdir -p logs pids

# Check prerequisites
echo -e "${YELLOW}🔍 Checking prerequisites...${NC}"

# Check Java
if ! command -v java &> /dev/null; then
    echo -e "${RED}❌ Java is not installed${NC}"
    exit 1
fi

# Check Maven
if ! command -v mvn &> /dev/null; then
    echo -e "${RED}❌ Maven is not installed${NC}"
    exit 1
fi

# Check PostgreSQL (optional for some services)
if ! pg_isready -h localhost -p 5432 &> /dev/null; then
    echo -e "${YELLOW}⚠️  PostgreSQL not running on localhost:5432${NC}"
    echo -e "${YELLOW}   Some services may fail to start${NC}"
fi

echo -e "${GREEN}✅ Prerequisites check completed${NC}"

# Start infrastructure services first
echo -e "\n${YELLOW}🏗️  Starting Infrastructure Services${NC}"

start_service "discovery-server" "8761" || exit 1
sleep 5

start_service "config-server" "8888" || exit 1
sleep 3

# Start business services
echo -e "\n${YELLOW}💼 Starting Business Services${NC}"

# Start services based on argument or all services
if [ -n "$1" ]; then
    case $1 in
        "inventory")
            start_service "services/inventory-service" "8110" "inventory"
            ;;
        "order")
            start_service "services/order-service" "8120" "orders"
            ;;
        "catalog")
            start_service "services/catalog-service" "8090"
            ;;
        "payment")
            start_service "services/payment-service" "8130"
            ;;
        "auth")
            start_service "services/auth-service" "8100"
            ;;
        "gateway")
            start_service "infrastructure/api-gateway" "8080"
            ;;
        *)
            echo -e "${RED}❌ Unknown service: $1${NC}"
            echo -e "${YELLOW}Available services: inventory, order, catalog, payment, auth, gateway${NC}"
            exit 1
            ;;
    esac
else
    # Start all services
    start_service "services/inventory-service" "8110" "inventory" || exit 1
    sleep 2

    start_service "services/catalog-service" "8090" || exit 1
    sleep 2

    start_service "services/order-service" "8120" "orders" || exit 1
    sleep 2

    start_service "services/payment-service" "8130" || exit 1
    sleep 2

    start_service "services/auth-service" "8100" || exit 1
    sleep 2

    start_service "infrastructure/api-gateway" "8080" || exit 1
fi

echo -e "\n${GREEN}🎉 All services started successfully!${NC}"
echo -e "${YELLOW}📊 Service URLs:${NC}"
echo -e "   • Eureka Dashboard: http://localhost:8761"
echo -e "   • API Gateway: http://localhost:8080/actuator/health"
echo -e "   • Inventory Service: http://localhost:8110/actuator/health"
echo -e "   • Order Service: http://localhost:8120/actuator/health"
echo -e "   • Catalog Service: http://localhost:8090/actuator/health"
echo -e "   • Payment Service: http://localhost:8130/actuator/health"
echo -e "   • Auth Service: http://localhost:8100/actuator/health"

echo -e "\n${YELLOW}📋 Check logs:${NC}"
echo -e "   tail -f logs/discovery-server.log"
echo -e "   tail -f logs/inventory-service.log"

echo -e "\n${YELLOW}🛑 To stop all services:${NC}"
echo -e "   ./scripts/dev-stop.sh"

echo -e "\n${GREEN}🚀 Platform is ready for development!${NC}"
