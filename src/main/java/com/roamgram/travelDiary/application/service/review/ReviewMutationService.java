package com.roamgram.travelDiary.application.service.review;

import com.roamgram.travelDiary.application.events.EventPublisher;
import com.roamgram.travelDiary.application.events.review.ReviewCreatedEvent;
import com.roamgram.travelDiary.application.events.review.ReviewPreDeletedEvent;
import com.roamgram.travelDiary.common.permissions.aop.CheckAccess;
import com.roamgram.travelDiary.domain.model.review.MediaFile;
import com.roamgram.travelDiary.domain.model.review.MediaFileStatus;
import com.roamgram.travelDiary.domain.model.review.Review;
import com.roamgram.travelDiary.domain.model.travel.Schedule;
import com.roamgram.travelDiary.presentation.dto.request.review.ReviewEditAppendRequest;
import com.roamgram.travelDiary.presentation.dto.request.review.ReviewEditRemoveRequest;
import com.roamgram.travelDiary.presentation.dto.request.review.ReviewUploadRequest;
import com.roamgram.travelDiary.presentation.dto.response.review.ReviewResponse;
import com.roamgram.travelDiary.presentation.dto.response.review.ReviewUploadResponse;
import com.roamgram.travelDiary.repository.persistence.review.MediaFileRepository;
import com.roamgram.travelDiary.repository.persistence.review.ReviewRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j
public class ReviewMutationService {

    private final ReviewRepository reviewRepository;
    private final ConversionService conversionService;
    private final EventPublisher eventPublisher;
    private final MediaFileFacadeService mediaFileAccessService;
    private final MediaFileRepository mediaFileRepository;

    @Autowired
    public ReviewMutationService(ReviewRepository reviewRepository,
                                 ConversionService conversionService,
                                 EventPublisher eventPublisher,
                                 MediaFileFacadeService mediaFileAccessService, MediaFileRepository mediaFileRepository) {
        this.reviewRepository = reviewRepository;
        this.conversionService = conversionService;
        this.eventPublisher = eventPublisher;
        this.mediaFileAccessService = mediaFileAccessService;
        this.mediaFileRepository = mediaFileRepository;
    }

    @Transactional
    @CheckAccess(resourceType = Review.class, spelResourceId = "#reviewEditAppendRequest.reviewId", permission = "EDIT")
    public Review editReviewAppendFiles(ReviewEditAppendRequest reviewEditAppendRequest) {
        UUID reviewId = reviewEditAppendRequest.getReviewId();
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));

        updateNonNullReviewMetaData(reviewEditAppendRequest, review);

        if (reviewEditAppendRequest.getFileLocation() == null) {
            return reviewRepository.save(review);
        }

        if (review.getContentLocation() == null) {
            review.setContentLocation(new HashMap<>());
        }
        if (review.getFileList() == null) {
            review.setFileList(new ArrayList<>());
        }

        for (Map.Entry<String, Long> entry : reviewEditAppendRequest.getFileLocation().entrySet()) {
            String mediaFileId = entry.getKey();
            Long fileLocation = entry.getValue();

            if (!review.getContentLocation().containsKey(mediaFileId)) {
                MediaFile mediaFile = mediaFileRepository.findByS3Key(mediaFileId);
                if (mediaFile == null) {
                    throw new IllegalArgumentException("Addition of Media file that has not been saved");
                }
                review.getFileList().add(mediaFile);
            }
            review.getContentLocation().put(mediaFileId, fileLocation);
        }

        return reviewRepository.save(review);
    }

    @Transactional
    @CheckAccess(resourceType = Review.class, spelResourceId = "#reviewEditRemoveRequest.reviewId", permission = "EDIT")
    public Review editReviewRemoveFiles(ReviewEditRemoveRequest reviewEditRemoveRequest) {
        UUID reviewId = reviewEditRemoveRequest.getReviewId();
        Review reviewToUpdate = reviewRepository.findById(reviewId).orElseThrow();
        if(reviewToUpdate.getFileList() == null) {
            return reviewRepository.save(reviewToUpdate);
        }
        for(String mediaFileKey : reviewEditRemoveRequest.getFileList()) {
            MediaFile mediaFile = mediaFileRepository.findByS3Key(mediaFileKey);
            reviewToUpdate.getFileList().remove(mediaFile);
            if(mediaFile == null) {
                throw new IllegalArgumentException("Removal of Media file that has not been saved");
            }
            mediaFileAccessService.deleteMediaFile(mediaFileKey);
            reviewToUpdate.getContentLocation().remove(mediaFileKey);
        }
        return reviewRepository.save(reviewToUpdate);
    }

    @Transactional
    @CheckAccess(resourceType = Schedule.class, spelResourceId = "#scheduleId", permission = "EDIT")
    @CheckAccess(resourceType = Review.class, spelResourceId = "#reviewId", permission = "EDIT")
    public UUID deleteReview(UUID scheduleId, UUID reviewId) {
        eventPublisher.publishEvent(new ReviewPreDeletedEvent(scheduleId, reviewId));
        reviewRepository.deleteById(reviewId);
        return reviewId;
    }

    @Transactional
    @CheckAccess(resourceType = Schedule.class, spelResourceId = "#scheduleId", permission = "EDIT")
    public ReviewUploadResponse uploadReview(UUID scheduleId, ReviewUploadRequest reviewUploadRequest) {
        List<MediaFile> uploadedFiles = new ArrayList<>();
        List<MediaFile> pendingOrFailedFiles = new ArrayList<>();
        ReviewUploadResponse uploadResponse = new ReviewUploadResponse();
        if (reviewUploadRequest.getFileLocation() != null && reviewUploadRequest.getFileList() != null) {
            if (reviewUploadRequest.getFileLocation().size() != reviewUploadRequest.getFileList().size()) {
                log.info("size of fileLocation is {}, size of fileList is {}",
                        reviewUploadRequest.getFileLocation().size(),
                        reviewUploadRequest.getFileList().size());
                throw new IllegalStateException("non-matching file location and file list sizes");
            }

            for (MediaFile file : reviewUploadRequest.getFileList()) {
                MediaFileStatus status = mediaFileAccessService.getUploadStatus(file.getS3Key());
                if (status == MediaFileStatus.UPLOADED) {
                    uploadedFiles.add(file);
                } else {
                    pendingOrFailedFiles.add(file);
                }
            }
        } else if (reviewUploadRequest.getFileLocation() != null || reviewUploadRequest.getFileList() != null) {
            log.info("fileLocation is {}, fileList is {}", reviewUploadRequest.getFileLocation(), reviewUploadRequest.getFileList());
            throw new IllegalStateException("non-matching file location and file list sizes");
        } else {
            log.info("both null fileLocation and fileList");
        }

        Review review = conversionService.convert(reviewUploadRequest, Review.class);
        assert review != null;
        review.setScheduleId(scheduleId);
        review.setFileList(uploadedFiles);
        review = reviewRepository.save(review);

        uploadResponse.setReview(conversionService.convert(review, ReviewResponse.class));
        uploadResponse.setPendingOrFailedFiles(pendingOrFailedFiles);

        eventPublisher.publishEvent(new ReviewCreatedEvent(scheduleId, review));
        return uploadResponse;
    }

    private static void updateNonNullReviewMetaData(ReviewEditAppendRequest reviewEditAppendRequest, Review review) {
        if (reviewEditAppendRequest.getRating() != null) {
            review.setRating(reviewEditAppendRequest.getRating());
        }
        if (reviewEditAppendRequest.getUserDescription() != null) {
            review.setUserDescription(reviewEditAppendRequest.getUserDescription());
        }
    }
}
