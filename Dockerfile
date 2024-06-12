#!/bin
# Stage 1: Build the application
FROM eclipse-temurin:21-jdk as builder

# Set the working directory inside the container
WORKDIR /app

# Copy the Spring Boot application source code
COPY . .

# Set execute permission for gradlew script
RUN chmod +x gradlew

# Build the application using Gradle, skipping tests
RUN ./gradlew build -x test --scan

# Create a custom JRE using jlink with all necessary modules
RUN jlink --module-path $JAVA_HOME/jmods \
    --add-modules ALL-MODULE-PATH \
    --compress=2 --strip-debug --no-header-files --no-man-pages \
    --output /custom-jre

# Stage 2: Create the runtime image
FROM eclipse-temurin:21-jre

# Set the working directory inside the container
WORKDIR /app

# Copy the packaged application from the builder stage
COPY --from=builder /app/build/libs/*.jar /app/app.jar

# Copy the custom JRE from the builder stage
COPY --from=builder /custom-jre /opt/custom-jre

# Set the PATH environment variable to use the custom JRE
ENV PATH="/opt/custom-jre/bin:$PATH"

# Copy H2 database files if needed
COPY --from=builder /app/roamGram.mv.db /app/roamGram.mv.db

# Expose the port on which the application will run
EXPOSE 8080

# Command to run the Spring Boot application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
