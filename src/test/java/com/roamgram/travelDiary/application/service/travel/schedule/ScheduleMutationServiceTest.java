package com.roamgram.travelDiary.application.service.travel.schedule;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.roamgram.travelDiary.application.events.EventPublisher;
import com.roamgram.travelDiary.application.events.schedule.ScheduleCreatedEvent;
import com.roamgram.travelDiary.application.events.schedule.ScheduleDeletedEvent;
import com.roamgram.travelDiary.application.events.schedule.SchedulePreDeletedEvent;
import com.roamgram.travelDiary.application.events.schedule.ScheduleUpdatedEvent;
import com.roamgram.travelDiary.application.service.location.PlaceMutationService;
import com.roamgram.travelDiary.application.service.travel.RouteAccessService;
import com.roamgram.travelDiary.domain.model.location.Place;
import com.roamgram.travelDiary.domain.model.travel.Route;
import com.roamgram.travelDiary.domain.model.travel.Schedule;
import com.roamgram.travelDiary.presentation.dto.request.travel.RouteUpdateRequest;
import com.roamgram.travelDiary.presentation.dto.request.travel.location.PlaceUpdateRequest;
import com.roamgram.travelDiary.presentation.dto.request.travel.schedule.ScheduleInsertRequest;
import com.roamgram.travelDiary.presentation.dto.request.travel.schedule.ScheduleMetadataUpdateRequest;
import com.roamgram.travelDiary.repository.persistence.travel.ScheduleRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.ConversionService;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
@Slf4j
class ScheduleMutationServiceTest {

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private PlaceMutationService placeMutationService;

    @Mock
    private ConversionService conversionService;

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private RouteAccessService routeAccessService;

    @InjectMocks
    private ScheduleMutationService scheduleMutationService;

    @Test
    @Transactional
    void testCreateSchedule() {
        UUID travelPlanId = UUID.randomUUID();
        UUID scheduleId = UUID.randomUUID();
        ScheduleInsertRequest request = new ScheduleInsertRequest();
        Schedule schedule = new Schedule();
        schedule.setId(scheduleId);
        when(conversionService.convert(request, Schedule.class)).thenReturn(schedule);
        when(scheduleRepository.save(schedule)).thenReturn(schedule);


        UUID resultId = scheduleMutationService.createSchedule(travelPlanId, request);
        log.info(String.valueOf(resultId));

        assertEquals(schedule.getId(), resultId);
        verify(scheduleRepository).save(schedule);
        verify(eventPublisher).publishEvent(any(ScheduleCreatedEvent.class));
    }

    @Test
    @Transactional
    void testDeleteSchedule() {
        UUID travelPlanId = UUID.randomUUID();
        UUID scheduleId = UUID.randomUUID();
        Schedule schedule = new Schedule();
        schedule.setPlace(new Place());
        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(schedule));

        UUID result = scheduleMutationService.deleteSchedule(travelPlanId, scheduleId);

        assertEquals(scheduleId, result);
        verify(scheduleRepository).delete(schedule);
        verify(eventPublisher).publishEvent(any(SchedulePreDeletedEvent.class));
        verify(eventPublisher).publishEvent(any(ScheduleDeletedEvent.class));
    }

    @Test
    @Transactional
    void testUpdateScheduleMetadata() {
        UUID travelPlanId = UUID.randomUUID();
        ScheduleMetadataUpdateRequest request = new ScheduleMetadataUpdateRequest();
        request.setScheduleId(UUID.randomUUID());
        Schedule schedule = new Schedule();
        Schedule sanitizedSchedule = new Schedule();
        when(scheduleRepository.findById(request.getScheduleId())).thenReturn(Optional.of(schedule));
//        when(conversionService.convert(request, Schedule.class)).thenReturn(sanitizedSchedule);

        Schedule result = scheduleMutationService.updateScheduleMetadata(travelPlanId, request);

        assertEquals(schedule, result);
        verify(scheduleRepository).save(schedule);
        verify(eventPublisher).publishEvent(any(ScheduleUpdatedEvent.class));
    }

    @Test
    @Transactional
    void testReassignPlace() {
        UUID scheduleId = UUID.randomUUID();
        PlaceUpdateRequest request = new PlaceUpdateRequest();
        Place place = new Place();
        Schedule schedule = new Schedule();
        schedule.setPlace(new Place());
        when(placeMutationService.createNewPlaceIfNotExists(request)).thenReturn(place);
        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(schedule));

        Schedule result = scheduleMutationService.reassignPlace(scheduleId, request);

        assertEquals(schedule, result);
        assertEquals(place, schedule.getPlace());
        verify(scheduleRepository).save(schedule);
        verify(eventPublisher).publishEvent(any(ScheduleUpdatedEvent.class));
        verify(eventPublisher).publishEvent(any(ScheduleDeletedEvent.class));
    }

    @Test
    @Transactional
    void testUpdateRouteDetails() {
        RouteUpdateRequest request = new RouteUpdateRequest();
        request.setInBoundScheduleId(UUID.randomUUID());
        request.setOutBoundScheduleId(UUID.randomUUID());
        Schedule inboundSchedule = new Schedule();
        Schedule outboundSchedule = new Schedule();
        Route route = new Route();
        when(scheduleRepository.findById(request.getInBoundScheduleId())).thenReturn(Optional.of(inboundSchedule));
        when(scheduleRepository.findById(request.getOutBoundScheduleId())).thenReturn(Optional.of(outboundSchedule));
        when(routeAccessService.updateRoute(request)).thenReturn(route);

        Route result = scheduleMutationService.updateRouteDetails(request);

        assertEquals(route, result);
        verify(routeAccessService).updateRoute(request);
    }
}

