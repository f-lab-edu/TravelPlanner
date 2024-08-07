package com.roamgram.travelDiary.common.permissions.service;

import com.roamgram.travelDiary.authenticationUtils.WithMockAuthUser;
import com.roamgram.travelDiary.common.auth.domain.AuthUser;
import com.roamgram.travelDiary.common.auth.repository.AuthUserRepository;
import com.roamgram.travelDiary.common.auth.service.AuthUserService;
import com.roamgram.travelDiary.common.permissions.domain.Resource;
import com.roamgram.travelDiary.common.permissions.domain.ResourcePermission;
import com.roamgram.travelDiary.common.permissions.domain.UserResourcePermissionTypes;
import com.roamgram.travelDiary.common.permissions.repository.ResourcePermissionRepository;
import com.roamgram.travelDiary.common.permissions.repository.ResourceRepository;
import com.roamgram.travelDiary.domain.IdentifiableResource;
import com.roamgram.travelDiary.domain.model.user.UserProfile;
import com.roamgram.travelDiary.repository.persistence.user.UserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("${spring.profiles.active}")
public class AccessControlServiceTest {

    @MockBean
    private ResourceRepository resourceRepository;

    @MockBean
    private ResourcePermissionRepository resourcePermissionRepository;

    @MockBean
    private AuthUserRepository authUserRepository;

    @MockBean
    private UserProfileRepository UserProfileRepository;

    @MockBean
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private AuthUserService authUserService;

    @Autowired
    private AccessControlService accessControlService;

    private AuthUser mockUser;
    private UserProfile userProfile;

    private final String authUserId = "b3a0a82f-f737-46f6-9d41-c475a7cc20ec";
    @Autowired
    private UserProfileRepository userProfileRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockUser = new AuthUser();
        mockUser.setId(UUID.randomUUID());
        mockUser.setUsername("testUser");
        mockUser.setId(UUID.fromString(authUserId));
        authUserRepository.save(mockUser);

        userProfile = UserProfile.builder().authUserId(UUID.fromString(authUserId)).build();
        userProfileRepository.save(userProfile);

        // Ensure the repository mock returns the user
        when(authUserRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));
        when(UserProfileRepository.findByAuthUserId(mockUser.getId())).thenReturn(Optional.of(userProfile));
    }

    @Test
    @WithMockAuthUser(id = authUserId)
    void testHasPermission_PublicResource() {
        Resource resource = new Resource();
        resource.setVisibility("public");

        when(resourceRepository.findByResourceUUIDAndType(any(UUID.class), anyString())).thenReturn(Optional.of(resource));

        boolean result = accessControlService.hasPermission(IdentifiableResource.class, UUID.randomUUID(), "VIEW");
        assertTrue(result);
    }

    @Test
    @WithMockAuthUser(id = authUserId)
    void testHasPermission_NoResource() {
        when(resourceRepository.findByResourceUUIDAndType(any(UUID.class), anyString())).thenReturn(Optional.empty());

        boolean result = accessControlService.hasPermission(IdentifiableResource.class, UUID.randomUUID(), "VIEW");
        assertFalse(result);
    }

    @Test
    @WithMockAuthUser(id = authUserId)
    void testHasPermission_ValidPermission() {
        Resource resource = new Resource();
        ResourcePermission resourcePermission = new ResourcePermission();
        resourcePermission.setPermissions(UserResourcePermissionTypes.EDITOR);

        when(resourceRepository.findByResourceUUIDAndType(any(UUID.class), anyString())).thenReturn(Optional.of(resource));
        when(resourcePermissionRepository.findByUserProfileAndResource(any(UserProfile.class), any(Resource.class))).thenReturn(Optional.of(resourcePermission));

        boolean result = accessControlService.hasPermission(IdentifiableResource.class, UUID.randomUUID(), "EDITOR");
        assertTrue(result);
    }

    @Test
    @WithMockAuthUser(id = authUserId)
    void testHasPermission_InvalidPermission() {
        Resource resource = new Resource();
        ResourcePermission resourcePermission = new ResourcePermission();
        resourcePermission.setPermissions(UserResourcePermissionTypes.VIEW);

        when(resourceRepository.findByResourceUUIDAndType(any(UUID.class), anyString())).thenReturn(Optional.of(resource));
        when(resourcePermissionRepository.findByUserProfileAndResource(any(UserProfile.class), any(Resource.class))).thenReturn(Optional.of(resourcePermission));

        boolean result = accessControlService.hasPermission(IdentifiableResource.class, UUID.randomUUID(), "EDITOR");
        assertFalse(result);
    }

    @Test
    @WithMockAuthUser(id = authUserId)
    void testAssignPermission_ValidUser() {
        Resource resource = new Resource();
        ResourcePermission resourcePermission = new ResourcePermission();
        resourcePermission.setPermissions(UserResourcePermissionTypes.OWNER);

        when(resourcePermissionRepository.findByUserProfileAndResource(any(UserProfile.class), any(Resource.class))).thenReturn(Optional.of(resourcePermission));

        assertDoesNotThrow(() -> accessControlService.assignPermission(resource, mockUser, UserResourcePermissionTypes.EDITOR));
    }

    @Test
    @WithMockAuthUser(id = authUserId)
    void testAssignPermission_InvalidUser() {
        Resource resource = new Resource();

        when(resourcePermissionRepository.findByUserProfileAndResource(any(UserProfile.class), any(Resource.class))).thenReturn(Optional.empty());

        assertThrows(AccessDeniedException.class, () -> accessControlService.assignPermission(resource, mockUser, UserResourcePermissionTypes.EDITOR));
    }

    @Test
    @WithMockAuthUser(id = authUserId)
    void testInheritParentPermissions() {
        Resource parentResource = new Resource();
        Resource childResource = new Resource();
        ResourcePermission parentPermission = new ResourcePermission();
        parentPermission.setUserProfile(this.userProfile);
        parentPermission.setPermissions(UserResourcePermissionTypes.EDITOR);

        when(resourcePermissionRepository.findByResource(parentResource)).thenReturn(Collections.singletonList(parentPermission));

        accessControlService.inheritParentPermissions(childResource, parentResource);

        verify(resourcePermissionRepository, times(1)).save(any(ResourcePermission.class));
    }

    @Test
    @WithMockAuthUser(id = authUserId)
    void testRevokePermission() {
        UUID userId = UUID.randomUUID();
        UUID resourceId = UUID.randomUUID();

        accessControlService.revokePermission(userId, resourceId);

        verify(resourcePermissionRepository, times(1)).deleteByUserProfileIdAndResourceId(userId, resourceId);
    }
}

