package com.example.travelDiary.application.service.travel;

import com.example.travelDiary.TestConfig;
import com.example.travelDiary.application.service.travel.schedule.ScheduleMutationService;
import com.example.travelDiary.application.service.travel.schedule.ScheduleQueryService;
import com.example.travelDiary.authenticationUtils.SecurityTestUtils;
import com.example.travelDiary.authenticationUtils.WithMockAuthUser;
import com.example.travelDiary.common.auth.domain.AuthUser;
import com.example.travelDiary.common.auth.repository.AuthUserRepository;
import com.example.travelDiary.common.auth.service.AuthUserService;
import com.example.travelDiary.common.permissions.domain.Resource;
import com.example.travelDiary.common.permissions.repository.ResourcePermissionRepository;
import com.example.travelDiary.common.permissions.repository.ResourceRepository;
import com.example.travelDiary.common.permissions.service.AccessControlService;
import com.example.travelDiary.common.permissions.service.ResourcePermissionService;
import com.example.travelDiary.common.permissions.service.ResourceService;
import com.example.travelDiary.domain.model.travel.Schedule;
import com.example.travelDiary.domain.model.travel.TravelPlan;
import com.example.travelDiary.domain.model.user.UserProfile;
import com.example.travelDiary.presentation.dto.request.travel.TravelPlanUpsertRequestDTO;
import com.example.travelDiary.presentation.dto.request.travel.schedule.ScheduleInsertRequest;
import com.example.travelDiary.repository.persistence.travel.ScheduleRepository;
import com.example.travelDiary.repository.persistence.travel.TravelPlanRepository;
import com.example.travelDiary.repository.persistence.user.UserProfileRepository;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.ConversionService;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@Slf4j
@SpringBootTest
@ActiveProfiles("${spring.profiles.active}")
@Import(TestConfig.class)
public class TravelPlanServiceIntegratedSecondTest {

    @MockBean
    private AuthUserService authUserService;

    @MockBean
    private ScheduleQueryService scheduleQueryService;

    @MockBean
    private AuthUserRepository authUserRepository;

    @Autowired
    private TravelPlanRepository travelPlanRepository;

    @Autowired
    private ConversionService conversionService;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private ResourcePermissionRepository resourcePermissionRepository;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private ResourcePermissionService resourcePermissionService;

    @Autowired
    private AccessControlService accessControlService;

    @Autowired
    private TravelPlanMutationService travelPlanMutationService;

    @Autowired
    private TravelPlanQueryService travelPlanQueryService;

    @Autowired
    private EntityManager testEntityManager;

    @Autowired
    private ScheduleMutationService scheduleMutationService;
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private PlatformTransactionManager transactionManager;


    private final String authUserId = "b3a0a82f-f737-46f6-9d41-c475a7cc20ec";
    private final AuthUser authUser = SecurityTestUtils.createMockAuthUser(authUserId);
    private UserProfile user = UserProfile.builder().authUserId(UUID.fromString(authUserId)).build();

    private UUID travelPlanId;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        transactionTemplate.execute(status -> {
            travelPlanRepository.deleteAll();
            userProfileRepository.deleteAll();
            resourcePermissionRepository.deleteAll();
            resourceRepository.deleteAll();
            return null;
        });
        this.user = transactionTemplate.execute(status -> userProfileRepository.saveAndFlush(this.user));
        when(authUserService.getCurrentAuthenticatedUser()).thenReturn(authUser);
        when(authUserService.getCurrentUser()).thenReturn(this.user);
    }

    @WithMockAuthUser(id = authUserId)
    public UUID createTravelPlanWithScheduleUtils(String travelPlanName) {
        UUID travelPlanId = transactionTemplate.execute(status -> {
            UUID id =  travelPlanMutationService.createPlan(TravelPlanUpsertRequestDTO.builder().name(travelPlanName).build());
            return id;
        });

        // Ensure the outer transaction is committed by executing a no-op transaction
        TravelPlan travelPlan = transactionTemplate.execute(status -> {
            // Find the travel plan to ensure it has been committed
            return travelPlanRepository.findById(travelPlanId).orElseThrow();
        });

        // Ensure the outer transaction is committed by executing a no-op transaction
        transactionTemplate.execute(status -> {
            // Find the travel plan to ensure it has been committed
            travelPlanRepository.findById(travelPlanId).orElseThrow();
            return null;
        });

        UUID scheduleId = getUuid(travelPlanId);
        List<Schedule> schedules = new ArrayList<>();
        travelPlan.setScheduleList(schedules);
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow();
        travelPlan.getScheduleList().add(schedule);
        travelPlanRepository.saveAndFlush(travelPlan);
        scheduleRepository.saveAndFlush(schedule);


        return travelPlanId;
    }

    private @Nullable UUID getUuid(UUID travelPlanId) {
        TransactionTemplate newTransactionTemplate = new TransactionTemplate(transactionManager);
        newTransactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

        // New transaction for creating the schedule
        UUID scheduleId = newTransactionTemplate.execute(status2 -> {
            TravelPlan travelPlan2 = travelPlanRepository.findById(travelPlanId).orElseThrow();

            return scheduleMutationService.createSchedule(
                    travelPlanId, ScheduleInsertRequest.builder().name("someSchedule").build()
            );
        });
        return scheduleId;
    }

    @Test
    @WithMockAuthUser(id = authUserId)
    @DirtiesContext
    void testDeletePlanWithSchedule() {
        UUID createdID = createTravelPlanWithScheduleUtils("someTravelPlan");
        transactionTemplate.executeWithoutResult(status -> {
            TravelPlan travelPlan = travelPlanRepository.findById(createdID).orElseThrow();
            Resource travelPlanRssId = resourceRepository.findById(travelPlan.getResource().getId()).orElseThrow();
            Resource scheduleRssId = resourceRepository.findById(travelPlan.getScheduleList().getFirst().getResource().getId()).orElseThrow();
            List<UUID> request = Collections.singletonList(createdID);

            assertThat(travelPlanRepository.findById(createdID)).isNotEmpty();

            List<UUID> result = travelPlanMutationService.deleteMultipleTravelPlans(request);
            assertThat(result).containsExactly(createdID);

            assertThat(resourceRepository.findAll()).isEmpty();
            assertThat(resourceRepository.findById(travelPlanRssId.getId())).isEmpty();
            assertThat(resourceRepository.findById(scheduleRssId.getId())).isEmpty();
        });
        assertThat(resourceRepository.findAll()).isEmpty();
    }
}
