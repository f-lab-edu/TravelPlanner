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
import com.example.travelDiary.common.permissions.domain.ResourcePermission;
import com.example.travelDiary.common.permissions.domain.UserResourcePermissionTypes;
import com.example.travelDiary.common.permissions.repository.ResourcePermissionRepository;
import com.example.travelDiary.common.permissions.repository.ResourceRepository;
import com.example.travelDiary.common.permissions.service.AccessControlService;
import com.example.travelDiary.common.permissions.service.ResourcePermissionService;
import com.example.travelDiary.common.permissions.service.ResourceService;
import com.example.travelDiary.domain.model.travel.TravelPlan;
import com.example.travelDiary.domain.model.user.UserProfile;
import com.example.travelDiary.domain.model.wallet.aggregate.MonetaryEvent;
import com.example.travelDiary.presentation.dto.request.travel.TravelPlanUpsertRequestDTO;
import com.example.travelDiary.repository.persistence.travel.ScheduleRepository;
import com.example.travelDiary.repository.persistence.travel.TravelPlanRepository;
import com.example.travelDiary.repository.persistence.user.UserProfileRepository;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;
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
public class TravelPlanServiceIntegratedTest {

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
        this.travelPlanId = createTravelPlanUtils("Travel Plan");
        when(authUserService.getCurrentAuthenticatedUser()).thenReturn(authUser);
        when(authUserService.getCurrentUser()).thenReturn(this.user);
    }

    public UUID createTravelPlanUtils(String travelPlanName) {
        return transactionTemplate.execute(status -> {
            TravelPlan travelPlan = new TravelPlan();
            travelPlan.setName(travelPlanName);
            travelPlanRepository.saveAndFlush(travelPlan);
            UUID createPlanTravelPlanId = travelPlan.getId();

            Resource resource = new Resource();
            resource.setResourceUUID(createPlanTravelPlanId);
            resource.setVisibility("private");
            resource.setType("TravelPlan");
            resource.setCreateTime(Instant.now());
            resource.setPermissions(new ArrayList<>());
            resourceRepository.saveAndFlush(resource);

            ResourcePermission resourcePermission = new ResourcePermission();
            resourcePermission.setResource(resource);
            resourcePermission.setPermissions(UserResourcePermissionTypes.OWNER);
            resourcePermission.setUserProfile(this.user);
            resourcePermissionRepository.saveAndFlush(resourcePermission);

            travelPlan.setResource(resource);
            travelPlanRepository.saveAndFlush(travelPlan);

            resource.getPermissions().add(resourcePermission);
            resourceRepository.saveAndFlush(resource);

            testEntityManager.flush();
            testEntityManager.clear();

            return createPlanTravelPlanId;
        });
    }

    public UUID createTravelPlanUtilsDiffUser(String travelPlanName) {
        return transactionTemplate.execute(status -> {
            UserProfile diffUser = UserProfile.builder().authUserId(UUID.randomUUID()).build();
            userProfileRepository.saveAndFlush(diffUser);

            TravelPlan travelPlan = new TravelPlan();
            travelPlan.setName(travelPlanName);
            travelPlanRepository.saveAndFlush(travelPlan);
            UUID createPlanTravelPlanId = travelPlan.getId();

            Resource resource = new Resource();
            resource.setResourceUUID(createPlanTravelPlanId);
            resource.setVisibility("private");
            resource.setType("TravelPlan");
            resource.setCreateTime(Instant.now());
            resource.setPermissions(new ArrayList<>());
            resourceRepository.saveAndFlush(resource);

            ResourcePermission resourcePermission = new ResourcePermission();
            resourcePermission.setResource(resource);
            resourcePermission.setPermissions(UserResourcePermissionTypes.OWNER);
            resourcePermission.setUserProfile(diffUser);
            resourcePermissionRepository.saveAndFlush(resourcePermission);

            travelPlan.setResource(resource);
            travelPlanRepository.saveAndFlush(travelPlan);

            resource.getPermissions().add(resourcePermission);
            resourceRepository.saveAndFlush(resource);

            testEntityManager.flush();
            testEntityManager.clear();

            return createPlanTravelPlanId;
        });
    }


    @Test
    @WithMockAuthUser(id = authUserId)
    @Transactional
    @DirtiesContext
    void testGetTravelPageContainingName() {
        UUID createdTravelPlanUUID = createTravelPlanUtils("Test Create Plan");
        List<TravelPlan> travelPlanList = travelPlanRepository.findAll();
        Page<TravelPlan> result = travelPlanQueryService.getAuthorisedTravelPageContainingName("Test Create Plan", 0, 10, null);
        assertThat(result.getContent()).isNotEmpty();
        assertThat(travelPlanRepository.findById(createdTravelPlanUUID).get().getName()).isEqualTo("Test Create Plan");
    }

    @Test
    @WithMockAuthUser(id = authUserId)
    @Transactional
    @DirtiesContext
    void testGetTravelPlan() {
        TravelPlan result = travelPlanQueryService.getTravelPlan(this.travelPlanId);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(this.travelPlanId);
    }

    @Test
    @WithMockAuthUser(id = authUserId)
    @Transactional
    @DirtiesContext
    void testGetAllTravelPlan() {
        createTravelPlanUtils("Test Plan1");
        createTravelPlanUtils("Test Plan2");

        List<TravelPlan> result = travelPlanQueryService.getAllAuthorisedTravelPlan(null);
        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(3);
    }

    @Test
    @WithMockAuthUser(id = authUserId)
    @Transactional
    @DirtiesContext
    void testGetAllTravelPlanAccessControl() {
        createTravelPlanUtils("Test Plan1");
        createTravelPlanUtils("Test Plan2");
        createTravelPlanUtilsDiffUser("Test Plan3");

        List<TravelPlan> result = travelPlanQueryService.getAllAuthorisedTravelPlan(null);
        assertThat(result).isNotEmpty();
        //3 because one was created by the beforeEach
        assertThat(result.size()).isEqualTo(3);
    }

    @Test
    @WithMockAuthUser(id = authUserId)
    @Transactional
    @DirtiesContext
    void testCreatePlan() {
        TravelPlanUpsertRequestDTO requestDTO = new TravelPlanUpsertRequestDTO();
        requestDTO.setName("New Plan");

        UUID result = travelPlanMutationService.createPlan(requestDTO);
        Page<TravelPlan> travelPlanPage = travelPlanRepository.findAllByNameContaining("New Plan", PageRequest.of(0, 10));
        Page<TravelPlan> travelPlans = travelPlanQueryService.getAuthorisedTravelPageContainingName("New Plan", 0, 10, null);
        log.info(travelPlans.getContent().toString());

        assertThat(travelPlanPage).isNotNull();
        assertThat(travelPlans).isNotNull();
        assertThat(travelPlanPage.getTotalElements()).isEqualTo(1);
        assertThat(travelPlans.getTotalElements()).isEqualTo(1);
        assertThat(travelPlans.getContent().get(0).getName()).isEqualTo("New Plan");

    }



    @Test
    @WithMockAuthUser(id = authUserId)
    @Transactional
    @DirtiesContext
    void testDeletePlan() {
        UUID createdID = createTravelPlanUtils("Test Plan 1");

        List<UUID> request = Collections.singletonList(createdID);

        //mid-check
        assertThat(travelPlanRepository.findById(createdID)).isNotEmpty();

        List<UUID> result = travelPlanMutationService.deleteMultipleTravelPlans(request);
        assertThat(result).containsExactly(createdID);

        assertThat(travelPlanRepository.findById(createdID)).isEmpty();
        assertThat(travelPlanRepository.findAll().size()).isEqualTo(1);
        assertThat(travelPlanRepository.findAll().getFirst().getId()).isEqualTo(this.travelPlanId);
    }

    @Test
    @WithMockAuthUser(id = authUserId)
    @Transactional
    @DirtiesContext
    void testDeletePlanMultiEntry() {
        UUID createdID2 = createTravelPlanUtils("Test Plan 2");
        UUID createdID3 = createTravelPlanUtils("Test Plan 3");

        List<UUID> request = List.of(createdID2, createdID3);

        //mid-check
        assertThat(travelPlanRepository.findById(createdID2)).isNotEmpty();
        //mid-check
        assertThat(travelPlanRepository.findById(createdID3)).isNotEmpty();

        List<Resource> resources = resourceRepository.findAll();

        List<UUID> result = travelPlanMutationService.deleteMultipleTravelPlans(request);
        assertThat(result).containsExactly(createdID2, createdID3);

        assertThat(travelPlanRepository.findById(createdID2)).isEmpty();
        assertThat(travelPlanRepository.findById(createdID3)).isEmpty();
        assertThat(travelPlanRepository.findAll().size()).isEqualTo(1);
        assertThat(travelPlanRepository.findAll().getFirst().getId()).isEqualTo(this.travelPlanId);
    }

    @Test
    @WithMockAuthUser(id = authUserId)
    @Transactional
    @DirtiesContext
    void testModifyPlanMetadata() {
        UUID planId = createTravelPlanUtils("Test Plan 1");

        TravelPlanUpsertRequestDTO requestDTO = new TravelPlanUpsertRequestDTO();
        requestDTO.setUuid(planId);
        requestDTO.setName("Updated Name");

        TravelPlan result = travelPlanMutationService.modifyPlanMetadata(requestDTO);

        Page<TravelPlan> repositoryResult = travelPlanRepository.findAllByNameContaining("Updated", PageRequest.of(1, 10));
        assertThat(result.getName()).isEqualTo("Updated Name");
        assertThat(repositoryResult.getTotalElements()).isEqualTo(1);
    }

    @Test
    @WithMockAuthUser(id = authUserId)
    @Transactional
    @DirtiesContext
    void testImportPlanSameUser() {
        UUID createdId = createTravelPlanUtils("Test Plan 1");

        TravelPlan result = travelPlanMutationService.importPlan(createdId);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotEqualTo(createdId);
    }


    //    Todo
    @WithMockAuthUser(id = authUserId)
    @Transactional
    @DirtiesContext
    void testGetAssociatedMonetaryEvent() {
        UUID planId = createTravelPlanUtils("Test Plan 1");

        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<MonetaryEvent> result = travelPlanQueryService.getAssociatedMonetaryEvent(planId, pageRequest);

        assertThat(result.getContent()).isEmpty();
    }

}
