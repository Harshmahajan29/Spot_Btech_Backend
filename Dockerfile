# --- STAGE 1: Build the Application ---
FROM maven:3.8.8-eclipse-temurin-17 AS build
WORKDIR /app

# Copy the source code and pom.xml into the container
COPY pom.xml .
COPY src ./src

# Compile and package the application inside the container (bypasses your local machine's Java entirely!)
RUN mvn clean package -DskipTests

# --- STAGE 2: Run the Application ---
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the compiled .jar file straight from STAGE 1
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
