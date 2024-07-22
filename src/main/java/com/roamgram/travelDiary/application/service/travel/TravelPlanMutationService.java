package com.roamgram.travelDiary.application.service.travel;

import com.roamgram.travelDiary.application.events.EventPublisher;
import com.roamgram.travelDiary.application.events.travelplan.TravelPlanDeletionEvent;
import com.roamgram.travelDiary.application.service.travel.schedule.ScheduleMutationService;
import com.roamgram.travelDiary.common.permissions.aop.CheckAccess;
import com.roamgram.travelDiary.common.permissions.domain.Resource;
import com.roamgram.travelDiary.common.permissions.service.ResourceService;
import com.roamgram.travelDiary.domain.model.travel.Schedule;
import com.roamgram.travelDiary.domain.model.travel.TravelPlan;
import com.roamgram.travelDiary.presentation.dto.request.travel.TravelPlanUpsertRequestDTO;
import com.roamgram.travelDiary.repository.persistence.travel.TravelPlanRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class TravelPlanMutationService {
    private final TravelPlanRepository travelPlanRepository;
    private final ConversionService conversionService;
    private final ScheduleMutationService scheduleMutationService;
    private final ResourceService resourceService;
    private final EventPublisher eventPublisher;


    @Autowired
    public TravelPlanMutationService(TravelPlanRepository travelPlanRepository,
                                     ConversionService conversionService,
                                     ScheduleMutationService scheduleMutationService,
                                     ResourceService resourceService,
                                     EventPublisher eventPublisher) {
        this.travelPlanRepository = travelPlanRepository;
        this.conversionService = conversionService;
        this.scheduleMutationService = scheduleMutationService;
        this.resourceService = resourceService;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public UUID createPlan(TravelPlanUpsertRequestDTO request) {
        TravelPlan createdPlan = conversionService.convert(request, TravelPlan.class);
        assert createdPlan != null;
        TravelPlan travelPlan = travelPlanRepository.save(createdPlan);
        Resource resource = resourceService.createResource(travelPlan, "private");
        travelPlan.setResource(resource);
        travelPlanRepository.save(travelPlan);
        return travelPlan.getId();
    }

    @Transactional
    @CheckAccess(resourceType = TravelPlan.class, spelResourceId = "#travelPlanId", permission = "EDITOR")
    public void deleteTravelPlan(UUID travelPlanId) {
        TravelPlan travelPlan = travelPlanRepository.findById(travelPlanId)
                .orElseThrow(() -> new EntityNotFoundException("TravelPlan not found"));

        // Trigger deletion of related schedules
        for (Schedule schedule : travelPlan.getScheduleList()) {
            scheduleMutationService.deleteSchedule(travelPlanId, schedule.getId());
        }
        // First, delete permissions in a separate transaction
        eventPublisher.publishEvent(new TravelPlanDeletionEvent(List.of(travelPlanId)));
        // Finally, delete the travel plan itself
        travelPlanRepository.delete(travelPlan);
    }

    @Transactional
    public List<UUID> deleteMultipleTravelPlans(List<UUID> travelPlanIds) {
        for (UUID travelPlanId : travelPlanIds) {
            // Call the deleteTravelPlan method to ensure proper deletion
            deleteTravelPlan(travelPlanId);
        }
        return travelPlanIds;
    }

    @Transactional
    @CheckAccess(resourceType = TravelPlan.class, spelResourceId = "#request.uuid", permission = "EDITOR")
    public TravelPlan modifyPlanMetadata(TravelPlanUpsertRequestDTO request) {
        TravelPlan travelPlan = travelPlanRepository.findById(request.getUuid()).orElseThrow();
        updateNonNullFields(request, travelPlan);
        travelPlanRepository.save(travelPlan);
        return travelPlan;
    }

    @CheckAccess(resourceType = TravelPlan.class, spelResourceId = "#travelPlanId", permission = "EDITOR")
    public TravelPlan importPlan(UUID travelPlanId){
        TravelPlan travelPlan = travelPlanRepository.findById(travelPlanId).orElseThrow();
        TravelPlan importedTravelPlan = TravelPlan
                .builder()
                .name(travelPlan.getName())
                .travelEndDate(travelPlan.getTravelEndDate())
                .travelStartDate(travelPlan.getTravelStartDate())
                .isPublic(false)
                .build();

        scheduleMutationService.importSchedule(importedTravelPlan);
        travelPlanRepository.save(importedTravelPlan);
        // Create the Resource and assign it to the TravelPlan
        Resource resource = resourceService.createResource(importedTravelPlan ,"private");
        importedTravelPlan.setResource(resource);
        travelPlanRepository.save(importedTravelPlan);
        return importedTravelPlan;
    }

    private void updateNonNullFields(TravelPlanUpsertRequestDTO request, TravelPlan travelPlan) {

        if (request.getEndDate() != null) {
            travelPlan.setTravelEndDate(request.getEndDate());
        }
        if (request.getStartDate() != null) {
            travelPlan.setTravelStartDate(request.getStartDate());
        }
        if (request.getName() != null) {
            travelPlan.setName(request.getName());
        }
    }

}
