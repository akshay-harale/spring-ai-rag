# Stage 1: Build the application
FROM maven:3.8.4-openjdk-17 AS build

# Set the working directory for the build
WORKDIR /app

# Copy the pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the source code and build the application
COPY src ./src
RUN mvn package -DskipTests

# Stage 2: Run the application
FROM openjdk:17-jre-slim

# Set the working directory in the container
WORKDIR /app

# Copy the built jar file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Make port 8080 available to the world outside this container
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java","-jar","app.jar"]
