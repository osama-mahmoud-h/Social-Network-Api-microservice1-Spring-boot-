#!/bin/bash

# Script to generate PNG and SVG images from PlantUML diagrams
#
# Prerequisites:
# - Docker installed, OR
# - PlantUML installed (sudo apt-get install plantuml graphviz)

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo "Generating diagram images from PlantUML files..."
echo "Directory: $SCRIPT_DIR"
echo ""

# Check if PlantUML is installed
if command -v plantuml &> /dev/null; then
    echo "✓ Using installed PlantUML"

    # Generate PNG images
    echo "Generating PNG images..."
    plantuml -tpng *.puml

    # Generate SVG images
    echo "Generating SVG images..."
    plantuml -tsvg *.puml

    echo ""
    echo "✓ Done! Generated PNG and SVG files for all diagrams."

elif command -v docker &> /dev/null; then
    echo "✓ Using PlantUML Docker container"

    # Generate PNG images using Docker
    echo "Generating PNG images..."
    for file in *.puml; do
        echo "  - Processing $file"
        docker run --rm -v "$SCRIPT_DIR:/data" plantuml/plantuml:latest -tpng "/data/$file"
    done

    # Generate SVG images using Docker
    echo "Generating SVG images..."
    for file in *.puml; do
        echo "  - Processing $file"
        docker run --rm -v "$SCRIPT_DIR:/data" plantuml/plantuml:latest -tsvg "/data/$file"
    done

    echo ""
    echo "✓ Done! Generated PNG and SVG files for all diagrams."

else
    echo "✗ Error: Neither PlantUML nor Docker is installed."
    echo ""
    echo "Please install one of the following:"
    echo ""
    echo "Option 1 - Install PlantUML:"
    echo "  sudo apt-get update"
    echo "  sudo apt-get install plantuml graphviz"
    echo ""
    echo "Option 2 - Install Docker:"
    echo "  Follow instructions at: https://docs.docker.com/get-docker/"
    echo ""
    echo "Alternative - View online:"
    echo "  Copy the content of any .puml file and paste it at:"
    echo "  http://www.plantuml.com/plantuml/uml/"
    exit 1
fi

# List generated files
echo ""
echo "Generated files:"
ls -lh *.png *.svg 2>/dev/null | awk '{print "  " $9 " (" $5 ")"}'

echo ""
echo "You can now view the diagrams as PNG or SVG files!"
echo "To add them to README, reference them like:"
echo "  ![Diagram](diagrams/microservices-architecture.png)"
