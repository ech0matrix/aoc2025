#!/bin/bash

# Navigate to the src directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SRC_DIR="$SCRIPT_DIR/../src"

cd "$SRC_DIR" || exit 1

# Find the highest existing Day number
MAX_DAY=$(ls Day*.kt 2>/dev/null | sed 's/Day\([0-9]\{2\}\)\.kt/\1/' | sort -n | tail -1)

# Calculate next day number
if [ -z "$MAX_DAY" ]; then
    NEXT_DAY=01
else
    NEXT_DAY=$(printf "%02d" $((10#$MAX_DAY + 1)))
fi

# Check if we're beyond Day25
if [ $((10#$NEXT_DAY)) -gt 25 ]; then
    echo "Error: Cannot create Day$NEXT_DAY (max is Day25)"
    exit 1
fi

# Check if files already exist
if [ -f "Day${NEXT_DAY}.kt" ]; then
    echo "Error: Day${NEXT_DAY}.kt already exists"
    exit 1
fi

echo "Creating Day $NEXT_DAY files..."

# Copy the template files
cp Day00.kt "Day${NEXT_DAY}.kt"
cp Day00.txt "Day${NEXT_DAY}.txt"
cp Day00_test.txt "Day${NEXT_DAY}_test.txt"

# Update the readInput calls in the .kt file
sed -i "s/Day00/Day${NEXT_DAY}/g" "Day${NEXT_DAY}.kt"

echo "✓ Created Day${NEXT_DAY}.kt"
echo "✓ Created Day${NEXT_DAY}.txt"
echo "✓ Created Day${NEXT_DAY}_test.txt"
echo ""
echo "Ready to work on Day $NEXT_DAY!"
