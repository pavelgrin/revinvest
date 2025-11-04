@echo off
setlocal

set PROJECT_ROOT=%~dp0
set VENV_PATH=%PROJECT_ROOT%data\.venv
set REQ_PATH=%PROJECT_ROOT%data\requirements.txt
set PYTHON_SCRIPT=%PROJECT_ROOT%data\migration_tool.py

where py -3 --version >nul 2>nul
if %errorlevel% neq 0 (
    echo Error: 'py -3' command is not found. Please ensure the Python Launcher is installed and Python 3 version is available. >&2
    exit /b 1
)
echo Python is available. Proceeding...

echo Creating virtual environment: %VENV_PATH%
py -3 -m venv %VENV_PATH%
if exist %VENV_PATH%\Scripts\activate.bat (
    set ACTIVATE_SCRIPT=%VENV_PATH%\Scripts\activate.bat
    set PYTHON_EXEC=%VENV_PATH%\Scripts\python.exe
) else (
    echo Error: Could not find venv activation script
    exit /b 1
)

echo Activating venv and running the script...
call %ACTIVATE_SCRIPT%
pip install -r %REQ_PATH%
%PYTHON_EXEC% %PYTHON_SCRIPT% %1 --root-dir "%PROJECT_ROOT%"

echo Deactivating virtual environment
call deactivate

echo Cleaning up temporary environment...
if exist %VENV_PATH% (
    echo Removing venv directory: %VENV_PATH%
    rmdir /S /Q %VENV_PATH%
)
pause
