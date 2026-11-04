#!/bin/bash

set -e

echo "deploying services----"
kubectl apply -f k8s/auth-service.yml
kubectl apply -f k8s/cart-service.yml
kubectl apply -f k8s/category-service.yml
kubectl apply -f k8s/frontend.yml
kubectl apply -f k8s/notification.yml
kubectl apply -f k8s/order-service.yml
kubectl apply -f k8s/product-service.yml
kubectl apply -f k8s/user-service.yml

kubectl get pods -n ecom-basic

