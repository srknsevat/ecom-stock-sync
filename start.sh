#!/bin/bash

echo "Starting Ecom Stock Sync Application..."
echo "Port: $PORT"
echo "Profile: railway"

# Wait for port to be available
sleep 2

# Start the application
java -jar app.jar --spring.profiles.active=railway --server.port=$PORT
