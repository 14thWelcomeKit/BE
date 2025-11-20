# Use an official Eclipse Temurin runtime as a parent image
FROM --platform=linux/amd64 eclipse-temurin:17-jre

# Set the working directory
WORKDIR /app

# Copy the Spring Boot jar file into the container
COPY build/libs/Welcomekit_BE-0.0.1-SNAPSHOT.jar .

# Expose the port the app runs on
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java", "-jar", "Welcomekit_BE-0.0.1-SNAPSHOT.jar"]
