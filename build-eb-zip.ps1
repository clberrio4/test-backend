$ErrorActionPreference = "Stop"

Write-Host "==> Limpiando y compilando (skip tests)…"
if (Test-Path ".\mvnw.cmd") {
  .\mvnw.cmd clean package -DskipTests
} else {
  mvn clean package -DskipTests
}

Write-Host "==> Buscando el JAR en target/…"
$jar = Get-ChildItem -Path "target" -Filter *.jar | Select-Object -First 1
if (-not $jar) {
  Write-Error "No se encontró ningún JAR en target/. ¿Falló el build?"
}

Write-Host "   JAR encontrado: $($jar.FullName)"

Write-Host "==> Preparando carpeta deploy/…"
if (Test-Path ".\deploy") { Remove-Item ".\deploy" -Recurse -Force }
New-Item -ItemType Directory -Path ".\deploy" | Out-Null

Write-Host "==> Copiando JAR a deploy\app.jar…"
Copy-Item $jar.FullName ".\deploy\app.jar" -Force

Write-Host "==> Creando Procfile…"
@'
web: java $JAVA_OPTS -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:prod} -jar app.jar
'@ | Set-Content -NoNewline -Encoding UTF8 ".\deploy\Procfile"

Write-Host "==> Carpeta deploy lista."
Write-Host "Contenido:"
Get-ChildItem ".\deploy"
