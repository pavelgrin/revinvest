#!/bin/bash

# Exit immediately if the script exits with a non-zero status
set -e

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"

PROJECT_ROOT="${SCRIPT_DIR}"
VENV_PATH="${PROJECT_ROOT}/data/.venv"
PYTHON_SCRIPT="${PROJECT_ROOT}/data/migration_tool.py"

if ! command -v python3 &> /dev/null; then
    echo "Error: 'python3' command is not found. Please ensure Python 3 is installed." >&2
    exit 1
fi
echo "Python is available. Proceeding..."

echo "Creating virtual environment: ${VENV_PATH}"
python3 -m venv "${VENV_PATH}"
if [ -f "${VENV_PATH}/bin/activate" ]; then
    ACTIVATE_SCRIPT="${VENV_PATH}/bin/activate"
    PYTHON_EXEC="${VENV_PATH}/bin/python"
else
    echo "Error: Could not find venv activation script" >&2
    exit 1
fi

echo "Activating venv and running the script..."
source "${ACTIVATE_SCRIPT}"
"${PYTHON_EXEC}" "${PYTHON_SCRIPT}" "$1" --root-dir "${PROJECT_ROOT}"

echo "Deactivating virtual environment"
deactivate

echo "Cleaning up temporary environment..."
if [ -d "${VENV_PATH}" ]; then
    echo "Removing venv directory: ${VENV_PATH}"
    rm -rf "${VENV_PATH}"
fi
