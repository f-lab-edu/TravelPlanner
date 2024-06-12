package com.example.travelDiary.presentation.controller.travel;

import com.example.travelDiary.application.service.location.PlaceMutationService;
import com.example.travelDiary.application.service.travel.schedule.ScheduleMutationService;
import com.example.travelDiary.application.service.travel.schedule.ScheduleQueryService;
import com.example.travelDiary.domain.model.location.Place;
import com.example.travelDiary.domain.model.travel.Route;
import com.example.travelDiary.domain.model.travel.Schedule;
import com.example.travelDiary.presentation.dto.request.travel.location.PlaceUpdateRequest;
import com.example.travelDiary.presentation.dto.request.travel.RouteUpdateRequest;
import com.example.travelDiary.presentation.dto.request.travel.schedule.ScheduleInsertRequest;
import com.example.travelDiary.presentation.dto.request.travel.schedule.ScheduleMetadataUpdateRequest;
import com.example.travelDiary.repository.persistence.travel.TravelPlanRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/travelPlan/{travelPlanId}/schedule")
@Slf4j
public class ScheduleController {
    private final ScheduleMutationService scheduleMutationService;
    private final TravelPlanRepository travelPlanRepository;
    private final PlaceMutationService placeMutationService;
    private final ScheduleQueryService scheduleQueryService;

    public ScheduleController(ScheduleMutationService scheduleMutationService, TravelPlanRepository travelPlanRepository, PlaceMutationService placeMutationService, ScheduleQueryService scheduleQueryService) {
        this.scheduleMutationService = scheduleMutationService;
        this.travelPlanRepository = travelPlanRepository;
        this.placeMutationService = placeMutationService;
        this.scheduleQueryService = scheduleQueryService;
    }

    @PutMapping("/create_schedule")
    public Schedule createSchedule(@PathVariable("travelPlanId") UUID travelPlanId, @RequestBody ScheduleInsertRequest request) {
        Schedule schedule = scheduleMutationService.createSchedule(travelPlanId, request);
        travelPlanRepository.findById(travelPlanId).ifPresent(travelPlan -> {travelPlan.getScheduleList().add(schedule);});
        return schedule;
    }

    @PatchMapping("/update_schedule_metadata")
    public Schedule modifySchedule(@PathVariable(value = "travelPlanId") UUID travelPlanId, @RequestBody ScheduleMetadataUpdateRequest request) {
        return scheduleMutationService.updateScheduleMetadata(request);
    }

    @DeleteMapping("/delete_schedule")
    public UUID deleteSchedule(@PathVariable(value = "travelPlanId") UUID travelPlanId, @RequestParam(value="scheduleId") UUID scheduleId) {
        return scheduleMutationService.deleteSchedule(travelPlanId, scheduleId);
    }

    @GetMapping("/search_schedule_by_place_name")
    public Page<Schedule> getScheduleContainingPlaceName(@PathVariable(value = "travelPlanId") UUID travelPlanId,
                                              @RequestParam(value="name") String name,
                                              @RequestParam(value="pageNumber") Integer pageNumber,
                                              @RequestParam(value="pageSize") Integer pageSize) {
        return scheduleQueryService.getScheduleContainingName(name, pageNumber, pageSize);
    }

    @GetMapping("/search_schedule_by_day")
    public Page<Schedule> getScheduleOnDay(@PathVariable(value = "travelPlanId") UUID travelPlanId,
                                           @RequestParam LocalDate date,
                                           @RequestParam Integer pageNumber,
                                           @RequestParam Integer pageSize) {
        log.info("search schedule by day reached");
        return scheduleQueryService.getSchedulesOnGivenDay(date, pageNumber, pageSize);
    }

    @GetMapping("/search_schedule")
    public Schedule getSchedule(@PathVariable(value = "travelPlanId") UUID travelPlanId, @RequestParam(value = "scheduleId") UUID scheduleId) {
        return scheduleQueryService.getSchedule(scheduleId);
    }

//    @GetMapping("/search_schedule_by_day")
//    public List<Schedule> getImmediatePrecedingAndSucceedingSchedule(@PathVariable(value = "travelPlanId") UUID travelPlanId,
//                                           @RequestParam LocalDateTime date) {
//        log.info("search schedule by day reached");
//        return scheduleQueryService.getImmediatePrecedingAndSucceedingSchedule(travelPlanId, date);
//    }

    //update PLACE
    @PatchMapping("/update_all_linked_place")
    public Place modifyPlaceOnAllLinkedSchedule (@PathVariable(value = "travelPlanId") UUID travelPlanId, @RequestBody PlaceUpdateRequest request) {
        return placeMutationService.updatePlace(request);
    }

    @PatchMapping("/update_place_on_schedule")
    public Schedule reassignPlaceOnSchedule (@PathVariable(value = "travelPlanId") UUID travelPlanId, @RequestBody PlaceUpdateRequest request) {
        return scheduleMutationService.reassignPlace(request.scheduleId, request);
    }

    //update Route
    @PatchMapping("/udpate_route_details")
    public Route updateRoute(@PathVariable(value = "travelPlanId") UUID travelPlanId, @RequestBody RouteUpdateRequest request) {
        return scheduleMutationService.updateRouteDetails(request);
    }


}
