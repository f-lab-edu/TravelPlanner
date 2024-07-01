package com.example.travelDiary.presentation.converter.request.mediaFile;

import com.example.travelDiary.domain.model.review.MediaFile;
import com.example.travelDiary.domain.model.review.MediaFileStatus;
import com.example.travelDiary.presentation.dto.request.s3.PreSignedUploadInitiateRequest;
import org.springframework.core.convert.converter.Converter;

public class PreSignedUploadRequestToMediaFile implements Converter<PreSignedUploadInitiateRequest, MediaFile> {
    public MediaFile convert(PreSignedUploadInitiateRequest source) {
        MediaFile mediaFile = new MediaFile();
        if (source.getFileSize() != null) {
            mediaFile.setSizeBytes(source.getFileSize());
        }
        if (source.getOriginalFileName() != null) {
            mediaFile.setOriginalFileName(source.getOriginalFileName());
        }
        mediaFile.setMediaFileStatus(MediaFileStatus.PENDING);

        return mediaFile;
    }
}
