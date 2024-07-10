package com.roamgram.travelDiary.application.events.eventListener;

import com.roamgram.travelDiary.application.events.review.ReviewCreatedEvent;
import com.roamgram.travelDiary.application.events.review.ReviewPreDeletedEvent;
import com.roamgram.travelDiary.application.service.travel.schedule.ScheduleMutationService;
import com.roamgram.travelDiary.common.permissions.domain.Resource;
import com.roamgram.travelDiary.common.permissions.service.ResourceService;
import com.roamgram.travelDiary.domain.model.review.Review;
import com.roamgram.travelDiary.repository.persistence.review.ReviewRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class ReviewEventListener {

    private final ScheduleMutationService scheduleMutationService;
    private final ResourceService resourceService;
    private final ReviewRepository reviewRepository;

    @Autowired
    public ReviewEventListener(ScheduleMutationService scheduleMutationService, ResourceService resourceService, ReviewRepository reviewRepository) {
        this.scheduleMutationService = scheduleMutationService;
        this.resourceService = resourceService;
        this.reviewRepository = reviewRepository;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleReviewCreatedEvent (ReviewCreatedEvent event) {
        Review review = event.getReview();
        UUID scheduleId = event.getScheduleId();
        Resource resource = resourceService.createResource(review, "private");
        scheduleMutationService.linkReview(scheduleId, review);
        review.setResource(resource);
        reviewRepository.save(review);
    }

    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleSchedulePreDeletedEvent(ReviewPreDeletedEvent event) {
        log.info("review prehandler is published");
        UUID scheduleId = event.getScheduleId();
        scheduleMutationService.removeReview(scheduleId);
        resourceService.delinkPermissions(List.of(event.getReviewId()));
    }


}
