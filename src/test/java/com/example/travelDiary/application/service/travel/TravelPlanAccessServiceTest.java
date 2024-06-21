package com.example.travelDiary.application.service.travel;

import com.example.travelDiary.application.events.EventPublisher;
import com.example.travelDiary.application.service.travel.schedule.ScheduleQueryService;
import com.example.travelDiary.authenticationUtils.SecurityTestUtils;
import com.example.travelDiary.authenticationUtils.WithMockAuthUser;
import com.example.travelDiary.common.auth.domain.AuthUser;
import com.example.travelDiary.common.auth.repository.AuthUserRepository;
import com.example.travelDiary.common.auth.service.AuthUserServiceImpl;
import com.example.travelDiary.common.permissions.domain.Resource;
import com.example.travelDiary.common.permissions.domain.ResourcePermission;
import com.example.travelDiary.common.permissions.domain.UserResourcePermissionTypes;
import com.example.travelDiary.common.permissions.repository.ResourcePermissionRepository;
import com.example.travelDiary.common.permissions.repository.ResourceRepository;
import com.example.travelDiary.domain.model.travel.TravelPlan;
import com.example.travelDiary.domain.model.wallet.aggregate.MonetaryEvent;
import com.example.travelDiary.presentation.dto.request.travel.TravelPlanUpsertRequestDTO;
import com.example.travelDiary.repository.persistence.travel.TravelPlanRepository;
import com.example.travelDiary.common.permissions.service.AccessControlService;
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
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles({"test", "secretsLocal"})
@Import(SecurityTestUtils.class)
public class TravelPlanAccessServiceTest {
    @MockBean
    private ResourceRepository resourceRepository;

    @MockBean
    private ResourcePermissionRepository resourcePermissionRepository;

    @MockBean
    private AuthUserServiceImpl authUserService;

    @MockBean
    private ScheduleQueryService scheduleQueryService;

    @MockBean
    private EventPublisher eventPublisher;

    @MockBean
    private AuthUserRepository authUserRepository;

    @Autowired
    private AccessControlService accessControlService;

    @Autowired
    private TravelPlanAccessService travelPlanAccessService;

    @Autowired
    private TravelPlanRepository travelPlanRepository;

    @Autowired
    private ConversionService conversionService;

    private final String authUserId = "b3a0a82f-f737-46f6-9d41-c475a7cc20ec";
    private final AuthUser authUser = SecurityTestUtils.createMockAuthUser(authUserId);

    private UUID travelPlanId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        travelPlanRepository.deleteAll();
        this.travelPlanId = createTravelPlanUtils("Travel Plan");
        when(authUserService.getCurrentAuthenticatedUser()).thenReturn(authUser);
    }

    private UUID createTravelPlanUtils(String travelPlanName) {
        TravelPlan travelPlan = new TravelPlan();
        travelPlan.setName(travelPlanName);
        travelPlanRepository.save(travelPlan);
        UUID createPlanTravelPlanId = travelPlan.getId();

        UUID resourcePlanId1 = UUID.randomUUID();
        Resource resource = new Resource();
        resource.setId(resourcePlanId1);
        resource.setResourceUUID(createPlanTravelPlanId);
        resource.setVisibility("private");
        resource.setType("TravelPlan");
        resource.setCreateTime(Instant.now());
        when(this.resourceRepository.findByResourceUUIDAndType(eq(createPlanTravelPlanId), anyString())).thenReturn(Optional.of(resource));

        ResourcePermission resourcePermission = new ResourcePermission();
        resourcePermission.setResource(resource);
        resourcePermission.setPermissions(UserResourcePermissionTypes.OWNER);
        resourcePermission.setUserProfile(authUser);
        when(this.resourcePermissionRepository.findByUserAndResource(any(AuthUser.class), eq(resource))).thenReturn(Optional.of(resourcePermission));

        return createPlanTravelPlanId;
    }

    @Test
    @WithMockAuthUser(id = authUserId)
    void testGetTravelPageContainingName() {
        UUID createdTravelPlanUUID = createTravelPlanUtils("Test Create Plan");
        Page<TravelPlan> result = travelPlanAccessService.getTravelPageContainingName("Test Create Plan", 0, 10);
        assertThat(result.getContent()).isNotEmpty();
        assertThat(travelPlanRepository.findById(createdTravelPlanUUID).get().getName()).isEqualTo("Test Create Plan");
    }

    @Test
    @WithMockAuthUser(id = authUserId)
    void testGetTravelPlan() {
        TravelPlan result = travelPlanAccessService.getTravelPlan(this.travelPlanId);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(this.travelPlanId);
    }

    @Test
    @WithMockAuthUser(id = authUserId)
    void testGetAllTravelPlan() {
        createTravelPlanUtils("Test Plan1");
        createTravelPlanUtils("Test Plan2");

        List<TravelPlan> result = travelPlanAccessService.getAllTravelPlan();
        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(3);
    }

    @Test
    @WithMockAuthUser(id = authUserId)
    void testCreatePlan() {
        TravelPlanUpsertRequestDTO requestDTO = new TravelPlanUpsertRequestDTO();
        requestDTO.setName("New Plan");

        UUID result = travelPlanAccessService.createPlan(requestDTO);
        Page<TravelPlan> travelPlanPage = travelPlanRepository.findAllByNameContaining("New Plan", PageRequest.of(1,10));

        assertThat(result).isNotNull();
        assertThat(travelPlanPage.getTotalElements()).isEqualTo(1);
        verify(eventPublisher, times(1)).publishEvent(any());

    }

    @Test
    @WithMockAuthUser(id = authUserId)
    void testDeletePlan() {
        UUID createdID = createTravelPlanUtils("Test Plan 1");

        List<UUID> request = Collections.singletonList(createdID);

        //mid-check
        assertThat(travelPlanRepository.findById(createdID)).isNotEmpty();

        List<UUID> result = travelPlanAccessService.deletePlan(request);
        assertThat(result).containsExactly(createdID);

        assertThat(travelPlanRepository.findById(createdID)).isEmpty();
        assertThat(travelPlanRepository.findAll().size()).isEqualTo(1);
        assertThat(travelPlanRepository.findAll().getFirst().getId()).isEqualTo(this.travelPlanId);
    }

    @Test
    @WithMockAuthUser(id = authUserId)
    void testDeletePlanMultiEntry() {
        UUID createdID2 = createTravelPlanUtils("Test Plan 2");
        UUID createdID3 = createTravelPlanUtils("Test Plan 3");

        List<UUID> request = List.of(createdID2, createdID3);

        //mid-check
        assertThat(travelPlanRepository.findById(createdID2)).isNotEmpty();
        //mid-check
        assertThat(travelPlanRepository.findById(createdID3)).isNotEmpty();

        List<UUID> result = travelPlanAccessService.deletePlan(request);
        assertThat(result).containsExactly(createdID2, createdID3);

        assertThat(travelPlanRepository.findById(createdID2)).isEmpty();
        assertThat(travelPlanRepository.findById(createdID3)).isEmpty();
        assertThat(travelPlanRepository.findAll().size()).isEqualTo(1);
        assertThat(travelPlanRepository.findAll().getFirst().getId()).isEqualTo(this.travelPlanId);
    }

    @Test
    @WithMockAuthUser(id = authUserId)
    void testModifyPlanMetadata() {
        UUID planId = createTravelPlanUtils("Test Plan 1");

        TravelPlanUpsertRequestDTO requestDTO = new TravelPlanUpsertRequestDTO();
        requestDTO.setUuid(planId);
        requestDTO.setName("Updated Name");

        TravelPlan result = travelPlanAccessService.modifyPlanMetadata(requestDTO);

        Page<TravelPlan> repositoryResult = travelPlanRepository.findAllByNameContaining("Updated", PageRequest.of(1,10));
        assertThat(result.getName()).isEqualTo("Updated Name");
        assertThat(repositoryResult.getTotalElements()).isEqualTo(1);
    }

    @Test
    @WithMockAuthUser(id = authUserId)
    void testImportPlanSameUser() {
        UUID createdId = createTravelPlanUtils("Test Plan 1");

        TravelPlan result = travelPlanAccessService.importPlan(createdId);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotEqualTo(createdId);
    }


//    Todo
    @WithMockAuthUser(id = authUserId)
    void testGetAssociatedMonetaryEvent() {
        UUID planId = createTravelPlanUtils("Test Plan 1");

        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<MonetaryEvent> result = travelPlanAccessService.getAssociatedMonetaryEvent(planId, pageRequest);

        assertThat(result.getContent()).isEmpty();
    }

}
