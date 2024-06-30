//package com.example.travelDiary.application.service.travel.review;
//
//import com.example.travelDiary.application.S3.S3Service;
//import com.example.travelDiary.application.service.review.MediaFileAccessService;
//import com.example.travelDiary.domain.model.review.MediaFile;
//import com.example.travelDiary.domain.model.review.MediaFileStatus;
//import com.example.travelDiary.presentation.dto.request.s3.FinishUploadRequest;
//import com.example.travelDiary.presentation.dto.request.s3.PreSignedUploadInitiateRequest;
//import com.example.travelDiary.presentation.dto.request.s3.PresignedUrlAbortRequest;
//import com.example.travelDiary.repository.persistence.review.MediaFileRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.core.convert.ConversionService;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.core.ValueOperations;
//import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
//
//import java.net.MalformedURLException;
//import java.net.URI;
//import java.net.URL;
//import java.time.Duration;
//import java.util.Optional;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class MediaFileAccessServiceTest {
//
//    @Mock
//    private S3Service s3Service;
//    @Mock
//    private ConversionService conversionService;
//    @Mock
//    private MediaFileRepository mediaFileRepository;
//    @Mock
//    private RedisTemplate<String, Object> redisTemplate;
//    @Mock
//    private ValueOperations<String, Object> valueOperations;
//
//    @InjectMocks
//    private MediaFileAccessService mediaFileAccessService;
//
//    private PreSignedUploadInitiateRequest request;
//
//    @BeforeEach
//    public void setUp() {
//        request = new PreSignedUploadInitiateRequest();
//        request.setOriginalFileName("example.txt");
//        request.setFileType("text/plain");
//        request.setFileSize(1024L);
//        request.setUserId(UUID.randomUUID());
//        request.setReviewId(UUID.randomUUID());
//
//    }
//
//    @Test
//    public void testUploadMediaFile() throws MalformedURLException {
//        URI uri = URI.create("http://example.com?fname=example.txt");
//
//        when(conversionService.convert(any(PreSignedUploadInitiateRequest.class), eq(MediaFile.class)))
//                .thenReturn(new MediaFile());
//        when(s3Service.createPresignedUrlForPut(anyString(), anyString(), anyLong()))
//                .thenReturn(uri.toURL());
//        // Mock redisTemplate behavior
//        ValueOperations<String, Object> mockValueOps = Mockito.mock(ValueOperations.class);
//        when(redisTemplate.opsForValue()).thenReturn(mockValueOps);
//
//        URL result = mediaFileAccessService.uploadMediaFile(request);
//        assertNotNull(result);
//        // Verify that set method on ValueOperations was called with expected arguments
//        verify(mockValueOps).set(anyString(), any(MediaFile.class), any(Duration.class));
//    }
//
//    @Test
//    public void testGetMediaFile() throws MalformedURLException {
//        URI uri = URI.create("http://example.com");
//        when(s3Service.createPresignedUrlForGet(anyString())).thenReturn(uri.toURL());
//
//        URL result = mediaFileAccessService.getMediaFile("testKey");
//        assertNotNull(result);
//    }
//
//    @Test
//    public void testDeleteMediaFile() {
//        when(s3Service.deleteS3Object(anyString())).thenReturn(DeleteObjectResponse.builder().build());
//
//        DeleteObjectResponse result = mediaFileAccessService.deleteMediaFile("testKey");
//        assertNotNull(result);
//        verify(mediaFileRepository).deleteByS3Key(anyString());
//    }
//
//    @Test
//    void testSaveMediaFile_NewFile() {
//        when(mediaFileRepository.findByOriginalFileNameAndContentType(any(), any())).thenReturn(Optional.empty());
//        when(conversionService.convert(any(), eq(MediaFile.class))).thenReturn(new MediaFile());
//        String key = mediaFileAccessService.saveMediaFile(request);
//        assertNotNull(key);
//        verify(redisTemplate).opsForValue().set(eq(key), any(MediaFile.class), eq(Duration.ofMinutes(30)));
//    }
//
//    @Test
//    void testSaveMediaFile_ExistingFile() {
//        MediaFile existingMediaFile = new MediaFile();
//        existingMediaFile.setS3Key("existingKey");
//        when(mediaFileRepository.findByOriginalFileNameAndContentType(any(), any())).thenReturn(Optional.of(existingMediaFile));
//        when(conversionService.convert(any(), eq(MediaFile.class))).thenReturn(new MediaFile());
//        String key = mediaFileAccessService.saveMediaFile(request);
//        assertEquals("existingKey", key);
//        verify(redisTemplate).opsForValue().set(eq(key), any(MediaFile.class), eq(Duration.ofMinutes(30)));
//    }
//
//
//    @Test
//    void testUploadMediaFileMultipart() {
//        when(mediaFileRepository.findByOriginalFileNameAndContentType(any(), any())).thenReturn(Optional.empty());
//        when(conversionService.convert(any(), eq(MediaFile.class))).thenReturn(new MediaFile());
//        URL url = mock(URL.class);
//        when(s3Service.createMultipartUploadRequest(any(), any())).thenReturn(url);
//        assertEquals(url, mediaFileAccessService.uploadMediaFileMultipart(request));
//    }
//
//    @Test
//    void testUploadMediaFileMultipartComplete() {
//        FinishUploadRequest finishUploadRequest = new FinishUploadRequest();
//        finishUploadRequest.setObjectKey("someKey");
//        doNothing().when(s3Service).completeMultipartUpload(any(), any());
//        assertEquals("completed", mediaFileAccessService.uploadMediaFileMultipartComplete(finishUploadRequest));
//        verify(redisTemplate).delete(finishUploadRequest.getObjectKey());
//    }
//
//    @Test
//    void testAbortUpload() {
//        PresignedUrlAbortRequest abortRequest = new PresignedUrlAbortRequest();
//        abortRequest.setObjectKey("someKey");
//        doNothing().when(s3Service).abortUpload(any(), any());
//        assertEquals("aborted", mediaFileAccessService.abortUpload(abortRequest));
//        verify(redisTemplate).delete(abortRequest.getObjectKey());
//    }
//
//    @Test
//    void testMarkMediaUploadFinished() {
//        String objectKey = "someKey";
//        MediaFile mediaFile = new MediaFile();
//        mediaFile.setS3Key(objectKey);
//        mediaFile.setMediaFileStatus(MediaFileStatus.PENDING);
//        // Mock ValueOperations
//        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
//        when(redisTemplate.opsForValue().get(objectKey)).thenReturn(mediaFile);
//        mediaFileAccessService.markMediaUploadFinished(objectKey);
//        assertEquals(MediaFileStatus.UPLOADED, mediaFile.getMediaFileStatus());
//        verify(mediaFileRepository).save(mediaFile);
//        verify(redisTemplate).delete(objectKey);
//    }
//
//    @Test
//    void testGetUploadStatus_Cached() {
//        String key = "someKey";
//        MediaFile mediaFile = new MediaFile();
//        mediaFile.setMediaFileStatus(MediaFileStatus.UPLOADED);
//        when(redisTemplate.opsForValue().get(key)).thenReturn(mediaFile);
//        assertEquals(MediaFileStatus.UPLOADED, mediaFileAccessService.getUploadStatus(key));
//    }
//
//    @Test
//    void testGetUploadStatus_NotCached() {
//        String key = "someKey";
//        MediaFile mediaFile = new MediaFile();
//        mediaFile.setMediaFileStatus(MediaFileStatus.UPLOADED);
//        when(redisTemplate.opsForValue().get(key)).thenReturn(null);
//        when(mediaFileRepository.findByS3Key(key)).thenReturn(mediaFile);
//        assertEquals(MediaFileStatus.UPLOADED, mediaFileAccessService.getUploadStatus(key));
//    }
//
//    @Test
//    void testGetUploadStatus_Failed() {
//        String key = "someKey";
//        when(redisTemplate.opsForValue().get(key)).thenReturn(null);
//        when(mediaFileRepository.findByS3Key(key)).thenReturn(null);
//        assertEquals(MediaFileStatus.FAILED, mediaFileAccessService.getUploadStatus(key));
//    }
//}
