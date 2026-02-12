$ErrorActionPreference = "Stop"

& "./mvnw.cmd" -q -DskipTests clean package
