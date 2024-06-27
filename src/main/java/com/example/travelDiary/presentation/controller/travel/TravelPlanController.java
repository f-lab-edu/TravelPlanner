package com.example.travelDiary.presentation.controller.travel;

import com.example.travelDiary.application.service.travel.TravelPlanQueryService;
import com.example.travelDiary.domain.model.travel.TravelPlan;
import com.example.travelDiary.application.service.travel.TravelPlanMutationService;
import com.example.travelDiary.presentation.dto.request.travel.TravelPlanUpsertRequestDTO;
import com.example.travelDiary.presentation.dto.response.travel.TravelPlanResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/travelPlan")
@Slf4j
public class TravelPlanController {
    private final RestTemplate restTemplate;
    private final TravelPlanMutationService planAccessService;
    private final TravelPlanQueryService planQueryService;
    private final ConversionService conversionService;

    @Autowired
    public TravelPlanController(RestTemplate restTemplate, TravelPlanMutationService planAccessService, TravelPlanQueryService planQueryService, ConversionService conversionService) {
        this.restTemplate = restTemplate;
        this.planAccessService = planAccessService;
        this.planQueryService = planQueryService;
        this.conversionService = conversionService;
    }

    @GetMapping("/get_by_id")
    public TravelPlanResponse getPlanWithId(@RequestParam(value="planId") UUID planId) {
        log.info("get by id METHOD TRIGGERED BY REACT");
        TravelPlan plan = planQueryService.getTravelPlan(planId);
        return conversionService.convert(plan, TravelPlanResponse.class);
    }

    @GetMapping("/search_by_plan_name")
    public Page<TravelPlanResponse> getPlansContainingName(@RequestParam(value="name") String name,
                                                   @RequestParam(value="pageNumber") Integer pageNumber,
                                                   @RequestParam(value="pageSize") int pageSize) {
        Page<TravelPlan> pages = planQueryService.getTravelPageContainingName(name, pageNumber, pageSize, null);
        return pages.map(p -> conversionService.convert(p, TravelPlanResponse.class));
    }


    @GetMapping("/get_all")
    public ResponseEntity<List<TravelPlanResponse>> getAll() {
        log.info("get all METHOD TRIGGERED BY REACT");
        List<TravelPlan> travelPlanList = planQueryService.getAllTravelPlan(null);
        List<TravelPlanResponse> responseList = travelPlanList.stream().map(travelPlan -> conversionService.convert(travelPlan, TravelPlanResponse.class)).toList();
        return ResponseEntity.ok(responseList);
    }

    @PostMapping("/create_travel_plan")
    public UUID createNewTravelPlan(@RequestBody TravelPlanUpsertRequestDTO request) {
        log.info("create METHOD TRIGGERED BY REACT");
        return planAccessService.createPlan(request);
    }

    @DeleteMapping("/delete_travel_plan")
    public List<UUID> deleteTravelPlan(@RequestBody List<UUID> request) {
        log.info("delete METHOD TRIGGERED BY REACT");
        return planAccessService.deletePlan(request);
    }

    @PatchMapping("/modify_travel_plan")
    public TravelPlanResponse modifyTravelPlanMetadata(@RequestBody TravelPlanUpsertRequestDTO request) {
        TravelPlan travelPlan = planAccessService.modifyPlanMetadata(request);
        return conversionService.convert(travelPlan, TravelPlanResponse.class);
    }

}
