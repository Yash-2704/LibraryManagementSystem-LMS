#!/bin/bash
set -e # Exit immediately if a command exits with a non-zero status.
# set -x # Print each command before executing it (for debugging)

# Navigate to the script's directory to ensure relative paths work correctly
cd "$(dirname "$0")"

# Define source and resource paths
SRC_JAVA_DIR="src/main/java"
SRC_RESOURCES_DIR="src/main/resources"

# Clean old class files from GUI source directory (no longer needed here if compiling from src)
# echo "Cleaning old class files from GUI source directory..."
# find $SRC_JAVA_DIR/GUI -name "*.class" -type f -print -delete # Example, adjust if needed

# echo "Verifying GUI source directory is clean of .class files..."
# CLASS_FILES_IN_GUI=$(find $SRC_JAVA_DIR/GUI -name "*.class" -type f -print)
# if [ -n "$CLASS_FILES_IN_GUI" ]; then
#     echo "ERROR: .class files still found in $SRC_JAVA_DIR/GUI source directory after cleaning:"
#     echo "$CLASS_FILES_IN_GUI"
#     echo "Please remove them manually and investigate why the clean step failed."
#     exit 1
# fi
# echo "$SRC_JAVA_DIR/GUI source directory is clean."

echo "Cleaning old class files from bin directory..."
find bin -name "*.class" -type f -print -delete

# Define the classpath - include resources directory
EFFECTIVE_CLASSPATH="bin:$SRC_RESOURCES_DIR:lib/flatlaf-3.6.jar:lib/mysql-connector-j-9.3.0.jar"

echo "Compiling all Java source files..."
# Compile from the new source directory structure
javac -d bin -cp "$EFFECTIVE_CLASSPATH" \
    $SRC_JAVA_DIR/GUI/*.java \
    $SRC_JAVA_DIR/GUI/dialogs/*.java \
    $SRC_JAVA_DIR/GUI/utils/*.java \
    $SRC_JAVA_DIR/Database/DBConnection.java

# Check if compilation was successful by looking for key class files
if [ ! -f "bin/GUI/Main.class" ] || [ ! -f "bin/GUI/utils/TableUtils.class" ]; then
    echo "ERROR: Compilation failed. Key class files not found in bin directory."
    exit 1
fi
echo "Compilation successful."

echo "Running the application..."
echo "--- Effective Classpath ---"
echo "$EFFECTIVE_CLASSPATH"
echo "---------------------------"

java -cp "$EFFECTIVE_CLASSPATH" GUI.Main

# set +x # Turn off command printing

echo "Application terminated." 