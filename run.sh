#!/bin/bash

set -e

echo "deploying mailpit server"
kubectl apply -f k8s/mailpit.yml

echo "configuring notification service"
kubectl apply -f k8s/notification.yml

echo "Waiting for Notification Service..."
kubectl rollout status deployment/notification-service -n ecom-basic

echo "mailpit port forwarding to 8025"
kubectl port-forward -n ecom-basic svc/mailpit 8025:8025