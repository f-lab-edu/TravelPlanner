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
import com.example.travelDiary.presentation.dto.response.travel.ScheduleResponse;
import com.example.travelDiary.repository.persistence.travel.TravelPlanRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
    private final ConversionService conversionService;

    public ScheduleController(ScheduleMutationService scheduleMutationService, TravelPlanRepository travelPlanRepository, PlaceMutationService placeMutationService, ScheduleQueryService scheduleQueryService, ConversionService conversionService) {
        this.scheduleMutationService = scheduleMutationService;
        this.travelPlanRepository = travelPlanRepository;
        this.placeMutationService = placeMutationService;
        this.scheduleQueryService = scheduleQueryService;
        this.conversionService = conversionService;
    }

    @PutMapping("/create_schedule")
    public ResponseEntity<ScheduleResponse> createSchedule(@PathVariable("travelPlanId") UUID travelPlanId, @RequestBody ScheduleInsertRequest request) {
        UUID scheduleId = scheduleMutationService.createSchedule(travelPlanId, request);
        Schedule schedule = scheduleQueryService.getSchedule(scheduleId);
        travelPlanRepository.findById(travelPlanId).ifPresent(travelPlan -> {travelPlan.getScheduleList().add(schedule);});
        return ResponseEntity.ok(conversionService.convert(schedule,ScheduleResponse.class));
    }

    @PatchMapping("/update_schedule_metadata")
    public ResponseEntity<ScheduleResponse> modifySchedule(@PathVariable(value = "travelPlanId") UUID travelPlanId, @RequestBody ScheduleMetadataUpdateRequest request) {
        Schedule schedule = scheduleMutationService.updateScheduleMetadata(travelPlanId, request);
        return ResponseEntity.ok(conversionService.convert(schedule,ScheduleResponse.class));
    }

    @DeleteMapping("/delete_schedule")
    public UUID deleteSchedule(@PathVariable(value = "travelPlanId") UUID travelPlanId, @RequestParam(value="scheduleId") UUID scheduleId) {
        return scheduleMutationService.deleteSchedule(travelPlanId, scheduleId);
    }

    @GetMapping("/search_schedule_by_place_name")
    public ResponseEntity<Page<ScheduleResponse>> getScheduleContainingPlaceName(@PathVariable(value = "travelPlanId") UUID travelPlanId,
                                              @RequestParam(value="name") String name,
                                              @RequestParam(value="pageNumber") Integer pageNumber,
                                              @RequestParam(value="pageSize") Integer pageSize) {
        Page<Schedule> schedulePages = scheduleQueryService.getAllAuthorisedScheduleContainingName(name, pageNumber, pageSize, null);
        return ResponseEntity.ok(schedulePages.map(p->conversionService.convert(p,ScheduleResponse.class)));
    }

    @GetMapping("/search_schedule_by_day")
    public ResponseEntity<Page<ScheduleResponse>>getScheduleOnDay(@PathVariable(value = "travelPlanId") UUID travelPlanId,
                                           @RequestParam LocalDate date,
                                           @RequestParam Integer pageNumber,
                                           @RequestParam Integer pageSize) {
        log.info("search schedule by day reached");
        Page<Schedule> schedulePages = scheduleQueryService.getAllAuthorisedSchedulesOnGivenDay(date, pageNumber, pageSize, null);
        return ResponseEntity.ok(schedulePages.map(p->conversionService.convert(p,ScheduleResponse.class)));
    }

    @GetMapping("/search_all")
    public ResponseEntity<List<ScheduleResponse>> getSchedule(@PathVariable(value = "travelPlanId") UUID travelPlanId) {
        List<Schedule> scheduleList = scheduleQueryService.getAllAuthorisedSchedulesInTravelPlan(travelPlanId, null);
        return ResponseEntity.ok(scheduleList.stream().map(p->conversionService.convert(p,ScheduleResponse.class)).toList());
    }

    @GetMapping("/search_schedule")
    public ResponseEntity<ScheduleResponse> getSchedule(@PathVariable(value = "travelPlanId") UUID travelPlanId, @RequestParam(value = "scheduleId") UUID scheduleId) {
        Schedule schedule = scheduleQueryService.getSchedule(scheduleId);
        return ResponseEntity.ok(conversionService.convert(schedule, ScheduleResponse.class));
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
    public ResponseEntity<ScheduleResponse> reassignPlaceOnSchedule (@PathVariable(value = "travelPlanId") UUID travelPlanId, @RequestBody PlaceUpdateRequest request) {
        Schedule schedule = scheduleMutationService.reassignPlace(request.scheduleId, request);
        return ResponseEntity.ok(conversionService.convert(schedule,ScheduleResponse.class));
    }

    //update Route
    @PatchMapping("/udpate_route_details")
    public Route updateRoute(@PathVariable(value = "travelPlanId") UUID travelPlanId, @RequestBody RouteUpdateRequest request) {
        return scheduleMutationService.updateRouteDetails(request);
    }


}
