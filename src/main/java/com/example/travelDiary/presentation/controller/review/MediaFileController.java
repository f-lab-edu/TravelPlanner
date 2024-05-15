package com.example.travelDiary.presentation.controller.review;

import com.example.travelDiary.application.service.review.MediaFileAccessService;
import com.example.travelDiary.presentation.dto.request.s3.FinishUploadRequest;
import com.example.travelDiary.presentation.dto.request.s3.PreSignedUploadInitiateRequest;
import com.example.travelDiary.presentation.dto.request.s3.PresignedUrlAbortRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URL;

@RestController
@RequestMapping("/media-file")
public class MediaFileController {

    private final MediaFileAccessService mediaFileAccessService;

    @Autowired
    public MediaFileController(MediaFileAccessService mediaFileAccessService) {
        this.mediaFileAccessService = mediaFileAccessService;
    }

    @PostMapping("/upload-file-small")
    public URL uploadFileSmall(@RequestBody PreSignedUploadInitiateRequest request) {
        return mediaFileAccessService.uploadMediaFile(request);
    }

    @PostMapping("/get-file")
    public URL getFile(@RequestBody String objectKey) {
        return mediaFileAccessService.getMediaFile(objectKey);
    }

    @PostMapping("/delete-file")
    public URL uploadFileSmall(@RequestBody String objectKey) {
        return mediaFileAccessService.deleteMediaFile(objectKey);
    }


    @PostMapping("/upload-multipart")
    public URL uploadFileMultipart(
            @RequestBody PreSignedUploadInitiateRequest request) {
        return mediaFileAccessService.uploadMediaFileMultipart(request);
    }

    @PostMapping("/upload-complete-multipart")
    public ResponseEntity<String> completeUpload(@RequestBody FinishUploadRequest finishUploadRequest) {
        String responseMessage = mediaFileAccessService.uploadMediaFileMultipartComplete(finishUploadRequest);
        return ResponseEntity.ok(responseMessage);
    }

    @PostMapping("/abort-upload")
    public ResponseEntity<String> abortUpload (@RequestBody PresignedUrlAbortRequest request) {
        String responseMessage = mediaFileAccessService.abortUpload(request);
        return ResponseEntity.ok(responseMessage);
    }



}
