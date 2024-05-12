package com.example.travelDiary.presentation.controller;

import com.example.travelDiary.domain.model.travel.TravelPlan;
import com.example.travelDiary.application.service.TravelPlanAccessService;
import com.example.travelDiary.presentation.dto.request.TravelPlanCreateRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
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

    @GetMapping("")
    public TravelPlan getPlanWithId(@RequestParam UUID planId) {
        return planAccessService.getTravelPlanwithUUID(planId);
    }

    @GetMapping("/page")
    public Page<TravelPlan> getPlansContainingName(@RequestParam String name,
                                                   @RequestParam Integer pageNumber,
                                                   @RequestParam int pageSize) {
        return planAccessService.getTravelPageContainingName(name, pageNumber, pageSize);
    }

    @PostMapping("/createTravelPlan")
    public UUID createNewTravelPlan(@RequestBody TravelPlanCreateRequestDTO request) {
        return planAccessService.createPlan(request);
    }

    @DeleteMapping()

    @PatchMapping()

}
