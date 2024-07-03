package com.example.travelDiary.application.service.travel.schedule;

import com.example.travelDiary.TestConfig;
import com.example.travelDiary.application.service.location.PlaceMutationService;
import com.example.travelDiary.application.service.travel.RouteAccessService;
import com.example.travelDiary.application.service.travel.TravelPlanMutationService;
import com.example.travelDiary.authenticationUtils.SecurityTestUtils;
import com.example.travelDiary.authenticationUtils.WithMockAuthUser;
import com.example.travelDiary.common.auth.domain.AuthUser;
import com.example.travelDiary.common.auth.service.AuthUserService;
import com.example.travelDiary.common.permissions.domain.Resource;
import com.example.travelDiary.common.permissions.domain.ResourcePermission;
import com.example.travelDiary.common.permissions.domain.UserResourcePermissionTypes;
import com.example.travelDiary.common.permissions.repository.ResourcePermissionRepository;
import com.example.travelDiary.common.permissions.repository.ResourceRepository;
import com.example.travelDiary.domain.model.location.Place;
import com.example.travelDiary.domain.model.travel.Route;
import com.example.travelDiary.domain.model.travel.Schedule;
import com.example.travelDiary.domain.model.user.UserProfile;
import com.example.travelDiary.presentation.dto.request.travel.RouteUpdateRequest;
import com.example.travelDiary.presentation.dto.request.travel.TravelPlanUpsertRequestDTO;
import com.example.travelDiary.presentation.dto.request.travel.location.PlaceUpdateRequest;
import com.example.travelDiary.presentation.dto.request.travel.schedule.ScheduleInsertRequest;
import com.example.travelDiary.presentation.dto.request.travel.schedule.ScheduleMetadataUpdateRequest;
import com.example.travelDiary.repository.persistence.location.PlaceRepository;
import com.example.travelDiary.repository.persistence.travel.RouteRepository;
import com.example.travelDiary.repository.persistence.travel.ScheduleRepository;
import com.example.travelDiary.repository.persistence.travel.TravelPlanRepository;
import com.example.travelDiary.repository.persistence.user.UserProfileRepository;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Slf4j
@ActiveProfiles("${spring.profiles.active}")
@Import(TestConfig.class)
class ScheduleServiceIntegrationTest {

    @MockBean
    private AuthUserService authUserService;

    @Autowired
    private RouteAccessService routeAccessService;

    @Autowired
    private ScheduleQueryService scheduleQueryService;

    @Autowired
    private ScheduleMutationService scheduleMutationService;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private PlaceMutationService placeMutationService;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private ResourcePermissionRepository resourcePermissionRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private TravelPlanMutationService travelPlanMutationService;

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private RouteRepository routeRepository;

    private final String authUserId = "b3a0a82f-f737-46f6-9d41-c475a7cc20ec";
    private final AuthUser authUser = SecurityTestUtils.createMockAuthUser(authUserId);
    private final UserProfile user = UserProfile.builder().authUserId(UUID.fromString(authUserId)).build();
    @Autowired
    private TravelPlanRepository travelPlanRepository;
    @Autowired
    private TransactionTemplate transactionTemplate;


    @BeforeEach
    @Transactional
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userProfileRepository.deleteAll();
        placeRepository.deleteAll();
        resourcePermissionRepository.deleteAll();
        resourceRepository.deleteAll();
        userProfileRepository.save(this.user);
        userProfileRepository.flush();
        when(authUserService.getCurrentAuthenticatedUser()).thenReturn(authUser);
        when(authUserService.getCurrentUser()).thenReturn(this.user);
    }

    private @NotNull Schedule testCreateSchedule(String placeString, String nameString, UUID travelPlanId) {
        Place place = Place.builder().name(placeString).googleMapsKeyId("testkeyId").build();
        placeRepository.save(place);
        placeRepository.flush();
        Schedule schedule = new Schedule();
        schedule.setName(nameString);
        schedule.setPlace(place);
        schedule.setTravelStartTimeEstimate(LocalDateTime.now());
        schedule.setTravelStartTimeEstimate(LocalDateTime.now());
        schedule.setTravelPlanId(travelPlanId);
        scheduleRepository.save(schedule);
        scheduleRepository.flush();
        Resource resource = Resource
                .builder()
                .visibility("private")
                .createTime(Instant.now())
                .type("Schedule")
                .resourceUUID(schedule.getId())
                .permissions(new ArrayList<ResourcePermission>())
                .build();
        resourceRepository.save(resource);
        resourceRepository.flush();
        ResourcePermission resourcePermission = ResourcePermission
                .builder()
                .resource(resource)
                .permissions(UserResourcePermissionTypes.OWNER)
                .userProfile(this.user)
                .build();
        schedule.setResource(resource);
        resource.getPermissions().add(resourcePermission);
        resourcePermissionRepository.save(resourcePermission);
        resourcePermissionRepository.flush();
        scheduleRepository.save(schedule);
        scheduleRepository.flush();
        resourceRepository.save(resource);
        resourceRepository.flush();


        return schedule;
    }


    UUID setScheduleSaveContext() {
        UUID id = transactionTemplate.execute(status -> {
            UUID planId = travelPlanMutationService.createPlan(TravelPlanUpsertRequestDTO.builder().name("test Plan").build());
            travelPlanRepository.flush();
            return planId;
        });
        return id;
    }

    @Test
    @WithMockAuthUser(id = authUserId)
    @DirtiesContext
    void testGetSchedule() {
        String nameString = "Test Plan";
        String placeString = "PlaceString";
        UUID travelPlanId = setScheduleSaveContext();
        Schedule schedule = testCreateSchedule(placeString, nameString, travelPlanId);

        Schedule result = scheduleQueryService.getSchedule(schedule.getId());
        assertThat(result.getName()).isEqualTo(nameString);
    }

    @Test
    @WithMockAuthUser(id = authUserId)
    @DirtiesContext
    void testCreateAndDeleteSchedule() {
        UUID travelPlanId = transactionTemplate.execute(status -> {
            UUID travelPlanIdTx = setScheduleSaveContext();
            Schedule schedule = new Schedule();
            schedule.setTravelPlanId(travelPlanIdTx);
            return travelPlanIdTx;
        });

        Place place = transactionTemplate.execute(status -> {
            Place placetx = new Place();
            placetx.setName("Test Place");
            placeRepository.save(placetx);

            return placetx;
        });

        ScheduleInsertRequest request = new ScheduleInsertRequest();
        request.setPreviousScheduleId(null);
        request.setName("Test Schedule");
        request.setPlace(place);


        UUID createdScheduleId = transactionTemplate.execute(status -> {
            UUID createdScheduleIdtx = scheduleMutationService.createSchedule(travelPlanId, request);
            scheduleRepository.flush();
            return createdScheduleIdtx;
        });

        List<Schedule> findAll = scheduleRepository.findAll();

        Schedule createdSchedule = scheduleQueryService.getSchedule(createdScheduleId);
        assertNotNull(createdSchedule);
        assertThat(createdSchedule.getName()).isEqualTo("Test Schedule");
        assertThat(createdSchedule.getPlace()).isEqualTo(place);

        UUID deletedScheduleId = scheduleMutationService.deleteSchedule(travelPlanId, createdSchedule.getId());
        assertEquals(createdSchedule.getId(), deletedScheduleId);
    }

    @Test
    @WithMockAuthUser(id = authUserId)
    @Transactional
    @DirtiesContext
    void testGetSchedulesOnGivenDay() {
        String nameString = "Test Plan";
        String placeString = "PlaceString";
        UUID travelPlanId = setScheduleSaveContext();
        Schedule schedule = testCreateSchedule(placeString, nameString, travelPlanId);
        LocalDate date = LocalDate.now();
        Page<Schedule> schedules = scheduleQueryService.getAllAuthorisedSchedulesOnGivenDay(date, 0, 10, null);
        assertNotNull(schedules);
        assertThat(schedules.getTotalElements()).isEqualTo(1);
        assertThat(schedules.getContent().getFirst().getName()).isEqualTo(nameString);
    }

    @Test
    @WithMockAuthUser(id = authUserId)
    @Transactional
    @DirtiesContext
    void testGetAllSchedules() {
        UUID travelPlanId = setScheduleSaveContext();
        List<Schedule> schedules = scheduleQueryService.getAllAuthorisedSchedulesInTravelPlan(travelPlanId, Collections.emptyList());

        assertNotNull(schedules);
    }

    @Test
    @WithMockAuthUser(id = authUserId)
    @Transactional
    @DirtiesContext
    void testUpdateScheduleMetadata() {
        UUID travelPlanId = setScheduleSaveContext();
        String nameString = "Test Plan";
        String placeString = "PlaceString";
        Schedule schedule = testCreateSchedule(nameString, placeString, travelPlanId);

        ScheduleMetadataUpdateRequest request = new ScheduleMetadataUpdateRequest();
        request.setScheduleId(schedule.getId());
        request.setName("New Name");

        Schedule updatedSchedule = scheduleMutationService.updateScheduleMetadata(travelPlanId, request);

        assertEquals("New Name", updatedSchedule.getName());
    }

    @Test
    @WithMockAuthUser(id = authUserId)
    @DirtiesContext
    void testReassignPlace() {
        UUID travelPlanId = setScheduleSaveContext();
        String nameString = "Test Plan";
        String placeString = "PlaceString";
        Schedule schedule = testCreateSchedule(nameString, placeString, travelPlanId);

        PlaceUpdateRequest request = new PlaceUpdateRequest();
        request.setScheduleId(schedule.getId());
        request.setName("New Name");
        request.setCountry("Singapore");
        request.setGoogleMapsKeyId("SingaporeBozo");

        Schedule result = scheduleMutationService.reassignPlace(schedule.getId(), request);

        assertThat(result.getPlace().getName()).isEqualTo("New Name");
        assertThat(result.getPlace().getCountry()).isEqualTo("Singapore");

        Schedule dBfetchedResult = scheduleRepository.findById(schedule.getId()).orElseThrow();
        Schedule serviceFetchedResult = scheduleQueryService.getSchedule(schedule.getId());

        assertThat(dBfetchedResult.getPlace()).isEqualTo(result.getPlace());
        assertThat(serviceFetchedResult.getPlace()).isEqualTo(result.getPlace());
    }

    @Test
    @WithMockAuthUser(id = authUserId)
    @Transactional
    @DirtiesContext
    void testUpdateRouteDetails() {
        UUID travelPlanId = setScheduleSaveContext();
        Schedule test1 = testCreateSchedule("test schedule 1", "test place 1", travelPlanId);
        Schedule test2 = testCreateSchedule("test schedule 2", "test place 2", travelPlanId);
        Schedule test3 = testCreateSchedule("test schedule 3", "test place 3", travelPlanId);
        RouteUpdateRequest request = new RouteUpdateRequest();
        request.setInBoundScheduleId(test1.getId());
        request.setOutBoundScheduleId(test3.getId());
        Route route = Route
                .builder()
                .inBoundScheduleId(test1.getId())
                .outBoundScheduleId(test2.getId())
                .methodOfTravel("cat")
                .build();
        routeRepository.save(route);
        test1.setInwardRoute(route);
        test2.setOutwardRoute(route);
        scheduleRepository.save(test1);
        scheduleRepository.save(test2);
        scheduleRepository.flush();
        request.setId(route.getId());

//        when(routeAccessService.updateRoute(request)).thenReturn(route);

        Route result = scheduleMutationService.updateRouteDetails(request);

        assertThat(result.inBoundScheduleId).isEqualTo(test1.getId());
        assertThat(result.outBoundScheduleId).isEqualTo(test3.getId());
    }
}

