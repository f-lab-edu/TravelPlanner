# Stage 1: Build the application
FROM eclipse-temurin:21-jdk as builder

# Set the working directory inside the container
WORKDIR /app

# Copy the Spring Boot application source code
COPY . .

# To ensure build compatibility with Windows based commit -> CRLF convert to LF
RUN apt-get update && apt-get install -y dos2unix

# Ensure correct permissions and line endings for gradlew script
RUN dos2unix gradlew && chmod +x gradlew

# Build the application using Gradle, skipping tests
RUN ./gradlew build --scan

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

# Expose the port on which the application will run
EXPOSE 8080

# Command to run the Spring Boot application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
