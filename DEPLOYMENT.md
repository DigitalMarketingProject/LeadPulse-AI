# First deployment: Render + Aiven MySQL

This project is a Spring Boot application that serves both Thymeleaf pages and REST APIs.
Deploy it as a Render **Web Service** using the included Dockerfile, not as a Static Site.
The production profile is currently configured for MySQL, so Aiven MySQL avoids an
unnecessary database-driver and schema conversion.

## 1. Create the database in Aiven

1. Create an Aiven MySQL service and database.
2. Create/use a restricted application database user.
3. Enable SSL/TLS and keep Aiven's CA certificate/private connection details safe.
4. Copy the JDBC connection information. `DB_URL` must be a JDBC URL of the form:

   ```text
   jdbc:mysql://MYSQL_HOST:MYSQL_PORT/DATABASE_NAME?sslMode=REQUIRED
   ```

5. Run `src/main/resources/db/production-schema-mysql.sql` against the new, empty database
   using Aiven's query console or a trusted SQL client. Do not run it against an existing
   production database without first reviewing and backing that database up.

The deployed application uses `ddl-auto=validate`; it will not silently create or mutate the
production schema.

## 2. Create a private GitHub repository

1. Create a **private** repository on GitHub.
2. Do not upload `.env` files, database dumps, certificates, API keys, or passwords.
3. From this folder, initialize and push the branch:

   ```powershell
   cd C:\Marketing\marketingleadscoring
   git init -b main
   git add .
   git status --short
   git commit -m "Prepare LeadPulse AI for deployment"
   git remote add origin https://github.com/YOUR_ACCOUNT/YOUR_REPOSITORY.git
   git push -u origin main
   ```

Review `git status` before committing. Never commit credentials. If a secret was committed,
rotate it; removing it in a later commit is not enough.

## 3. Deploy the Render Web Service

1. In Render, choose **New > Blueprint** and connect the private GitHub repository, or create
   a Docker Web Service using the repository's Dockerfile.
2. The included `render.yaml` defines the production profile and lists the secret environment
   variables Render must request.
3. Add the environment variables described below in Render. Do not place secret values in
   `render.yaml` or GitHub.
4. Deploy. Render builds the Java 21 image and starts Spring Boot on Render's assigned `PORT`.
5. Confirm the service is healthy at its `onrender.com` URL and the homepage returns HTTP 200.

## 4. Required Render environment variables

| Variable | Purpose |
|---|---|
| `SPRING_PROFILES_ACTIVE` | Must be `production` |
| `DB_URL` | Aiven JDBC URL with SSL required |
| `DB_USERNAME` | Aiven database username |
| `DB_PASSWORD` | Aiven database password |
| `LEADPULSE_BOOTSTRAP_KEY` | Random, high-entropy administrative provisioning secret |
| `LEADPULSE_ADMIN_USERNAME` | Administrative UI username |
| `LEADPULSE_ADMIN_PASSWORD_BCRYPT` | BCrypt hash of a unique strong password |
| `LEADPULSE_ALLOWED_ORIGINS` | Comma-separated exact HTTPS browser origins; never `*` |

`DB_DRIVER` and `DB_DIALECT` default to MySQL values. `PORT` is supplied by Render.
Generate and store all secrets in Render's environment/secrets UI. Use a trusted BCrypt
generator for the administrator password; do not paste a plaintext password into the
deployment manifest or source tree.

## 5. Important launch limitations

- The production UI uses one configured platform admin, not separate company user accounts.
- The built-in rate limiter is in-memory and only coordinates one application instance.
- Organization API keys are server-side secrets. Do not embed them in public website JavaScript.
- The local browser tracker currently uses the organization key and is development-only.
- Webhook HMAC currently uses the organization API key as the signing secret.
- Configure Aiven backups, Render health alerts, and a recovery procedure before storing
  real customer information.
- Check applicable privacy/consent requirements and obtain legal guidance before tracking
  production visitors.

The production profile, privacy endpoints, and application security provide a deployment
baseline, not a security certification or a substitute for a production readiness review.
