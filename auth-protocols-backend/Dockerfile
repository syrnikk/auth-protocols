# Use the OpenJDK image with Java 21
FROM openjdk:21-jdk-slim

# Set a working directory inside the container
WORKDIR /app

# Add Kerberos configuration file
COPY src/main/resources/kerberos/krb5.conf /etc/krb5.conf

# Copy the JAR file into the container
COPY ./target/auth-protocols-backend-*.jar /app/app.jar

# Expose the port that your application uses
EXPOSE 3000

# Command to run the application
CMD ["java", "-jar", "/app/app.jar"]