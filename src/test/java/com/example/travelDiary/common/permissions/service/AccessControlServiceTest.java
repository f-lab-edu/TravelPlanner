package com.example.travelDiary.common.permissions.service;

import com.example.travelDiary.common.auth.domain.AuthUser;
import com.example.travelDiary.common.auth.service.AuthUserService;
import com.example.travelDiary.common.permissions.domain.Resource;
import com.example.travelDiary.common.permissions.domain.ResourcePermission;
import com.example.travelDiary.common.permissions.domain.UserResourcePermissionTypes;
import com.example.travelDiary.common.permissions.repository.ResourcePermissionRepository;
import com.example.travelDiary.common.permissions.repository.ResourceRepository;
import com.example.travelDiary.domain.IdentifiableResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import java.nio.file.AccessDeniedException;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles({"test", "secretsLocal"})
public class AccessControlServiceTest {

//    @Test
//    void contextLoads() {
//        // Simple test to check if the context loads successfully
//    }

    @MockBean
    private ResourceRepository resourceRepository;

    @MockBean
    private ResourcePermissionRepository resourcePermissionRepository;

    @MockBean
    private AuthUserService authUserService;

    @Autowired
    private AccessControlService accessControlService;

    private AuthUser mockUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockUser = new AuthUser();
        mockUser.setId(UUID.randomUUID());
        mockUser.setUsername("testUser");

        // Mock authentication
        when(authUserService.getCurrentAuthenticatedUser()).thenReturn(mockUser);
    }

    @Test
    @WithMockUser(username = "testUser")
    void testHasPermission_PublicResource() {
        Resource resource = new Resource();
        resource.setVisibility("public");

        when(resourceRepository.findByResourceUUIDAndType(any(UUID.class), anyString())).thenReturn(Optional.of(resource));

        boolean result = accessControlService.hasPermission(IdentifiableResource.class, UUID.randomUUID(), "VIEW");
        assertTrue(result);
    }

    @Test
    @WithMockUser(username = "testUser")
    void testHasPermission_NoResource() {
        when(resourceRepository.findByResourceUUIDAndType(any(UUID.class), anyString())).thenReturn(Optional.empty());

        boolean result = accessControlService.hasPermission(IdentifiableResource.class, UUID.randomUUID(), "VIEW");
        assertFalse(result);
    }

    @Test
    @WithMockUser(username = "testUser")
    void testHasPermission_ValidPermission() {
        Resource resource = new Resource();
        ResourcePermission resourcePermission = new ResourcePermission();
        resourcePermission.setPermissions(UserResourcePermissionTypes.EDIT);

        when(resourceRepository.findByResourceUUIDAndType(any(UUID.class), anyString())).thenReturn(Optional.of(resource));
        when(resourcePermissionRepository.findByUserAndResource(any(AuthUser.class), any(Resource.class))).thenReturn(Optional.of(resourcePermission));

        boolean result = accessControlService.hasPermission(IdentifiableResource.class, UUID.randomUUID(), "EDIT");
        assertTrue(result);
    }

    @Test
    @WithMockUser(username = "testUser")
    void testHasPermission_InvalidPermission() {
        Resource resource = new Resource();
        ResourcePermission resourcePermission = new ResourcePermission();
        resourcePermission.setPermissions(UserResourcePermissionTypes.VIEW);

        when(resourceRepository.findByResourceUUIDAndType(any(UUID.class), anyString())).thenReturn(Optional.of(resource));
        when(resourcePermissionRepository.findByUserAndResource(any(AuthUser.class), any(Resource.class))).thenReturn(Optional.of(resourcePermission));

        boolean result = accessControlService.hasPermission(IdentifiableResource.class, UUID.randomUUID(), "EDIT");
        assertFalse(result);
    }

    @Test
    @WithMockUser(username = "testUser")
    void testAssignPermission_ValidUser() {
        Resource resource = new Resource();
        ResourcePermission resourcePermission = new ResourcePermission();
        resourcePermission.setPermissions(UserResourcePermissionTypes.OWNER);

        when(resourcePermissionRepository.findByUserAndResource(any(AuthUser.class), any(Resource.class))).thenReturn(Optional.of(resourcePermission));

        assertDoesNotThrow(() -> accessControlService.assignPermission(resource, mockUser, UserResourcePermissionTypes.EDIT));
    }

    @Test
    @WithMockUser(username = "testUser")
    void testAssignPermission_InvalidUser() {
        Resource resource = new Resource();

        when(resourcePermissionRepository.findByUserAndResource(any(AuthUser.class), any(Resource.class))).thenReturn(Optional.empty());

        assertThrows(AccessDeniedException.class, () -> accessControlService.assignPermission(resource, mockUser, UserResourcePermissionTypes.EDIT));
    }

    @Test
    @WithMockUser(username = "testUser")
    void testInheritParentPermissions() {
        Resource parentResource = new Resource();
        Resource childResource = new Resource();
        ResourcePermission parentPermission = new ResourcePermission();
        parentPermission.setUser(mockUser);
        parentPermission.setPermissions(UserResourcePermissionTypes.EDIT);

        when(resourcePermissionRepository.findByResource(parentResource)).thenReturn(Collections.singletonList(parentPermission));

        accessControlService.inheritParentPermissions(childResource, parentResource);

        verify(resourcePermissionRepository, times(1)).save(any(ResourcePermission.class));
    }

    @Test
    @WithMockUser(username = "testUser")
    void testRevokePermission() {
        UUID userId = UUID.randomUUID();
        UUID resourceId = UUID.randomUUID();

        accessControlService.revokePermission(userId, resourceId);

        verify(resourcePermissionRepository, times(1)).deleteByUserIdAndResourceId(userId, resourceId);
    }
}

