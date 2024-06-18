package com.example.travelDiary.presentation.controller.travel;

import com.example.travelDiary.domain.model.travel.TravelPlan;
import com.example.travelDiary.application.service.travel.TravelPlanAccessService;
import com.example.travelDiary.presentation.dto.request.travel.TravelPlanUpsertRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/get_by_id")
    public TravelPlan getPlanWithId(@RequestParam(value="planId") UUID planId) {
        log.info("get by id METHOD TRIGGERED BY REACT");
        return planAccessService.getTravelPlan(planId);
    }

    @GetMapping("/search_by_plan_name")
    public Page<TravelPlan> getPlansContainingName(@RequestParam(value="name") String name,
                                                   @RequestParam(value="pageNumber") Integer pageNumber,
                                                   @RequestParam(value="pageSize") int pageSize) {
        return planAccessService.getTravelPageContainingName(name, pageNumber, pageSize);
    }


    @GetMapping("/get_all")
    public ResponseEntity<List<TravelPlan>> getAll() {
        log.info("get all METHOD TRIGGERED BY REACT");
        List<TravelPlan> travelPlanList = planAccessService.getAllTravelPlan();
        return ResponseEntity.ok(travelPlanList);
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
    public TravelPlan modifyTravelPlanMetadata(@RequestBody TravelPlanUpsertRequestDTO request) {
        return planAccessService.modifyPlanMetadata(request);
    }

}
