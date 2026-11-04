#!/usr/bin/env bash

set -e

GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m'

IMAGE='hemantdevgan/api-gateway:v2.0'
DOCKERFILE='microservice-backend/api-gateway/Dockerfile'
MANIFEST='k8s/api-gateway.yml'

printf "${BLUE}Building image: %s${NC}\n" "$IMAGE"
docker build -f "$DOCKERFILE" -t "$IMAGE" .

printf "${BLUE}Pushing image: %s${NC}\n" "$IMAGE"
docker push "$IMAGE"

printf "${GREEN}Applying Kubernetes manifest: %s${NC}\n" "$MANIFEST"
kubectl apply -f "$MANIFEST"

printf "${GREEN}API Gateway deployment complete.${NC}\n"
