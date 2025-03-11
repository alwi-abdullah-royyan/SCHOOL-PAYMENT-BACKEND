@echo off
REM Build the project using Maven
echo Building project...
mvn clean package

REM Check if build was successful
IF %ERRORLEVEL% NEQ 0 (
    echo Build failed! Exiting...
    exit /b 1
)

REM Automatically detect the .jar file in the target directory
echo Locating jar file...
for %%f in (target\*.jar) do set JAR_FILE=%%f

IF NOT DEFINED JAR_FILE (
    echo No JAR file found in target directory!
    exit /b 1
)

echo Running %JAR_FILE%...
java -jar %JAR_FILE%
