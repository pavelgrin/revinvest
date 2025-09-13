#!/bin/bash

if [ -z "$1" ]; then
    echo "Error: Please provide command: up or down"
    exit 1
fi

ARGUMENT="$1"

mvn compile exec:java -Dexec.args="$1"
