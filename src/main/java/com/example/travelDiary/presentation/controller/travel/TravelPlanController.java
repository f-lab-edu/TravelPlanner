package com.example.travelDiary.presentation.controller.travel;

import com.example.travelDiary.domain.model.travel.TravelPlan;
import com.example.travelDiary.application.service.travel.TravelPlanAccessService;
import com.example.travelDiary.presentation.dto.request.travel.TravelPlanUpsertRequestDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/travelPlan")
@Slf4j
public class TravelPlanController {
    private final RestTemplate restTemplate;
    private final TravelPlanAccessService planAccessService;

    @Autowired
    public TravelPlanController(RestTemplate restTemplate, TravelPlanAccessService planAccessService) {
        this.restTemplate = restTemplate;
        this.planAccessService = planAccessService;
    }

    @Tag(name = "secure")
    @GetMapping("/get_by_id")
    public TravelPlan getPlanWithId(@RequestParam(value="planId") UUID planId) {
        log.info("get by id METHOD TRIGGERED BY REACT");
        return planAccessService.getTravelPlan(planId);
    }

    @Tag(name = "secure")
    @GetMapping("/search_by_plan_name")
    public Page<TravelPlan> getPlansContainingName(@RequestParam(value="name") String name,
                                                   @RequestParam(value="pageNumber") Integer pageNumber,
                                                   @RequestParam(value="pageSize") int pageSize) {
        return planAccessService.getTravelPageContainingName(name, pageNumber, pageSize);
    }


    @Tag(name = "secure")
    @GetMapping("/get_all")
    public List<TravelPlan> getAll() {
        log.info("get all METHOD TRIGGERED BY REACT");
        return planAccessService.getAllTravelPlan();
    }

    @Tag(name = "secure")
    @PostMapping("/create_travel_plan")
    public UUID createNewTravelPlan(@RequestBody TravelPlanUpsertRequestDTO request) {
        log.info("create METHOD TRIGGERED BY REACT");
        return planAccessService.createPlan(request);
    }

    @Tag(name = "secure")
    @DeleteMapping("/delete_travel_plan")
    public List<UUID> deleteTravelPlan(@RequestBody List<UUID> request) {
        log.info("delete METHOD TRIGGERED BY REACT");
        return planAccessService.deletePlan(request);
    }

    @Tag(name = "secure")
    @PatchMapping("/modify_travel_plan")
    public TravelPlan modifyTravelPlanMetadata(@RequestBody TravelPlanUpsertRequestDTO request) {
        return planAccessService.modifyPlanMetadata(request);
    }

}
