package com.example.travelDiary.application.service.travel.review;

import com.example.travelDiary.application.S3.S3Service;
import com.example.travelDiary.application.service.review.MediaFileDataService;
import com.example.travelDiary.application.service.review.MediaFileFacadeService;
import com.example.travelDiary.application.service.review.MediaFileRedisService;
import com.example.travelDiary.application.service.review.MediaFileService;
import com.example.travelDiary.common.auth.service.AuthUserServiceImpl;
import com.example.travelDiary.domain.model.review.MediaFile;
import com.example.travelDiary.domain.model.review.MediaFileStatus;
import com.example.travelDiary.domain.model.user.UserProfile;
import com.example.travelDiary.presentation.dto.request.s3.FinishUploadRequest;
import com.example.travelDiary.presentation.dto.request.s3.PreSignedUploadInitiateRequest;
import com.example.travelDiary.presentation.dto.request.s3.PresignedUrlAbortRequest;
import com.example.travelDiary.presentation.dto.request.s3.lambda.LambdaUploadCompleteRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.ConversionService;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MediaFileFacadeServiceTest {

    @Mock
    private S3Service s3Service;
    @Mock
    private ConversionService conversionService;
    @Mock
    private MediaFileService mediaFileService;
    @Mock
    private MediaFileRedisService redisService;
    @Mock
    private AuthUserServiceImpl authUserService;

    @Mock
    private MediaFileDataService mediaFileDataService;

    @InjectMocks
    private MediaFileFacadeService mediaFileAccessService;

    private PreSignedUploadInitiateRequest request;
    private UserProfile user;

    @BeforeEach
    public void setUp() {
        request = new PreSignedUploadInitiateRequest();
        request.setOriginalFileName("example.txt");
        request.setFileSize(1024L);
        request.setScheduleId(UUID.randomUUID());

        user = new UserProfile();
        user.setId(UUID.randomUUID());

    }

    @Test
    public void testUploadMediaFile() throws MalformedURLException {
        URI uri = URI.create("http://example.com?fname=example.txt");
        when(s3Service.createPresignedUrlForPut(anyString(), anyString(), anyLong()))
                .thenReturn(uri.toURL());
        when(mediaFileDataService.saveMediaFile(any(PreSignedUploadInitiateRequest.class)))
                .thenReturn("testKey");

        URL result = mediaFileAccessService.uploadMediaFile(request);
        assertNotNull(result);
    }

    @Test
    public void testGetMediaFile() throws MalformedURLException {
        URI uri = URI.create("http://example.com");
        when(s3Service.createPresignedUrlForGet(anyString())).thenReturn(uri.toURL());

        URL result = mediaFileAccessService.getMediaFile("testKey");

        assertNotNull(result);
    }

    @Test
    public void testDeleteMediaFile() {
        when(s3Service.deleteS3Object(anyString())).thenReturn(DeleteObjectResponse.builder().build());
        doNothing().when(mediaFileDataService).deleteMediaMetadata(anyString());

        DeleteObjectResponse result = mediaFileAccessService.deleteMediaFile("testKey");

        assertNotNull(result);
        verify(mediaFileDataService).deleteMediaMetadata(anyString());
    }


    @Test
    public void testUploadMediaFileMultipart() throws URISyntaxException, MalformedURLException {
        URI uri = new URI("http://example.com");  // Use a real URL instead of a mock to avoid issues
        URL url = uri.toURL();
        when(s3Service.createMultipartUploadRequest(any(), any())).thenReturn(url);
        when(mediaFileDataService.saveMediaFile(any(PreSignedUploadInitiateRequest.class)))
                .thenReturn("testKey");

        URL result = mediaFileAccessService.uploadMediaFileMultipart(request);
        assertEquals(url, result);
    }

    @Test
    public void testUploadMediaFileMultipartComplete() {
        FinishUploadRequest finishUploadRequest = new FinishUploadRequest();
        finishUploadRequest.setObjectKey("uploads/someUser/someName/text/1024/someId/someMd5");
        finishUploadRequest.setFileSize(1024L);
        when(s3Service.completeMultipartUpload(any(), any())).thenReturn(null);

        assertEquals("completed", mediaFileAccessService.uploadMediaFileMultipartComplete(finishUploadRequest));
        verify(mediaFileService).saveMediaFile(any(MediaFile.class));
    }

    @Test
    public void testAbortUpload() {
        PresignedUrlAbortRequest abortRequest = new PresignedUrlAbortRequest();
        abortRequest.setObjectKey("someKey");
        doNothing().when(s3Service).abortUpload(any(), any());

        assertEquals("aborted", mediaFileAccessService.abortUpload(abortRequest));
        verify(redisService).deleteMediaFile(abortRequest.getObjectKey());
    }

    @Test
    public void testMarkMediaUploadFinished() {
        String objectKey = "someKey";
        MediaFile mediaFile = new MediaFile();
        mediaFile.setS3Key(objectKey);
        mediaFile.setSizeBytes(1024L);
        mediaFile.setMediaFileStatus(MediaFileStatus.PENDING);

        when(redisService.getMediaFile(objectKey)).thenReturn(mediaFile);
        LambdaUploadCompleteRequest request = new LambdaUploadCompleteRequest();
        request.setObjectKey(objectKey);
        request.setSize("1024");
        mediaFileAccessService.markMediaUploadFinished(request);

        assertEquals(MediaFileStatus.UPLOADED, mediaFile.getMediaFileStatus());
        verify(mediaFileService).saveMediaFile(mediaFile);
        verify(redisService).deleteMediaFile(objectKey);
    }

    @Test
    public void testGetUploadStatus_Cached() {
        String key = "someKey";
        MediaFile mediaFile = new MediaFile();
        mediaFile.setMediaFileStatus(MediaFileStatus.UPLOADED);
        when(redisService.getMediaFile(key)).thenReturn(mediaFile);

        assertEquals(MediaFileStatus.UPLOADED, mediaFileAccessService.getUploadStatus(key));
    }

    @Test
    public void testGetUploadStatus_NotCached() {
        String key = "someKey";
        MediaFile mediaFile = new MediaFile();
        mediaFile.setMediaFileStatus(MediaFileStatus.UPLOADED);
        when(redisService.getMediaFile(key)).thenReturn(null);
        when(mediaFileService.findByS3Key(key)).thenReturn(mediaFile);

        assertEquals(MediaFileStatus.UPLOADED, mediaFileAccessService.getUploadStatus(key));
    }

    @Test
    public void testGetUploadStatus_Failed() {
        String key = "someKey";
        when(redisService.getMediaFile(key)).thenReturn(null);
        when(mediaFileService.findByS3Key(key)).thenReturn(null);

        assertEquals(MediaFileStatus.FAILED, mediaFileAccessService.getUploadStatus(key));
    }
}