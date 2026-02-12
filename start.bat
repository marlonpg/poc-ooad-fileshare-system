@echo off
setlocal
set "JAVA_HOME=C:\Users\gamba\.jdks\corretto-25.0.1"
set "PATH=%JAVA_HOME%\bin;%PATH%"

if not exist "%JAVA_HOME%\bin\java.exe" (
	echo ERROR: JAVA_HOME does not point to a valid JDK: %JAVA_HOME%
	exit /b 1
)

echo Using Java:
"%JAVA_HOME%\bin\java" -version

echo Maven wrapper Java:
call .\mvnw.cmd -v

echo Starting backend...
call .\mvnw.cmd spring-boot:run