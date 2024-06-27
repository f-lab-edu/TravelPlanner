package com.example.travelDiary.presentation.controller.review;

import com.example.travelDiary.application.service.review.MediaFileAccessService;
import com.example.travelDiary.presentation.dto.request.s3.FinishUploadRequest;
import com.example.travelDiary.presentation.dto.request.s3.PreSignedUploadInitiateRequest;
import com.example.travelDiary.presentation.dto.request.s3.PresignedUrlAbortRequest;
import com.example.travelDiary.presentation.dto.request.s3.lambda.LambdaUploadCompleteRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;

import java.net.URL;

@RestController
@RequestMapping("/media-file")
@Slf4j
public class MediaFileController {

    private final MediaFileAccessService mediaFileAccessService;

    @Autowired
    public MediaFileController(MediaFileAccessService mediaFileAccessService) {
        this.mediaFileAccessService = mediaFileAccessService;
    }

    @PostMapping("/upload-file-small")
    public ResponseEntity<URL> uploadFileSmall(@RequestBody PreSignedUploadInitiateRequest request) {
        URL url = mediaFileAccessService.uploadMediaFile(request);
        return ResponseEntity.ok(url);
    }

    @PostMapping("/get-file")
    public ResponseEntity<URL> getFile(@RequestBody String objectKey) {
        URL url = mediaFileAccessService.getMediaFile(objectKey);
        return ResponseEntity.ok(url);
    }

    @PostMapping("/upload-multipart")
    public ResponseEntity<URL> uploadFileMultipart(@RequestBody PreSignedUploadInitiateRequest request) {
        URL url = mediaFileAccessService.uploadMediaFileMultipart(request);
        return ResponseEntity.ok(url);
    }

    @PostMapping("/upload-complete-multipart")
    public ResponseEntity<String> completeUpload(@RequestBody FinishUploadRequest finishUploadRequest) {
        String responseMessage = mediaFileAccessService.uploadMediaFileMultipartComplete(finishUploadRequest);
        return ResponseEntity.ok(responseMessage);
    }

    @PostMapping("/abort-upload")
    public ResponseEntity<String> abortUpload(@RequestBody PresignedUrlAbortRequest request) {
        String responseMessage = mediaFileAccessService.abortUpload(request);
        return ResponseEntity.ok(responseMessage);
    }

    @PostMapping("/complete-upload")
    public ResponseEntity<String> completeUpload(@RequestBody LambdaUploadCompleteRequest request) {
        log.info("Received upload complete request: {}", request);
        mediaFileAccessService.markMediaUploadFinished(request.getObjectKey());
        return ResponseEntity.ok("Upload completed");
    }

    @PostMapping("/delete-file")
    public ResponseEntity<DeleteObjectResponse> deleteFile(@RequestBody String objectKey) {
        DeleteObjectResponse response = mediaFileAccessService.deleteMediaFile(objectKey);
        return ResponseEntity.ok(response);
    }
}
