#!/bin/bash

# Restaurant Chatbot - Setup Script

echo "=========================================="
echo "Restaurant Chatbot - Setup"
echo "=========================================="
echo ""

# Check Java
echo "✓ Checking Java..."
if ! command -v java &> /dev/null
then
    echo "✗ Java is not installed. Please install Java 11 or later."
    exit 1
fi
java_version=$(java -version 2>&1 | head -1)
echo "✓ Found: $java_version"
echo ""

# Check Maven
echo "✓ Checking Maven..."
if ! command -v mvn &> /dev/null
then
    echo "✗ Maven is not installed. Please install Maven 3.6+."
    exit 1
fi
mvn_version=$(mvn -version 2>&1 | head -1)
echo "✓ Found: $mvn_version"
echo ""

# Build the project
echo "Building project..."
mvn clean install
if [ $? -ne 0 ]; then
    echo "✗ Build failed."
    exit 1
fi
echo "✓ Build successful!"
echo ""

# Check for API key
if [ -z "$ANTHROPIC_API_KEY" ]; then
    echo "⚠ ANTHROPIC_API_KEY is not set."
    echo "The server will still run, but API calls will fail."
    echo "To set it later:"
    echo "  export ANTHROPIC_API_KEY=sk-ant-api03-your-key-here"
    echo ""
fi

# Start the application
echo "Starting application on http://localhost:8080"
echo "Press Ctrl+C to stop"
echo ""
mvn spring-boot:run
