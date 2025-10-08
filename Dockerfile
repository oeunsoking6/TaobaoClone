# This Dockerfile uses multi-stage builds to handle different services

# --- Build stage for user-service ---
FROM node:18-slim AS user-service-build
WORKDIR /app
COPY backend/user-service/package*.json ./
RUN npm install
COPY backend/user-service/ .
CMD [ "node", "index.js" ]

# --- Build stage for product-service ---
FROM node:18-slim AS product-service-build
WORKDIR /app
COPY backend/product-service/package*.json ./
RUN npm install
COPY backend/product-service/ .
CMD [ "node", "index.js" ]

# --- Build stage for recommendation-service ---
FROM node:18-slim AS recommendation-service-build
WORKDIR /app
COPY backend/recommendation-service/package*.json ./
RUN npm install
COPY backend/recommendation-service/ .
CMD [ "node", "index.js" ]