#!/bin/bash

# Script to build Docker images for all services
# Usage: ./scripts/docker-build.sh [service-name]

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[1;34m'
NC='\033[0m' # No Color

SERVICE=""
IMAGE_TAG=${IMAGE_TAG:-latest}

# Parse arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        -*)
            echo -e "${RED}❌ Unknown option: $1${NC}"
            echo -e "${YELLOW}Usage: $0 [service-name]${NC}"
            exit 1
            ;;
        *)
            SERVICE="$1"
            shift
            ;;
    esac
done

echo -e "${BLUE}🐳 Building Docker Images for B2B E-commerce Platform${NC}"

# Function to build a specific service
build_service_image() {
    local service=$1

    echo -e "\n${YELLOW}🔨 Building $service...${NC}"

    # Check if service exists
    if [ ! -d "$service" ]; then
        echo -e "${RED}❌ Service not found: $service${NC}"
        return 1
    fi

    # Build the image
    echo -e "${BLUE}📦 Building Docker image: eplatform/$service:$IMAGE_TAG${NC}"
    docker build -f docker/Dockerfile \
        --target builder \
        --build-arg SERVICE_NAME=$service \
        -t eplatform/$service:$IMAGE_TAG \
        .

    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✅ Successfully built eplatform/$service:$IMAGE_TAG${NC}"
        return 0
    else
        echo -e "${RED}❌ Failed to build $service${NC}"
        return 1
    fi
}

# Build specific service or all services
if [ -n "$SERVICE" ]; then
    build_service_image $SERVICE
else
    echo -e "${YELLOW}🔨 Building all services...${NC}"

    services=(
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
        if ! build_service_image $service; then
            failed_services+=($service)
        fi
    done

    # Summary
    echo -e "\n${BLUE}📊 Build Summary${NC}"
    if [ ${#failed_services[@]} -eq 0 ]; then
        echo -e "${GREEN}🎉 All Docker images built successfully!${NC}"

        echo -e "\n${YELLOW}🚀 Available images:${NC}"
        docker images eplatform/* --format "table {{.Repository}}\t{{.Tag}}\t{{.Size}}\t{{.CreatedAt}}"

        echo -e "\n${YELLOW}💡 To start services:${NC}"
        echo -e "   docker-compose -f docker/docker-compose.yml up -d"

        echo -e "\n${YELLOW}🔍 To check logs:${NC}"
        echo -e "   docker-compose -f docker/docker-compose.yml logs -f"

        exit 0
    else
        echo -e "${RED}❌ Some services failed to build:${NC}"
        for failed in "${failed_services[@]}"; do
            echo -e "   • $failed"
        done
        exit 1
    fi
fi
