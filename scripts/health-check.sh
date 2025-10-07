#!/bin/bash

# Script to check health of all services
# Usage: ./scripts/health-check.sh

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[1;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}🏥 Health Check for B2B E-commerce Platform${NC}"

# Service health check configuration
declare -A services=(
    ["discovery-server"]="8761"
    ["config-server"]="8888"
    ["api-gateway"]="8080"
    ["inventory-service"]="8110"
    ["catalog-service"]="8090"
    ["order-service"]="8120"
    ["payment-service"]="8130"
    ["auth-service"]="8100"
)

# Function to check service health
check_service_health() {
    local service=$1
    local port=$2
    local health_url="http://localhost:$port/actuator/health"

    if curl -f -s $health_url > /dev/null 2>&1; then
        echo -e "${GREEN}✅ $service (Port: $port)${NC}"
        return 0
    else
        echo -e "${RED}❌ $service (Port: $port) - Not healthy${NC}"
        return 1
    fi
}

# Function to check Eureka registration
check_eureka_registration() {
    local eureka_url="http://localhost:8761/eureka/apps"

    echo -e "\n${YELLOW}🔍 Checking Eureka registrations...${NC}"

    if curl -f -s $eureka_url > /dev/null 2>&1; then
        echo -e "${GREEN}✅ Eureka is accessible${NC}"

        # Get registered services
        local registered_services=$(curl -s $eureka_url | grep -o '"name":"[^"]*"' | sed 's/"name":"//g' | sed 's/"//g' | sort | uniq)

        if [ -n "$registered_services" ]; then
            echo -e "${BLUE}📋 Registered services:${NC}"
            echo "$registered_services" | while read -r service; do
                if [ "$service" != "DISCOVERY-SERVER" ]; then
                    echo -e "   • $service"
                fi
            done
        else
            echo -e "${YELLOW}⚠️  No services registered in Eureka${NC}"
        fi
    else
        echo -e "${RED}❌ Eureka not accessible${NC}"
    fi
}

# Check database connectivity
check_database() {
    echo -e "\n${YELLOW}🗄️  Checking database connectivity...${NC}"

    if command -v pg_isready &> /dev/null; then
        if pg_isready -h localhost -p 5432 &> /dev/null 2>&1; then
            echo -e "${GREEN}✅ PostgreSQL is running${NC}"
        else
            echo -e "${RED}❌ PostgreSQL not running${NC}"
        fi
    else
        echo -e "${YELLOW}⚠️  pg_isready command not found${NC}"
    fi
}

# Check Kafka connectivity
check_kafka() {
    echo -e "\n${YELLOW}📨 Checking Kafka connectivity...${NC}"

    if command -v nc &> /dev/null; then
        if nc -vz localhost 9092 &> /dev/null 2>&1; then
            echo -e "${GREEN}✅ Kafka is running${NC}"
        else
            echo -e "${RED}❌ Kafka not running${NC}"
        fi
    else
        echo -e "${YELLOW}⚠️  nc command not found${NC}"
    fi
}

# Main health check
main() {
    local failed_services=()
    local total_services=${#services[@]}
    local healthy_services=0

    echo -e "${BLUE}🔍 Checking individual service health...${NC}"

    for service in "${!services[@]}"; do
        local port=${services[$service]}
        if check_service_health "$service" "$port"; then
            ((healthy_services++))
        else
            failed_services+=("$service")
        fi
    done

    # Check infrastructure
    check_database
    check_kafka
    check_eureka_registration

    # Summary
    echo -e "\n${BLUE}📊 Health Summary${NC}"
    echo -e "Total services: $total_services"
    echo -e "Healthy services: $healthy_services"

    if [ ${#failed_services[@]} -eq 0 ]; then
        echo -e "${GREEN}🎉 All services are healthy!${NC}"
        return 0
    else
        echo -e "${RED}❌ Some services are not healthy:${NC}"
        for failed in "${failed_services[@]}"; do
            echo -e "   • $failed"
        done
        return 1
    fi
}

# Run main function
main
