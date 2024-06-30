package com.example.travelDiary.application.service.travel.review;

import com.example.travelDiary.presentation.dto.request.s3.PreSignedUploadInitiateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MediaFileControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testUploadFileSmall() {
        PreSignedUploadInitiateRequest request = new PreSignedUploadInitiateRequest();
        HttpEntity<PreSignedUploadInitiateRequest> entity = new HttpEntity<>(request);

        ResponseEntity<URL> response = restTemplate.exchange("/media-file/upload-file-small", HttpMethod.POST, entity, URL.class);
        assertNotNull(response.getBody());
    }

    @Test
    public void testGetFile() {
        String objectKey = "testKey";
        HttpEntity<String> entity = new HttpEntity<>(objectKey);

        ResponseEntity<URL> response = restTemplate.exchange("/media-file/get-file", HttpMethod.POST, entity, URL.class);
        assertNotNull(response.getBody());
    }

    @Test
    public void testDeleteFile() {
        String objectKey = "testKey";
        HttpEntity<String> entity = new HttpEntity<>(objectKey);

        ResponseEntity<DeleteObjectResponse> response = restTemplate.exchange("/media-file/delete-file", HttpMethod.POST, entity, DeleteObjectResponse.class);
        assertNotNull(response.getBody());
    }
}

