# ApexDrive: deploy to Render (Java Spring Boot + persistent SQLite)

## Important
- This is a college/demo deployment setup, not a production-ready rental/payment platform.
- The admin authentication implementation needs a security review before real customer data is used. Do not collect real payment details or sensitive customer data.
- The Render blueprint uses a paid Starter web service because SQLite needs a persistent disk. A free ephemeral filesystem can lose database changes on redeploy/restart.

## Deploy
1. Push the contents of this folder to a GitHub repository (the `pom.xml`, `src/`, `Dockerfile`, `render.yaml`, and `.dockerignore` must be at repository root).
2. In Render, choose **New + → Blueprint**, connect the repository, and let Render read `render.yaml`.
3. Confirm the service and persistent disk costs before applying. The disk is mounted at `/var/data`.
4. When prompted for environment variables, set `APEXDRIVE_ADMIN_USERNAME` to your chosen username and `APEXDRIVE_ADMIN_PASSWORD` to a strong unique password. Do not commit these values to GitHub.
5. Deploy and wait for the build to finish.
6. Open the generated `https://...onrender.com` URL. Test `/api/vehicles`, create a test booking, restart/redeploy, and verify the booking remains.

## Database
The application uses `APEXDRIVE_DB_URL=jdbc:sqlite:/var/data/apexdrive.db`, so the SQLite file is on Render's persistent disk, not the container's temporary filesystem. Back up the database before any destructive change.

## Local run
`run.bat` is intended for Windows. For Maven-equipped Java 21 environments, run `mvn spring-boot:run` from this folder. The default local database is `apexdrive.db` in the working directory.
