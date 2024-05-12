package com.example.travelDiary.presentation.controller;

import com.example.travelDiary.application.service.ScheduleAccessService;
import com.example.travelDiary.domain.model.travel.Schedule;
import com.example.travelDiary.presentation.dto.request.ScheduleUpsertRequest;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RequestMapping("/travelPlan/{travelPlanId}/schedule")
public class ScheduleController {
    private final ScheduleAccessService scheduleAccessService;

    public ScheduleController(ScheduleAccessService scheduleAccessService) {
        this.scheduleAccessService = scheduleAccessService;
    }

    @PostMapping
    public Schedule createSchedule(@PathVariable UUID travelPlanId, @RequestBody ScheduleUpsertRequest request) {
        scheduleAccessService.createSchedule(travelPlanId, request);
    }

    @PatchMapping
    public Schedule modifySchedule(@PathVariable UUID travelPlanId, @RequestBody ScheduleUpsertRequest request) {

    }

    @DeleteMapping
    public UUID deleteSchedule(@PathVariable UUID travelPlanId, @RequestParam UUID scheduleId) {

    }

    @GetMapping
    public Page<Schedule> getScheduleWithName(@PathVariable UUID travelPlanId, @RequestParam String name) {

    }

    @GetMapping
    public Page<Schedule> getScheduleOnDay(@PathVariable UUID travelPlanId, @RequestParam LocalDate date) {

    }

    @GetMapping
    public Schedule getSchedule(@PathVariable UUID travelPlanId, @RequestParam UUID scheduleId) {

    }


}
