package com.example.travelDiary.application.service.travel;

import com.example.travelDiary.application.service.travel.schedule.ScheduleQueryService;
import com.example.travelDiary.common.permissions.aop.CheckAccess;
import com.example.travelDiary.common.permissions.aop.InjectResourceIds;
import com.example.travelDiary.common.permissions.domain.UserResourcePermissionTypes;
import com.example.travelDiary.domain.model.travel.TravelPlan;
import com.example.travelDiary.domain.model.wallet.aggregate.MonetaryEvent;
import com.example.travelDiary.repository.persistence.travel.TravelPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class TravelPlanQueryService {

    private final ScheduleQueryService scheduleQueryService;
    private final TravelPlanRepository travelPlanRepository;

    @Autowired
    public TravelPlanQueryService(ScheduleQueryService scheduleQueryService, TravelPlanRepository travelPlanRepository) {
        this.scheduleQueryService = scheduleQueryService;
        this.travelPlanRepository = travelPlanRepository;
    }

    @Transactional(readOnly = true)
    @CheckAccess(resourceType = TravelPlan.class, spelResourceId = "#planId", permission = "VIEW")
    public TravelPlan getTravelPlan(UUID planId) {
        return travelPlanRepository.findById(planId).orElseThrow();
    }

    @Transactional(readOnly = true)
    @InjectResourceIds(parameterName = "resourceIds", resourceType = "TravelPlan", permissionType = UserResourcePermissionTypes.VIEW)
    public List<TravelPlan> getAllTravelPlan(List<UUID> resourceIds) {
        List<TravelPlan>result = travelPlanRepository.findAllByResourceIds(resourceIds);
        return travelPlanRepository.findAll();
    }
    // Delegate the permission handling to PermissionService
    @Transactional(readOnly = true)
    @InjectResourceIds(parameterName = "resourceIds", resourceType = "TravelPlan", permissionType = UserResourcePermissionTypes.VIEW)
    public Page<TravelPlan> getTravelPageContainingName(String name, int pageNumber, int pageSize, List<UUID> resourceIds) {
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        // Fetch travel plans by resource IDs
        Page<TravelPlan> result = travelPlanRepository.findAllByNameContainingAndResourceIds(name, resourceIds, pageRequest);
        return result;
    }

    @CheckAccess(resourceType = TravelPlan.class, spelResourceId = "#travelPlanId", permission = "VIEW")
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

}
