package com.example.travelDiary.presentation.controller;

import com.example.travelDiary.application.service.ScheduleAccessService;
import com.example.travelDiary.domain.model.travel.Schedule;
import com.example.travelDiary.presentation.dto.request.ScheduleUpsertRequest;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/travelPlan/{travelPlanId}/schedule")
public class ScheduleController {
    private final ScheduleAccessService scheduleAccessService;

    public ScheduleController(ScheduleAccessService scheduleAccessService) {
        this.scheduleAccessService = scheduleAccessService;
    }

    @PostMapping("/create_schedule")
    public Schedule createSchedule(@PathVariable("travelPlanId") UUID travelPlanId, @RequestBody ScheduleUpsertRequest request) {
        return scheduleAccessService.createSchedule(travelPlanId, request);
    }

    @PatchMapping("/modify_schedule")
    public Schedule modifySchedule(@PathVariable(value = "travelPlanId") UUID travelPlanId, @RequestBody ScheduleUpsertRequest request) {
        return scheduleAccessService.modifySchedule(request);
    }

    @DeleteMapping("/delete_schedule")
    public UUID deleteSchedule(@PathVariable(value = "travelPlanId") UUID travelPlanId, @RequestParam UUID scheduleId) {
        return scheduleAccessService.deleteSchedule(scheduleId);
    }

    @GetMapping("/search_by_name")
    public Page<Schedule> getScheduleContainingPlaceName(@PathVariable(value = "travelPlanId") UUID travelPlanId,
                                              @RequestParam String name,
                                              @RequestParam Integer pageNumber,
                                              @RequestParam Integer pageSize) {
        return scheduleAccessService.getScheduleContainingName(name, pageNumber, pageSize);
    }

    @GetMapping("/search_by_day")
    public Page<Schedule> getScheduleOnDay(@PathVariable(value = "travelPlanId") UUID travelPlanId,
                                           @RequestParam LocalDate date,
                                           @RequestParam Integer pageNumber,
                                           @RequestParam Integer pageSize) {
        return scheduleAccessService.getSchedulesOnGivenDay(date, pageNumber, pageSize);
    }

    @GetMapping("/search_schedule")
    public Schedule getSchedule(@PathVariable(value = "travelPlanId") UUID travelPlanId, @RequestParam UUID scheduleId) {
        return scheduleAccessService.getSchedule(scheduleId);
    }


}
