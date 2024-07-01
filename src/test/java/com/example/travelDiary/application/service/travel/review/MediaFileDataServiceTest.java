package com.example.travelDiary.application.service.travel.review;

import com.example.travelDiary.application.S3.S3Service;
import com.example.travelDiary.application.service.review.MediaFileDataService;
import com.example.travelDiary.application.service.review.MediaFileRedisService;
import com.example.travelDiary.application.service.review.MediaFileService;
import com.example.travelDiary.common.auth.service.AuthUserService;
import com.example.travelDiary.domain.model.review.MediaFile;
import com.example.travelDiary.domain.model.user.UserProfile;
import com.example.travelDiary.presentation.dto.request.s3.PreSignedUploadInitiateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.ConversionService;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MediaFileDataServiceTest {

    @Mock
    private S3Service s3Service;
    @Mock
    private ConversionService conversionService;
    @Mock
    private MediaFileService mediaFileService;
    @Mock
    private MediaFileRedisService redisService;
    @Mock
    private AuthUserService authUserService;
    @InjectMocks
    private MediaFileDataService mediaFileDataService;

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
    public void testSaveMediaFile_NewFile() {
        when(authUserService.getCurrentUser()).thenReturn(user);

        when(mediaFileService.findByOriginalFileNameAndContentType(any(), any())).thenReturn(Optional.empty());
        when(conversionService.convert(any(), eq(MediaFile.class))).thenReturn(new MediaFile());

        String key = mediaFileDataService.saveMediaFile(request);

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

        String key = mediaFileDataService.saveMediaFile(request);

        assertEquals("existingKey", key);
    }


    @Test
    public void testSaveMediaFile_NullUser() {
        when(authUserService.getCurrentUser()).thenReturn(null);

        MediaFile existingMediaFile = new MediaFile();
        existingMediaFile.setS3Key("existingKey");
        when(mediaFileService.findByOriginalFileNameAndContentType(any(), any())).thenReturn(Optional.of(existingMediaFile));

        assertThrows(NullPointerException.class, () -> mediaFileDataService.saveMediaFile(request));
    }

    @Test
    public void testSaveMediaFile_ConversionFailure() {
        when(authUserService.getCurrentUser()).thenReturn(user);

        when(mediaFileService.findByOriginalFileNameAndContentType(any(), any())).thenReturn(Optional.empty());
        when(conversionService.convert(any(), eq(MediaFile.class))).thenReturn(null);  // Simulate conversion failure

        assertThrows(AssertionError.class, () -> mediaFileDataService.saveMediaFile(request));
    }
}
