#!/bin/bash

# Script to stop all running services
# Usage: ./scripts/dev-stop.sh

echo "🛑 Stopping B2B E-commerce Platform Services"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to stop a service
stop_service() {
    local service=$1

    if [ -f "pids/$service.pid" ]; then
        local pid=$(cat pids/$service.pid)
        if ps -p $pid > /dev/null 2>&1; then
            echo -e "${YELLOW}🔄 Stopping $service (PID: $pid)...${NC}"
            kill $pid

            # Wait for process to terminate
            while ps -p $pid > /dev/null 2>&1; do
                sleep 1
            done

            echo -e "${GREEN}✅ Stopped $service${NC}"
        else
            echo -e "${YELLOW}⚠️  $service (PID: $pid) not running${NC}"
        fi
        rm -f pids/$service.pid
    else
        echo -e "${YELLOW}⚠️  No PID file found for $service${NC}"
    fi
}

# Kill any remaining Spring Boot processes
echo -e "${YELLOW}🔍 Looking for remaining Spring Boot processes...${NC}"
for pid in $(ps aux | grep "spring-boot:run" | grep -v grep | awk '{print $2}'); do
    if ps -p $pid > /dev/null 2>&1; then
        local process_name=$(ps -p $pid -o comm=)
        echo -e "${YELLOW}🔄 Stopping $process_name (PID: $pid)...${NC}"
        kill $pid

        # Wait for process to terminate
        while ps -p $pid > /dev/null 2>&1; do
            sleep 1
        done

        echo -e "${GREEN}✅ Stopped $process_name${NC}"
    fi
done

# Stop services in reverse order
services=(
    "api-gateway"
    "auth-service"
    "payment-service"
    "order-service"
    "catalog-service"
    "inventory-service"
    "config-server"
    "discovery-server"
)

echo -e "\n${YELLOW}🛑 Stopping services...${NC}"
for service in "${services[@]}"; do
    stop_service $service
done

# Clean up directories
if [ -d "pids" ]; then
    echo -e "${YELLOW}🧹 Cleaning up PID files...${NC}"
    rm -rf pids/*
fi

echo -e "\n${GREEN}🎉 All services stopped successfully!${NC}"
echo -e "${YELLOW}💡 To start services again:${NC}"
echo -e "   ./scripts/dev-start.sh"
