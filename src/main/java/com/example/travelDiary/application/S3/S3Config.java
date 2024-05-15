package com.example.travelDiary.application.S3;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.endpoints.Endpoint;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.endpoints.S3EndpointParams;
import software.amazon.awssdk.services.s3.endpoints.S3EndpointProvider;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
@Slf4j
class S3Config {

    private final String region;
    private final AwsCredentials awsCredentials;
    private final AwsCredentialsProvider provider;

    public S3Config(@Value("${aws.credentials.access-key}")String accessKey,
                    @Value("${aws.credentials.secret-key}")String secretKey,
                    @Value("${aws.region.static}") String region) {
        this.region = region;
        this.awsCredentials = AwsBasicCredentials.create(accessKey, secretKey);
        this.provider = StaticCredentialsProvider.create(awsCredentials);
    }


    @Bean
    public S3Client amazonS3Client(){

//        S3EndpointParams endpointParams = S3EndpointParams.builder()
//                .region(Region.AP_SOUTHEAST_2)
//                .region(Region.of(region))
                // .endpoint(endpointURL)
                // path style access is done by default if the url is not a
                // virtual host IIRC, if not you can force path style with
                // .forcePathStyle(true) // I think you only need this once here or in the client
//                .build();

//        Endpoint endpoint = S3EndpointProvider
//                .defaultProvider()
//                .resolveEndpoint(endpointParams).join();

        return S3Client
                .builder()
                .credentialsProvider(provider)
                .region(Region.of(region))
//                .endpointOverride(endpoint.url())
                // path style access is done by default if the url is not a
                // virtual host IIRC, if not you can force path style with
                // .forcePathStyle(true) // I think you only need this once here or in the params
                .build();
    }

    @Bean
    public S3Presigner s3Presigner(){
        return S3Presigner.builder()
                .region(Region.AP_SOUTHEAST_2)
                .credentialsProvider(provider)
                .build();
    }

}
