# Stage 1: Build the application
FROM eclipse-temurin:21-jdk as builder

# Set the working directory inside the container
WORKDIR /app

# Copy the Spring Boot application source code
COPY . .

# Accept build arguments and set them as environment variables
ARG POSTGRES_USERNAME
ARG POSTGRES_PASSWORD
ARG POSTGRES_URL
ARG REDIS_HOST
ARG REDIS_PORT
ARG AWS_S3_ACCESS_KEY
ARG AWS_S3_SECRET_KEY
ARG AWS_S3_BUCKET
ARG AWS_REGION
ARG AWS_EC2_URI
ARG GOOGLE_CLIENT_SECRET
ARG GOOGLE_CLIENT_ID
ARG JWT_KEY
ARG KEY_STORE_PASSWORD
ARG FRONTEND_URL
ARG EMAILSENDER_EMAIL
ARG EMAILSENDER_PASSWORD

ENV POSTGRES_USERNAME=${POSTGRES_USERNAME}
ENV POSTGRES_PASSWORD=${POSTGRES_PASSWORD}
ENV POSTGRES_URL=${POSTGRES_URL}
ENV REDIS_HOST=${REDIS_HOST}
ENV REDIS_PORT=${REDIS_PORT}
ENV AWS_S3_ACCESS_KEY=${AWS_S3_ACCESS_KEY}
ENV AWS_S3_SECRET_KEY=${AWS_S3_SECRET_KEY}
ENV AWS_S3_BUCKET=${AWS_S3_BUCKET}
ENV AWS_REGION=${AWS_REGION}
ENV AWS_EC2_URI=${AWS_EC2_URI}
ENV GOOGLE_CLIENT_SECRET=${GOOGLE_CLIENT_SECRET}
ENV GOOGLE_CLIENT_ID=${GOOGLE_CLIENT_ID}
ENV JWT_KEY=${JWT_KEY}
ENV KEY_STORE_PASSWORD=${KEY_STORE_PASSWORD}
ENV FRONTEND_URL=${FRONTEND_URL}
ENV EMAILSENDER_EMAIL=${EMAILSENDER_EMAIL}
ENV EMAILSENDER_PASSWORD=${EMAILSENDER_PASSWORD}

# To ensure build compatibility with Windows based commit -> CRLF convert to LF and get netcat
# Install required packages in a single RUN command to ensure compatibility
RUN apt-get update && apt-get install -y dos2unix

# Verify installation of dos2unix and netcat
RUN which dos2unix && dos2unix --version

# Ensure correct permissions and line endings for gradlew script
RUN dos2unix gradlew && chmod +x gradlew

# Build the application using Gradle
RUN ./gradlew build

# Create a custom JRE using jlink with all necessary modules
RUN jlink --module-path $JAVA_HOME/jmods \
    --add-modules ALL-MODULE-PATH \
    --compress=2 --strip-debug --no-header-files --no-man-pages \
    --output /custom-jre

# Stage 2: Create the runtime image
FROM eclipse-temurin:21-jre

# Set the working directory inside the container
WORKDIR /app

# Create log directory
RUN mkdir -p /var/log/spring-boot

# Copy the packaged application from the builder stage
COPY --from=builder /app/build/libs/*.jar /app/app.jar

# Copy the custom JRE from the builder stage
COPY --from=builder /custom-jre /opt/custom-jre

# Copy the keystore file from the builder stage
COPY --from=builder /app/keystore.p12 /app/keystore.p12

# Copy the JaCoCo report from the builder stage
COPY --from=builder /app/build/reports/jacoco/test/jacocoTestReport.xml /app/build/reports/jacoco/test/jacocoTestReport.xml

# Set the PATH environment variable to use the custom JRE
ENV PATH="/opt/custom-jre/bin:$PATH"

# Expose the port on which the application will run
EXPOSE 8080

# Command to run the Spring Boot application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
