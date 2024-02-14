# Use the official Maven image
FROM maven:3.9.5-eclipse-temurin-21 AS build
LABEL authors="heying.yu"

# Set the working directory in the builder stage
WORKDIR /app

# Copy the pom.xml file
COPY ./pom.xml ./pom.xml

# Download all required dependencies into one layer
RUN mvn dependency:go-offline -B

# Copy other project files and build
COPY ./src ./src

RUN mvn package

# Run stage
FROM openjdk:21-slim

RUN apt-get update && apt-get install -y ffmpeg && rm -rf /var/lib/apt/lists/*


WORKDIR /app

# Copy the jar file from build stage
COPY --from=build /app/target/*.jar /app/app.jar

EXPOSE 8095

ENTRYPOINT ["java", "-jar", "/app/app.jar"]

