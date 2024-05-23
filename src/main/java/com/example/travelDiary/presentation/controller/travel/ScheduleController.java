package com.example.travelDiary.presentation.controller.travel;

import com.example.travelDiary.application.service.travel.ScheduleAccessService;
import com.example.travelDiary.domain.model.travel.Route;
import com.example.travelDiary.domain.model.travel.Schedule;
import com.example.travelDiary.presentation.dto.request.travel.PlaceUpdateRequest;
import com.example.travelDiary.presentation.dto.request.travel.RouteUpdateRequest;
import com.example.travelDiary.presentation.dto.request.travel.ScheduleInsertRequest;
import com.example.travelDiary.presentation.dto.request.travel.ScheduleMetadataUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/travelPlan/{travelPlanId}/schedule")
public class ScheduleController {
    private final ScheduleAccessService scheduleAccessService;

    public ScheduleController(ScheduleAccessService scheduleAccessService) {
        this.scheduleAccessService = scheduleAccessService;
    }

    @PostMapping("/create_schedule")
    public Schedule createSchedule(@PathVariable("travelPlanId") UUID travelPlanId, @RequestBody ScheduleInsertRequest request) {
        return scheduleAccessService.createSchedule(travelPlanId, request);
    }

    @PatchMapping("/update_schedule_metadata")
    public Schedule modifySchedule(@PathVariable(value = "travelPlanId") UUID travelPlanId, @RequestBody ScheduleMetadataUpdateRequest request) {
        return scheduleAccessService.updateScheduleMetadata(request);
    }

    @DeleteMapping("/delete_schedule")
    public UUID deleteSchedule(@PathVariable(value = "travelPlanId") UUID travelPlanId, @RequestParam(value="scheduleId") UUID scheduleId) {
        return scheduleAccessService.deleteSchedule(scheduleId);
    }

    @GetMapping("/search_schedule_by_place_name")
    public Page<Schedule> getScheduleContainingPlaceName(@PathVariable(value = "travelPlanId") UUID travelPlanId,
                                              @RequestParam(value="name") String name,
                                              @RequestParam(value="pageNumber") Integer pageNumber,
                                              @RequestParam(value="pageSize") Integer pageSize) {
        return scheduleAccessService.getScheduleContainingName(name, pageNumber, pageSize);
    }

    @GetMapping("/search_schedule_by_day")
    public Page<Schedule> getScheduleOnDay(@PathVariable(value = "travelPlanId") UUID travelPlanId,
                                           @RequestParam LocalDate date,
                                           @RequestParam Integer pageNumber,
                                           @RequestParam Integer pageSize) {
        return scheduleAccessService.getSchedulesOnGivenDay(date, pageNumber, pageSize);
    }

    @GetMapping("/search_schedule")
    public Schedule getSchedule(@PathVariable(value = "travelPlanId") UUID travelPlanId, @RequestParam(value = "scheduleId") UUID scheduleId) {
        return scheduleAccessService.getSchedule(scheduleId);
    }

    //update PLACE

    @PatchMapping("/update_all_linked_place")
    public List<Schedule> modifyPlaceOnAllLinkedSchedule (@PathVariable(value = "travelPlanId") UUID travelPlanId, @RequestBody PlaceUpdateRequest request) {
        return scheduleAccessService.updatePlace(request);
    }

    @PatchMapping("/update_place_on_schedule")
    public Schedule reassignPlaceOnSchedule (@PathVariable(value = "travelPlanId") UUID travelPlanId, @RequestBody PlaceUpdateRequest request) {
        return scheduleAccessService.reassignPlace(request.scheduleId, request);
    }

    //update Route
    @PatchMapping("/udpate_route_details")
    public Route updateRoute(@PathVariable(value = "travelPlanId") UUID travelPlanId, @RequestBody RouteUpdateRequest request) {
        return scheduleAccessService.updateRouteDetails(request);
    }


}
