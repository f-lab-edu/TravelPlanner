package com.example.travelDiary.application.service.travel;

import com.example.travelDiary.application.service.travel.schedule.ScheduleMutationService;
import com.example.travelDiary.application.service.travel.schedule.ScheduleQueryService;
import com.example.travelDiary.common.permissions.aop.CheckAccess;
import com.example.travelDiary.common.permissions.domain.Resource;
import com.example.travelDiary.common.permissions.domain.ResourcePermission;
import com.example.travelDiary.common.permissions.domain.UserResourcePermissionTypes;
import com.example.travelDiary.common.permissions.repository.ResourcePermissionRepository;
import com.example.travelDiary.common.permissions.repository.ResourceRepository;
import com.example.travelDiary.common.permissions.service.ResourcePermissionService;
import com.example.travelDiary.common.permissions.service.ResourceService;
import com.example.travelDiary.domain.model.travel.TravelPlan;
import com.example.travelDiary.domain.model.wallet.aggregate.MonetaryEvent;
import com.example.travelDiary.repository.persistence.travel.TravelPlanRepository;
import com.example.travelDiary.presentation.dto.request.travel.TravelPlanUpsertRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class TravelPlanAccessService {
    private final TravelPlanRepository travelPlanRepository;
    private final ConversionService conversionService;
    private final ScheduleQueryService scheduleQueryService;
    private final ScheduleMutationService scheduleMutationService;
    private final ResourceService resourceService;
    private final ResourcePermissionService resourcePermissionService;
    private final ResourceRepository resourceRepository;
    private final ResourcePermissionRepository resourcePermissionRepository;

    @Autowired
    public TravelPlanAccessService(TravelPlanRepository travelPlanRepository, ConversionService conversionService, ScheduleQueryService scheduleQueryService, ScheduleMutationService scheduleMutationService, ResourceService resourceService, ResourcePermissionService resourcePermissionService, ResourceRepository resourceRepository, ResourcePermissionRepository resourcePermissionRepository) {
        this.travelPlanRepository = travelPlanRepository;
        this.conversionService = conversionService;
        this.scheduleQueryService = scheduleQueryService;
        this.scheduleMutationService = scheduleMutationService;
        this.resourceService = resourceService;
        this.resourcePermissionService = resourcePermissionService;
        this.resourceRepository = resourceRepository;
        this.resourcePermissionRepository = resourcePermissionRepository;
    }

    @Transactional
    public Page<TravelPlan> getTravelPageContainingName(String name, int pageNumber, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        // Delegate the permission handling to PermissionService
        List<UUID> resourceIds = resourcePermissionService.getResourceIdsByUserPermission(UserResourcePermissionTypes.VIEW);
        // Fetch travel plans by resource IDs
        Page<TravelPlan> result = travelPlanRepository.findAllByNameContainingAndResourceIds(name, resourceIds, pageRequest);
        return result;
    }

    @Transactional
    @CheckAccess(resourceType = TravelPlan.class, resourceId = "#planId", permission = "VIEW")
    public TravelPlan getTravelPlan(UUID planId) {
        return travelPlanRepository.findById(planId).orElseThrow();
    }

    @Transactional
    public UUID createPlan(TravelPlanUpsertRequestDTO request) {
        TravelPlan createdPlan = conversionService.convert(request, TravelPlan.class);
        assert createdPlan != null;
        TravelPlan travelPlan = travelPlanRepository.save(createdPlan);
        Resource resource = resourceService.createResource(travelPlan, "private");
        travelPlanRepository.save(travelPlan);
        return travelPlan.getId();
    }

    @Transactional
    @CheckAccess(resourceType = TravelPlan.class, resourceId = "#request", permission = "EDIT", isList = true)
    public List<UUID> deletePlan(List<UUID> request) {
        // First, delete permissions in a separate transaction
        resourceService.delinkPermissions(request);
        // Delete travel plans
        travelPlanRepository.deleteAllById(request);

        return request;
    }

    @Transactional
    @CheckAccess(resourceType = TravelPlan.class, resourceId = "#request.uuid", permission = "EDIT")
    public TravelPlan modifyPlanMetadata(TravelPlanUpsertRequestDTO request) {
        TravelPlan travelPlan = travelPlanRepository.findById(request.getUuid()).orElseThrow();
        updateNonNullFields(request, travelPlan);
        travelPlanRepository.save(travelPlan);
        return travelPlan;
    }

    @CheckAccess(resourceType = TravelPlan.class, resourceId = "#travelPlanId", permission = "EDIT")
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

    @CheckAccess(resourceType = TravelPlan.class, resourceId = "#travelPlanId", permission = "VIEW")
//    @FilterResultsForUser(resourceType = MonetaryEvent.class, permission = "VIEW")
    public Page<MonetaryEvent> getAssociatedMonetaryEvent(UUID travelPlanId, PageRequest pageRequest) {
        List<MonetaryEvent> monetaryEvents = travelPlanRepository
                .findById(travelPlanId)
                .orElseThrow()
                .getScheduleList()
                .stream()
                .flatMap(schedule -> scheduleQueryService
                        .getAssociatedMonetaryEvent(
                                schedule
                                        .getId()
                        )
                        .stream()
                )
                .toList();

        return new PageImpl<>(monetaryEvents, pageRequest, monetaryEvents.size());

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

//    @FilterResultsForUser(resourceType = TravelPlan.class, permission = "VIEW")
    public List<TravelPlan> getAllTravelPlan() {
        return travelPlanRepository.findAll();
    }
}
