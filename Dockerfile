# Stage 1: Build the application
FROM maven:3.8.5-openjdk-17-slim AS build

# Set the working directory
WORKDIR /build

# Copy the project files
COPY . .

# Build the project
RUN mvn clean install -DskipTests

# Stage 2: Package the application into the final image
FROM eclipse-temurin:17-jdk-alpine

# Set the working directory
WORKDIR /app

# Copy the built jar file from the previous stage
COPY --from=build /build/target/innomapper_service.jar innomapper_service.jar

# Expose the port the application runs on
EXPOSE 8081

# Define the command to run the application
CMD ["java", "-jar", "-Dspring.profiles.active=stage", "innomapper_service.jar"]