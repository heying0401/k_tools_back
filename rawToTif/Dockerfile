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
FROM openjdk:21-slim AS runtime

RUN apt-get update && apt-get install -y \
    autoconf \
    automake \
    libtool \
    make \
    gcc \
    g++ \
    libgomp1 \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /usr/src/libraw

# Copy the LibRaw source code from the host
COPY ./LibRaw-0.21.2 /usr/src/libraw

# Compile and install LibRaw
RUN ./configure && \
    make && \
    make install

RUN apt-get purge -y --auto-remove autoconf automake libtool make gcc g++

# Copy the jar file from build stage
COPY --from=build /app/target/*.jar /app/app.jar

EXPOSE 8097

ENTRYPOINT ["java", "-jar", "/app/app.jar"]

