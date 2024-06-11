# Use a multi-stage build to optimize the final image size
# Stage 1: Build the application
FROM eclipse-temurin:21-jdk as builder

# Set the working directory inside the container
WORKDIR /app

# Copy the Spring Boot application source code
COPY . .

# Package the application
RUN ./mvnw package -DskipTests

# Create a custom JRE using jlink
RUN jlink --module-path $JAVA_HOME/jmods --add-modules java.base,java.logging,java.sql \
    --output /custom-jre --compress=2 --strip-debug --no-header-files --no-man-pages

# Stage 2: Create the runtime image
FROM eclipse-temurin:21-jre

# Set the working directory inside the container
WORKDIR /app

# Copy the packaged application from the builder stage
COPY --from=builder /app/target/*.jar /app/app.jar

# Copy the custom JRE from the builder stage
COPY --from=builder /custom-jre /opt/custom-jre

# Set the PATH environment variable to use the custom JRE
ENV PATH="/opt/custom-jre/bin:$PATH"

# Copy H2 database files if needed
COPY --from=builder /app/h2-data /app/h2-data

# Expose the port on which the application will run
EXPOSE 8080

# Command to run the Spring Boot application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
