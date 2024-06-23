package com.example.travelDiary.application.service.travel;

import com.example.travelDiary.application.events.EventPublisher;
import com.example.travelDiary.application.events.resource.ResourceDeletionEvent;
import com.example.travelDiary.application.service.travel.schedule.ScheduleMutationService;
import com.example.travelDiary.common.permissions.aop.CheckAccess;
import com.example.travelDiary.common.permissions.domain.Resource;
import com.example.travelDiary.common.permissions.service.ResourceService;
import com.example.travelDiary.domain.model.travel.TravelPlan;
import com.example.travelDiary.repository.persistence.travel.TravelPlanRepository;
import com.example.travelDiary.presentation.dto.request.travel.TravelPlanUpsertRequestDTO;
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
    @CheckAccess(resourceType = TravelPlan.class, spelResourceId = "#request", permission = "EDIT", isList = true)
    public List<UUID> deletePlan(List<UUID> request) {
        // First, delete permissions in a separate transaction
        eventPublisher.publishEvent(new ResourceDeletionEvent(request));
        // Delete travel plans
        travelPlanRepository.deleteAllById(request);

        return request;
    }

    @Transactional
    @CheckAccess(resourceType = TravelPlan.class, spelResourceId = "#request.uuid", permission = "EDIT")
    public TravelPlan modifyPlanMetadata(TravelPlanUpsertRequestDTO request) {
        TravelPlan travelPlan = travelPlanRepository.findById(request.getUuid()).orElseThrow();
        updateNonNullFields(request, travelPlan);
        travelPlanRepository.save(travelPlan);
        return travelPlan;
    }

    @CheckAccess(resourceType = TravelPlan.class, spelResourceId = "#travelPlanId", permission = "EDIT")
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
