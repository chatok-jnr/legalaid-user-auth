#!/bin/bash

# Get the directory where this script lives
DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$DIR"

echo "🔨 Building JAR..."
./mvnw clean package -DskipTests

echo "🐳 Building Docker image..."
docker build -t chatokjunior/legalaid-user-auth "$DIR"

echo "🚀 Pushing to Docker Hub..."
docker push chatokjunior/legalaid-user-auth

echo "✅ Done! Go to Render and click Manual Deploy."
