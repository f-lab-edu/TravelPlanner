package com.example.travelDiary.application.S3;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.endpoints.Endpoint;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.endpoints.S3EndpointParams;
import software.amazon.awssdk.services.s3.endpoints.S3EndpointProvider;

@Configuration
@Slf4j
class S3Config {
    @Value("${aws.s3.access-key}")
    private String accessKey;

    @Value("${aws.s3.secret-key}")
    private String secretKey;

    @Value("${aws.s3.region.static}")
    private String region;

    @Bean
    public S3Client amazonS3Client(){
        AwsCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        AwsCredentialsProvider provider = StaticCredentialsProvider.create(credentials);

        S3EndpointParams endpointParams = S3EndpointParams.builder()
                .region(Region.of(region))
//                .endpoint(endpointURL)
                // path style access is done by default if the url is not a
                // virtual host IIRC, if not you can force path style with
                // .forcePathStyle(true) // I think you only need this once here or in the client
                .build();

        Endpoint endpoint = S3EndpointProvider
                .defaultProvider()
                .resolveEndpoint(endpointParams).join();

        S3Client s3Client = S3Client
                .builder()
                .credentialsProvider(provider)
                .endpointOverride(endpoint.url())
                // path style access is done by default if the url is not a
                // virtual host IIRC, if not you can force path style with
                // .forcePathStyle(true) // I think you only need this once here or in the params
                .build();
        return s3Client;
    }

}
