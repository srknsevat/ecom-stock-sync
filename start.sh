#!/bin/bash

echo "Starting Ecom Stock Sync Application..."
echo "Port: $PORT"
echo "Profile: railway"

# Start the application
exec java -jar app.jar --spring.profiles.active=railway --server.port=$PORT
