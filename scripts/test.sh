#!/bin/bash

# Script to run tests for the entire project or specific services
# Usage: ./scripts/test.sh [service-name] [--integration]

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[1;34m'
NC='\033[0m' # No Color

SERVICE=""
RUN_INTEGRATION=false

# Parse arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        --integration)
            RUN_INTEGRATION=true
            shift
            ;;
        -*)
            echo -e "${RED}❌ Unknown option: $1${NC}"
            echo -e "${YELLOW}Usage: $0 [service-name] [--integration]${NC}"
            exit 1
            ;;
        *)
            SERVICE="$1"
            shift
            ;;
    esac
done

echo -e "${BLUE}🧪 Running Tests for B2B E-commerce Platform${NC}"

# Function to run tests for a service
run_service_tests() {
    local service=$1
    local include_integration=$2

    echo -e "\n${YELLOW}🔄 Testing $service...${NC}"

    if [ "$include_integration" = true ]; then
        echo -e "${BLUE}📋 Running unit and integration tests...${NC}"
        mvn test -pl $service
    else
        echo -e "${BLUE}📋 Running unit tests only...${NC}"
        mvn test -pl $service -Dtest="**/*Test" -DfailIfNoTests=false
    fi

    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✅ $service tests passed${NC}"
    else
        echo -e "${RED}❌ $service tests failed${NC}"
        return 1
    fi
}

# Run tests based on arguments
if [ -n "$SERVICE" ]; then
    # Test specific service
    if [ ! -d "$SERVICE" ]; then
        echo -e "${RED}❌ Service not found: $SERVICE${NC}"
        echo -e "${YELLOW}Available services:${NC}"
        find . -name "pom.xml" -path "*/src/*" -exec dirname {} \; | grep -E "(inventory|order|catalog|payment|auth|common|api-gateway|config-server|discovery-server)-service" | sed 's|./||' | sed 's|/src||'
        exit 1
    fi

    run_service_tests $SERVICE $RUN_INTEGRATION
else
    # Test all services
    echo -e "${YELLOW}🔄 Testing all services...${NC}"

    services=(
        "common"
        "infrastructure/discovery-server"
        "infrastructure/config-server"
        "infrastructure/api-gateway"
        "services/auth-service"
        "services/catalog-service"
        "services/inventory-service"
        "services/order-service"
        "services/payment-service"
    )

    failed_services=()

    for service in "${services[@]}"; do
        if ! run_service_tests $service $RUN_INTEGRATION; then
            failed_services+=($service)
        fi
    done

    # Summary
    echo -e "\n${BLUE}📊 Test Summary${NC}"
    if [ ${#failed_services[@]} -eq 0 ]; then
        echo -e "${GREEN}🎉 All tests passed!${NC}"
        exit 0
    else
        echo -e "${RED}❌ Some tests failed:${NC}"
        for failed in "${failed_services[@]}"; do
            echo -e "   • $failed"
        done
        exit 1
    fi
fi
