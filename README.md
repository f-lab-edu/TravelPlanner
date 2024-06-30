~~# Roamgram: A Travel Diary Backend

Roamgram is a sophisticated backend service for a travel diary application, enabling users to log, manage, and share their travel experiences. Built using Java Spring Boot, Roamgram leverages a wide array of technologies to ensure security, scalability, and performance.

## Features

- **OAuth2 Authentication**: Secure authentication using Google OAuth2.
- **JWT Security**: Robust security with JWT tokens.
- **Data Persistence**: Uses Spring Data JPA with PostgreSQL.
- **Caching**: Enhanced performance with Redis caching.
- **Automated Testing**: Comprehensive testing with JUnit5, Mockito, and Jacoco.
- **Continuous Integration and Deployment**: GitHub Actions for CI/CD.
- **Cloud Services**: AWS S3 for storage, AWS EC2 for deployment, and AWS Lambda for serverless functions.
- **Monitoring**: Prometheus for metrics collection and Loki for log aggregation.
- **API Documentation**: Interactive API documentation with Swagger.

## Prerequisites

- Docker (Docker Desktop or CLI)
- Java 21
- Gradle 6.8+
- AWS Account with lambda /  S3 enabled

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/roamgram.git
cd roamgram
```

## create the .env file for docker-compose to use

```dotenv
AWS_EC2_URI=CURRENTLY_NOT_AVAILABLE(EC2_INSTANCE_IS_PAUSED_TO_SAVE_COSTS)
AWS_REGION=CURRENTLY_NOT_AVAILABLE(EC2_INSTANCE_IS_PAUSED_TO_SAVE_COSTS)
AWS_S3_ACCESS_KEY=YOUR_AWS_S3_ACCESS_KEY
AWS_S3_BUCKET=YOUR_AWS_S3_BUCKET
AWS_S3_SECRET_KEY=YOUR_AWS_S3_SECRET_KEY
DOCKER_USERNAME=YOUR_DOCKER_USERNAME
GOOGLE_CLIENT_ID=YOUR_GOOGLE_CLIENT_ID
GOOGLE_CLIENT_SECRET=YOUR_GOOGLE_CLIENT_SECRET
JWT_KEY=YOUR_JWT_SOURCE_KEY
REDIS_HOST=redis
REDIS_PORT=6379
POSTGRES_USERNAME=YOUR_POSTGRES_USERNAME
POSTGRES_PASSWORD=YOUR_POSTGRES_PASSWORD
POSTGRES_URL=jdbc:postgresql://postgres:5432/roamgram
POSTGRES_DB_NAME=roamgram
```

## build and run with docker-compose

### Ensure Docker is installed and running. Then execute:

```bash
docker compose up -d
```

- or if you are using the old docker-compose

```bash
docker-compose up -d
```


## Access the Application
   The application should be accessible at http://localhost:8080.

## Explore API Documentation
   API documentation is available via Swagger at http://localhost:8080/swagger-ui/index.html.


## Deployment
Roamgram is set up with GitHub Actions for continuous integration and deployment, only up to the creation of a docker image.
The workflow is defined in .github/workflows/docker-image.yml.
Ensure your repository secrets are configured in GitHub for seamless deployments.

## Why GitHub Actions?
Jenkins was initially considered for CI/CD.
However, due to the cost-saving measure of not running the Amazon EC2 server 24/7, Jenkins was deemed overkill. 
GitHub Actions was chosen as a more cost-effective and efficient solution for our currently small - medium sized solution.

## Monitoring and Logging (to be implemented)
Prometheus: For metrics collection.
Loki: For log aggregation.
These tools help monitor the application's health and performance, 
providing valuable insights for maintaining and improving the service.

## Technology Stack
- Java Spring Boot: Core framework.
- Spring OAuth2: For easy authentication/authorization via social(google) login and future integration with google calendars.
- Spring Security: To secure the application.
- JWT: For token-based security.
- Spring Data JPA: For ORM with Hibernate.
- Redis: For caching and managing tokens.
- PostgreSQL: As the primary database.
- H2DB: For in-memory database during testing.
- JUnit5: For unit testing.
- Mockito: For mocking during tests.
- Jacoco: For test coverage reporting.
- Lombok: To reduce boilerplate code.
- Flyway: For database migrations.
- Slf4j/Logback: For logging(internally).
- Docker: To containerize the application.
- Docker Compose: For orchestrating multi-container applications for one-step deployments.
- AWS S3: For object storage of media-related files within reviews.
- AWS EC2: For deployment.
- AWS Lambda: For serverless functions primarily for S3 upload reporting.
- GitHub Actions: For CI/CD.

## <--- Future Enhancements( to be implemented ) --->
- Prometheus: For monitoring.
- Loki: For log aggregation.
- Grafana: For enhanced monitoring and visualization.

## Contributing
We welcome contributions! Please see our CONTRIBUTING.md for more details.

## License
This project is licensed under the MIT License. See the LICENSE file for details.

## Contact
For any inquiries or issues, please reach out to us at incongnito12@gmail.com.~~
