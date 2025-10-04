#!/bin/bash

echo "Starting Ecom Stock Sync Application..."
echo "Port: $PORT"
echo "Profile: railway"

# Set default port if not provided
export PORT=${PORT:-8080}

# Start the application
exec java -jar app.jar --spring.profiles.active=railway --server.port=$PORT
