#!/bin/bash

# Create lib directory if it doesn't exist
mkdir -p lib

# Download SQLite JDBC driver
echo "Downloading SQLite JDBC driver..."
curl -L https://github.com/xerial/sqlite-jdbc/releases/download/3.45.1.0/sqlite-jdbc-3.45.1.0.jar -o lib/sqlite-jdbc.jar

# Add SQLite JDBC to classpath
echo "Adding SQLite JDBC to classpath..."
export CLASSPATH=$CLASSPATH:lib/sqlite-jdbc.jar

echo "Setup complete! You can now run the database tests." 