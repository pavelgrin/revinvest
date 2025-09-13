@echo off

if "%1"=="" (
    echo Error: Please provide command: up or down
    exit /b 1
)

mvn compile exec:java -D"exec.args"="%1"
