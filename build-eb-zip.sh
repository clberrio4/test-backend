#!/usr/bin/env bash
set -euo pipefail

# 1) Ir a la raíz del proyecto (si corres desde otro lado)
# cd "$(dirname "$0")"

echo "==> Limpiando y compilando (skip tests)…"
if [ -f ./mvnw ]; then
  ./mvnw clean package -DskipTests
else
  mvn clean package -DskipTests
fi

echo "==> Buscando el JAR en target/…"
JAR_PATH=$(ls -1 target/*.jar | grep -E '\.jar$' | head -n1)
if [ -z "$JAR_PATH" ]; then
  echo "ERROR: No se encontró ningún JAR en target/. ¿Falló el build?"
  exit 1
fi
echo "   JAR encontrado: $JAR_PATH"

echo "==> Preparando carpeta deploy/…"
rm -rf deploy
mkdir -p deploy

echo "==> Copiando JAR a deploy/app.jar…"
cp "$JAR_PATH" deploy/app.jar

echo "==> Creando Procfile…"
cat > deploy/Procfile <<'EOF'
web: java $JAVA_OPTS -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:prod} -jar app.jar
EOF

echo "==> Empaquetando ZIP…"
cd deploy
jar -cMf ../deploy.zip .
cd ..

echo "==> Listo!"
echo "Sube este archivo a Elastic Beanstalk: app-deploy.zip"