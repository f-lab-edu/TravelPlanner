package com.roamgram.travelDiary.application.service.travel;

import com.roamgram.travelDiary.application.service.travel.schedule.ScheduleQueryService;
import com.roamgram.travelDiary.common.permissions.aop.CheckAccess;
import com.roamgram.travelDiary.common.permissions.aop.InjectAuthorisedResourceIds;
import com.roamgram.travelDiary.common.permissions.domain.UserResourcePermissionTypes;
import com.roamgram.travelDiary.domain.model.travel.TravelPlan;
import com.roamgram.travelDiary.domain.model.wallet.aggregate.MonetaryEvent;
import com.roamgram.travelDiary.repository.persistence.travel.TravelPlanRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
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
    @InjectAuthorisedResourceIds(parameterName = "resourceIds", resourceType = "TravelPlan", permissionType = UserResourcePermissionTypes.VIEW)
    public Page<TravelPlan> getAllAuthorisedTravelPlan(List<UUID> resourceIds, Pageable pageable) {
        log.info("resourceIds: {}", resourceIds);
        Page<TravelPlan> result = travelPlanRepository.findAllByResourceIds(resourceIds, pageable);
        log.info("travelPlanAuthorizedGet result : {}", result);
        return result;
    }
    // Delegate the permission handling to PermissionService
    @Transactional(readOnly = true)
    @InjectAuthorisedResourceIds(parameterName = "resourceIds", resourceType = "TravelPlan", permissionType = UserResourcePermissionTypes.VIEW)
    public Page<TravelPlan> getAuthorisedTravelPageContainingName(String name, int pageNumber, int pageSize, List<UUID> resourceIds) {
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        // Fetch travel plans by resource IDs
        Page<TravelPlan> result = travelPlanRepository.findAllByNameContainingAndResourceIds(name, resourceIds, pageRequest);
        return result;
    }


}
