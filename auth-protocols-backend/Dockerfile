# Stage 1: Build the application
FROM maven:3.9.5-amazoncorretto-21 as builder

# Copy the source code
COPY src /app/src
COPY pom.xml /app

# Set the working directory
WORKDIR /app

# Build the application
RUN mvn clean package

# Stage 2: Create the final image
FROM openjdk:21-jdk-slim

# Set a working directory inside the container
WORKDIR /app

# Copy Kerberos configuration file
COPY src/main/resources/kerberos/krb5.conf /etc/krb5.conf

# Copy the built JAR from the builder stage
COPY --from=builder /app/target/auth-protocols-backend-*.jar /app/app.jar

# Expose the port that your application uses
EXPOSE 3000

# Command to run the application
CMD ["java", "-jar", "/app/app.jar"]