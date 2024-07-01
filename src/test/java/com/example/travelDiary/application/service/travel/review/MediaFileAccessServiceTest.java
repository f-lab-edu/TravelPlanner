package com.example.travelDiary.application.service.travel.review;

import com.example.travelDiary.application.S3.S3Service;
import com.example.travelDiary.application.service.review.MediaFileAccessService;
import com.example.travelDiary.application.service.review.MediaFileRedisService;
import com.example.travelDiary.application.service.review.MediaFileService;
import com.example.travelDiary.common.auth.service.AuthUserServiceImpl;
import com.example.travelDiary.domain.model.review.MediaFile;
import com.example.travelDiary.domain.model.review.MediaFileStatus;
import com.example.travelDiary.domain.model.user.UserProfile;
import com.example.travelDiary.presentation.dto.request.s3.FinishUploadRequest;
import com.example.travelDiary.presentation.dto.request.s3.PreSignedUploadInitiateRequest;
import com.example.travelDiary.presentation.dto.request.s3.PresignedUrlAbortRequest;
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
import java.net.URL;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MediaFileAccessServiceTest {

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

    @InjectMocks
    private MediaFileAccessService mediaFileAccessService;

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
        when(authUserService.getCurrentUser()).thenReturn(user);

        URI uri = URI.create("http://example.com?fname=example.txt");
        when(conversionService.convert(any(PreSignedUploadInitiateRequest.class), eq(MediaFile.class)))
                .thenReturn(new MediaFile());
        when(s3Service.createPresignedUrlForPut(anyString(), anyString(), anyLong()))
                .thenReturn(uri.toURL());

        URL result = mediaFileAccessService.uploadMediaFile(request);

        assertNotNull(result);
        verify(redisService).cacheMediaFile(anyString(), any(MediaFile.class));
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

        DeleteObjectResponse result = mediaFileAccessService.deleteMediaFile("testKey");

        assertNotNull(result);
        verify(mediaFileService).deleteByS3Key(anyString());
    }

    @Test
    public void testSaveMediaFile_NewFile() {
        when(authUserService.getCurrentUser()).thenReturn(user);

        when(mediaFileService.findByOriginalFileNameAndContentType(any(), any())).thenReturn(Optional.empty());
        when(conversionService.convert(any(), eq(MediaFile.class))).thenReturn(new MediaFile());

        String key = mediaFileAccessService.saveMediaFile(request);

        assertNotNull(key);
        verify(redisService).cacheMediaFile(eq(key), any(MediaFile.class));
    }

    @Test
    public void testSaveMediaFile_ExistingFile() {
        when(authUserService.getCurrentUser()).thenReturn(user);

        MediaFile existingMediaFile = new MediaFile();
        existingMediaFile.setS3Key("existingKey");
        when(mediaFileService.findByOriginalFileNameAndContentType(any(), any())).thenReturn(Optional.of(existingMediaFile));
        when(conversionService.convert(any(), eq(MediaFile.class))).thenReturn(new MediaFile());

        String key = mediaFileAccessService.saveMediaFile(request);

        assertEquals("existingKey", key);
        verify(redisService).cacheMediaFile(eq(key), any(MediaFile.class));
    }

    @Test
    public void testUploadMediaFileMultipart() {
        when(authUserService.getCurrentUser()).thenReturn(user);

        when(mediaFileService.findByOriginalFileNameAndContentType(any(), any())).thenReturn(Optional.empty());
        when(conversionService.convert(any(), eq(MediaFile.class))).thenReturn(new MediaFile());
        URL url = mock(URL.class);
        when(s3Service.createMultipartUploadRequest(any(), any())).thenReturn(url);

        assertEquals(url, mediaFileAccessService.uploadMediaFileMultipart(request));
    }

    @Test
    public void testUploadMediaFileMultipartComplete() {
        FinishUploadRequest finishUploadRequest = new FinishUploadRequest();
        finishUploadRequest.setObjectKey("uploads/someUser/someName/text/1024/someId/someMd5");
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
        mediaFile.setMediaFileStatus(MediaFileStatus.PENDING);

        when(redisService.getMediaFile(objectKey)).thenReturn(mediaFile);

        mediaFileAccessService.markMediaUploadFinished(objectKey);

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