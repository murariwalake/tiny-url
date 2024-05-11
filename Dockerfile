# Use OpenJDK 17 as the base image
FROM openjdk:17

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file into the container
COPY target/tinyurl-0.0.1-SNAPSHOT.jar /app/tinyurl-0.0.1-SNAPSHOT.jar

# Expose the port that your Spring Boot application uses (default is 8080)
EXPOSE 8080

# Command to run the Spring Boot application when the container starts
CMD ["java", "-jar", "tinyurl-0.0.1-SNAPSHOT.jar"]
