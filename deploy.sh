#!/bin/bash
set -euo pipefail

# Get the directory where this script lives
DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$DIR"

JAR_FILE="target/user-auth-1.0.0.jar"

echo "🔨 Building JAR..."
./mvnw clean package -DskipTests

if [ ! -f "$JAR_FILE" ]; then
  echo "❌ Expected build artifact not found: $JAR_FILE"
  exit 1
fi

echo "🐳 Building Docker image..."
docker build -t chatokjunior/legalaid-user-auth "$DIR"

echo "🚀 Pushing to Docker Hub..."
docker push chatokjunior/legalaid-user-auth

echo "✅ Done! Go to Render and click Manual Deploy."
