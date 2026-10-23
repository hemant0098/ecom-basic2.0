#!/bin/bash

set -e

echo "building images...."
docker build -f microservice-backend/category-service/Dockerfile -t category-service:v1.0 . 
docker build -f microservice-backend/notification-service/Dockerfile -t notification-service:v1.0 . 
docker build -f microservice-backend/order-service/Dockerfile -t order-service:v1.0 . 
docker build -f microservice-backend/product-service/Dockerfile -t product-service:v1.0 . 
docker build -f microservice-backend/user-service/Dockerfile -t user-service:v1.0 .

echo "tagging images"
docker tag category-service:v1.0 hemantdevgan/category-service:v1.0
docker tag notification-service:v1.0 hemantdevgan/notification-service:v1.0
docker tag order-service:v1.0 hemantdevgan/order-service:v1.0
docker tag product-service:v1.0 hemantdevgan/product-service:v1.0
docker tag user-service:v1.0 hemantdevgan/user-service:v1.0

echo "pushing images to docker hub"
docker push hemantdevgan/category-service:v1.0
docker push hemantdevgan/notification-service:v1.0
docker push hemantdevgan/order-service:v1.0
docker push hemantdevgan/product-service:v1.0
docker push hemantdevgan/user-service:v1.0

echo "deploying deployment and service"
kubectl apply -f k8s/category-service.yml
kubectl apply -f k8s/notification.yml
kubectl apply -f k8s/order-service.yml
kubectl apply -f k8s/product-service.yml
kubectl apply -f k8s/user-service.yml

echo "checking deployments"
kubectl get deployment -n ecom-basic
kubectl get pods -n ecom-basic -w