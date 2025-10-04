#!/bin/bash

echo "Starting Ecom Stock Sync Application..."
echo "Port: $PORT"
echo "Profile: railway"

# Set default port if not provided
export PORT=${PORT:-8080}

# JVM options for Railway
export JAVA_OPTS="-Xmx512m -Xms256m -Djava.security.egd=file:/dev/./urandom"

# Start the application
exec java $JAVA_OPTS -jar app.jar --spring.profiles.active=railway --server.port=$PORT
