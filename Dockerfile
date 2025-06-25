# Use official OpenJDK 17 runtime
FROM openjdk:17-jdk-slim

# Set working directory in the container
WORKDIR /app

# Copy jar file from host to container
COPY target/personal-finance-manager-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8080

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
