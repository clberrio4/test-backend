$ErrorActionPreference = 'Stop'

# ---- Config ----
$BucketName = "funds-backend-funds-prod-artifacts-465741428954-us-east-1"
$Region     = "us-east-1"
$ZipName    = "app-deploy.zip"
$S3Key      = $ZipName            # o "artifacts/$ZipName"

$StackName   = "funds-backend-stack"
$Template    = "template.yml"
$ParamsFile  = "params.json"
$Capabilities = "CAPABILITY_NAMED_IAM"

# ---- 1) Build ----
mvn -DskipTests clean package
# ./gradlew bootJar

# ---- 2) Artefactos temporales ----
Copy-Item target\*.jar app.jar
"web: java -Dspring.profiles.active=prod -jar app.jar" | Out-File -NoNewline -Encoding ascii Procfile

# ---- 3) ZIP solo lo necesario ----
Remove-Item $ZipName -ErrorAction SilentlyContinue
Compress-Archive -LiteralPath app.jar, Procfile -DestinationPath $ZipName

# ---- 4) Subir a S3 ----
Write-Host "Subiendo al bucket: s3://$BucketName/$S3Key"
aws s3 cp ".\$ZipName" "s3://$BucketName/$S3Key" --region $Region

# ---- 5) Limpieza ----
Remove-Item app.jar, Procfile -Force

# ---- 6) Desplegando template (CloudFormation) ----
Write-Host "Desplegando template a CloudFormation: $StackName"
aws cloudformation create-stack `
  --stack-name funds-backend-stack `
  --template-body file://template.yml `
  --parameters file://params.json `
  --capabilities CAPABILITY_NAMED_IAM `
  --region us-east-1
