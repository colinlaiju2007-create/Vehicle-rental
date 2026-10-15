# Build the Spring Boot executable JAR with Java 21.
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
RUN mvn -B -q clean package -DskipTests

# Run the application with a small Java 21 runtime image.
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/target/apexdrive-1.0.0.jar /app/apexdrive.jar
ENV PORT=8080
EXPOSE 8080
CMD ["sh", "-c", "java -Djava.io.tmpdir=/tmp -jar /app/apexdrive.jar"]
