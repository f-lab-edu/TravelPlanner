package com.example.travelDiary.presentation.controller.travel;

import com.example.travelDiary.domain.model.travel.TravelPlan;
import com.example.travelDiary.application.service.travel.TravelPlanAccessService;
import com.example.travelDiary.presentation.dto.request.TravelPlanUpsertRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/travelPlan")
public class TravelPlanController {
    private final RestTemplate restTemplate;
    private final TravelPlanAccessService planAccessService;

    @Autowired
    public TravelPlanController(RestTemplate restTemplate, TravelPlanAccessService planAccessService) {
        this.restTemplate = restTemplate;
        this.planAccessService = planAccessService;
    }

    @GetMapping("/search_travel_plan")
    public TravelPlan getPlanWithId(@RequestParam(value="planId") UUID planId) {
        return planAccessService.getTravelPlan(planId);
    }

    @GetMapping("/search_by_plan_name")
    public Page<TravelPlan> getPlansContainingName(@RequestParam(value="name") String name,
                                                   @RequestParam(value="pageNumber") Integer pageNumber,
                                                   @RequestParam(value="pageSize") int pageSize) {
        return planAccessService.getTravelPageContainingName(name, pageNumber, pageSize);
    }

    @PostMapping("/create_travel_plan")
    public UUID createNewTravelPlan(@RequestBody TravelPlanUpsertRequestDTO request) {
        return planAccessService.createPlan(request);
    }

    @DeleteMapping("/delete_travel_plan")
    public List<UUID> deleteTravelPlan(@RequestBody List<UUID> request) {
        return planAccessService.deletePlan(request);
    }

    @PatchMapping("/modify_travel_plan")
    public TravelPlan modifyTravelPlanMetadata(@RequestBody TravelPlanUpsertRequestDTO request) {
        return planAccessService.modifyPlanMetadata(request);
    }

}
