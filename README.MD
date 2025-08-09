# 🏗️ Guía de Despliegue – **Funds Backend**

Este documento describe el proceso de instalación, ejecución y despliegue del proyecto **Funds Backend**, así como las herramientas necesarias para su correcto funcionamiento en entornos **local** y **producción**.

---

## 📋 **Requisitos Previos**

Antes de comenzar, asegúrate de tener instalado:

- [Maven 3.9+](https://maven.apache.org/download.cgi)
- [Java 17 o superior](https://adoptium.net/)
- [AWS CLI](https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html) configurado con las credenciales adecuadas
- [NoSQL Workbench para DynamoDB](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/workbench.settingup.html) (para entorno local)
- Utilidad `zip` instalada en el sistema (**solo Linux/Mac**)
  ```bash
  sudo apt install zip    # Debian/Ubuntu
  brew install zip        # macOS
  ```

---

## ⚙️ **Ejecución en Local**

1. Asegúrate de tener corriendo **DynamoDB Local** desde NoSQL Workbench.
2. Ejecuta el proyecto con el perfil `local`:
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=local
   ```

---

## 🚀 **Despliegue en Producción**

### 1️⃣ Preparar Configuración

- Crea el archivo:
  ```
  src/main/resources/application-prod.properties
  ```
  con las variables necesarias para producción.

- Renombra y ajusta `params-copy.json` a:
  ```
  params.json
  ```
  completando los valores de cada parámetro.

---

### 2️⃣ Ejecutar Script de Build y Deploy

#### **Windows (PowerShell)**
```powershell
.uild.ps1
```

#### **Linux / macOS**
```bash
chmod +x build.sh
./build.sh
```
> 💡 Ambos scripts generan `app-deploy.zip`, lo suben al bucket S3 y actualizan el stack de **AWS CloudFormation**.

---

## 🔗 **URLs de Acceso en Producción**

- **Swagger UI:** `/api/docs`
- **Servicios API:** `/api/*`

> Estas rutas estarán disponibles en el dominio configurado para el entorno productivo.

---

## 🔄 **Actualización de Producción**

Si necesitas actualizar el código en producción **sin cambiar la estructura del stack**:

1. Sube manualmente el nuevo `.zip` al bucket S3.
2. Ejecuta:
   ```bash
   aws cloudformation update-stack      --stack-name funds-backend-stack      --template-body file://template.yml      --parameters file://params.json      --capabilities CAPABILITY_NAMED_IAM      --region us-east-1
   ```

⚠️ **Importante:** Si el cambio implica modificar recursos estructurales (por ejemplo, tablas DynamoDB, roles IAM, etc.), será necesario eliminar el stack y volver a desplegar.

---

## ❌ **Eliminar Stack**

```bash
aws cloudformation delete-stack   --stack-name funds-backend-stack   --region us-east-1
```

Luego, vuelve a ejecutar el script correspondiente a tu sistema operativo para recrearlo.

---

## 📚 **Referencias**

- [Maven – Apache](https://maven.apache.org/)
- [Java 17 – Adoptium](https://adoptium.net/)
- [AWS CLI – Instalación](https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html)
- [AWS CloudFormation – Documentación](https://docs.aws.amazon.com/cloudformation/)
- [NoSQL Workbench – AWS](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/workbench.settingup.html)
- [DynamoDB Local – Guía](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBLocal.html)
