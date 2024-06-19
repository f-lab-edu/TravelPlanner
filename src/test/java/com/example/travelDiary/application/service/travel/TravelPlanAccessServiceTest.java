package com.example.travelDiary.application.service.travel;

import com.example.travelDiary.application.events.EventPublisher;
import com.example.travelDiary.application.service.travel.schedule.ScheduleQueryService;
import com.example.travelDiary.authenticationUtils.SecurityTestUtils;
import com.example.travelDiary.authenticationUtils.WithMockAuthUser;
import com.example.travelDiary.domain.model.travel.TravelPlan;
import com.example.travelDiary.domain.model.wallet.aggregate.MonetaryEvent;
import com.example.travelDiary.presentation.dto.request.travel.TravelPlanUpsertRequestDTO;
import com.example.travelDiary.repository.persistence.travel.TravelPlanRepository;
import com.example.travelDiary.common.permissions.service.AccessControlService;
import com.example.travelDiary.common.auth.service.AuthUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles({"test", "secretsLocal"})
@Import(SecurityTestUtils.class)
public class TravelPlanAccessServiceTest {

    @Autowired
    private TravelPlanAccessService travelPlanAccessService;

    @MockBean
    private TravelPlanRepository travelPlanRepository;

    @MockBean
    private ConversionService conversionService;

    @MockBean
    private ScheduleQueryService scheduleQueryService;

    @MockBean
    private EventPublisher eventPublisher;

    @MockBean
    private AccessControlService accessControlService;

    @MockBean
    private AuthUserService authUserService;

    private final String authUserId = "b3a0a82f-f737-46f6-9d41-c475a7cc20ec";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        travelPlanRepository.deleteAll();
        // Mock the access control checks
        when(accessControlService.hasPermission(any(), any(UUID.class), anyString())).thenReturn(true);
    }

    @Test
    @WithMockAuthUser(id = authUserId)
    void testGetTravelPageContainingName() {
        TravelPlan plan = new TravelPlan();
        plan.setName("Sample Plan");
        travelPlanRepository.save(plan);

        Page<TravelPlan> result = travelPlanAccessService.getTravelPageContainingName("Sample", 0, 10);
        assertThat(result.getContent()).isNotEmpty();
    }

    @Test
    @WithMockAuthUser(id = authUserId)
    void testGetTravelPlan() {
        TravelPlan plan = new TravelPlan();
        UUID planId = UUID.randomUUID();
        plan.setId(planId);
        when(travelPlanRepository.findById(planId)).thenReturn(Optional.of(plan));

        TravelPlan result = travelPlanAccessService.getTravelPlan(planId);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(planId);
    }

    @Test
    @WithMockAuthUser(id = authUserId)
    void testCreatePlan() {
        TravelPlanUpsertRequestDTO requestDTO = new TravelPlanUpsertRequestDTO();
        requestDTO.setName("New Plan");
        TravelPlan createdPlan = new TravelPlan();
        createdPlan.setId(UUID.randomUUID());
        when(conversionService.convert(requestDTO, TravelPlan.class)).thenReturn(createdPlan);
        when(travelPlanRepository.save(any(TravelPlan.class))).thenReturn(createdPlan);

        UUID result = travelPlanAccessService.createPlan(requestDTO);
        assertThat(result).isNotNull();

        verify(eventPublisher, times(1)).publishEvent(any());
    }

    @Test
    @WithMockAuthUser(id = authUserId)
    void testDeletePlan() {
        TravelPlan plan = new TravelPlan();
        UUID planId = UUID.randomUUID();
        plan.setId(planId);
        when(travelPlanRepository.findById(planId)).thenReturn(Optional.of(plan));

        List<UUID> request = Collections.singletonList(planId);
        List<UUID> result = travelPlanAccessService.deletePlan(request);

        assertThat(result).containsExactly(planId);
        verify(travelPlanRepository, times(1)).deleteAllById(request);
    }

    @Test
    @WithMockAuthUser(id = authUserId)
    void testModifyPlanMetadata() {
        TravelPlan plan = new TravelPlan();
        UUID planId = UUID.randomUUID();
        plan.setId(planId);
        when(travelPlanRepository.findById(planId)).thenReturn(Optional.of(plan));

        TravelPlanUpsertRequestDTO requestDTO = new TravelPlanUpsertRequestDTO();
        requestDTO.setUuid(planId);
        requestDTO.setName("Updated Name");

        TravelPlan result = travelPlanAccessService.modifyPlanMetadata(requestDTO);
        assertThat(result.getName()).isEqualTo("Updated Name");
        verify(travelPlanRepository, times(1)).save(plan);
    }

    @Test
    @WithMockAuthUser(id = authUserId)
    void testImportPlan() {
        TravelPlan plan = new TravelPlan();
        plan.setName("Imported Plan");
        when(travelPlanRepository.save(plan)).thenReturn(plan);

        TravelPlan result = travelPlanAccessService.importPlan(plan);
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Imported Plan");
    }

    @Test
    @WithMockAuthUser(id = authUserId)
    void testGetAssociatedMonetaryEvent() {
        TravelPlan plan = new TravelPlan();
        UUID planId = UUID.randomUUID();
        plan.setId(planId);
        when(travelPlanRepository.findById(planId)).thenReturn(Optional.of(plan));
        when(scheduleQueryService.getAssociatedMonetaryEvent(any())).thenReturn(Collections.emptyList());

        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<MonetaryEvent> result = travelPlanAccessService.getAssociatedMonetaryEvent(planId, pageRequest);

        assertThat(result.getContent()).isEmpty();
    }

    @Test
    @WithMockAuthUser(id = authUserId)
    void testGetAllTravelPlan() {
        TravelPlan plan1 = new TravelPlan();
        TravelPlan plan2 = new TravelPlan();
        when(travelPlanRepository.findAll()).thenReturn(Arrays.asList(plan1, plan2));

        List<TravelPlan> result = travelPlanAccessService.getAllTravelPlan();
        assertThat(result).hasSize(2);
    }
}
