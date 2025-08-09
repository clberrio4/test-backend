set -euo pipefail

# ---- Config ----
BUCKET_NAME="funds-backend-funds-prod-artifacts-465741428954-us-east-1"
REGION="us-east-1"
ZIP_NAME="app-deploy.zip"
S3_KEY="$ZIP_NAME"  # o "artifacts/$ZIP_NAME"

STACK_NAME="funds-backend-stack"
TEMPLATE="template.yml"
PARAMS_FILE="params.json"
CAPABILITIES="CAPABILITY_NAMED_IAM"

# ---- 1) Build ----
mvn -DskipTests clean package
# ./gradlew bootJar

# ---- 2) Artefactos temporales ----
cp target/*.jar app.jar
printf '%s' 'web: java -Dspring.profiles.active=prod -jar app.jar' > Procfile

# ---- 3) ZIP solo lo necesario ----
rm -f "$ZIP_NAME"
zip -q "$ZIP_NAME" app.jar Procfile

# ---- 4) Subir a S3 ----
echo "Subiendo al bucket: s3://$BUCKET_NAME/$S3_KEY"
aws s3 cp "./$ZIP_NAME" "s3://$BUCKET_NAME/$S3_KEY" --region "$REGION"

# ---- 5) Limpieza ----
rm -f app.jar Procfile

# ---- 6) Desplegando template (CloudFormation) ----
echo "Desplegando template a CloudFormation (update-stack): $STACK_NAME"
aws cloudformation update-stack \
  --stack-name "$STACK_NAME" \
  --template-body "file://$TEMPLATE" \
  --parameters "file://$PARAMS_FILE" \
  --capabilities "$CAPABILITIES" \
  --region "$REGION"
