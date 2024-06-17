//package com.example.travelDiary.common.permissions.service;
//
//import com.example.travelDiary.common.auth.domain.AuthUser;
//import com.example.travelDiary.common.auth.domain.PrincipalDetails;
//import com.example.travelDiary.common.permissions.aop.CheckAccess;
//import com.example.travelDiary.common.permissions.domain.Resource;
//import com.example.travelDiary.common.permissions.domain.UserResourcePermissionTypes;
//import com.example.travelDiary.common.permissions.domain.exception.ResourceNotFoundException;
//import com.example.travelDiary.common.permissions.dto.ResourcePermissionUpdateRequest;
//import com.example.travelDiary.common.permissions.repository.ResourcePermissionRepository;
//import com.example.travelDiary.common.permissions.repository.ResourceRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.access.prepost.PostAuthorize;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Service;
//
//import java.time.Instant;
//import java.util.UUID;
//
//@Service
//public class ResourceService {
//    private final AccessControlService accessControlService;
//    private final ResourceRepository resourceRepository;
//    private final ResourcePermissionRepository resourcePermissionRepository;
//
//    @Autowired
//    public ResourceService(AccessControlService accessControlService, ResourceRepository resourceRepository, ResourcePermissionRepository resourcePermissionRepository) {
//        this.accessControlService = accessControlService;
//        this.resourceRepository = resourceRepository;
//        this.resourcePermissionRepository = resourcePermissionRepository;
//    }
//
//    @PreAuthorize("hasPermission(#resourceId, 'VIEW')")
//    @CheckAccess(resourceType = "Resource", resourceId = "#resourceId", permissionType = UserResourcePermissionTypes.VIEW)
//    public Resource getResource(UUID resourceId) {
//        return resourceRepository.findById(resourceId)
//                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
//    }
//
//    @PreAuthorize("hasPermission(#resourceId, 'EDIT')")
//    @CheckAccess(resourceType = "Resource", resourceId = "#resourceId", permissionType = UserResourcePermissionTypes.EDIT)
//    public void updateResource(UUID resourceId, ResourcePermissionUpdateRequest updateRequest) {
//        Resource resource = resourceRepository.findById(resourceId)
//                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
//        // Perform update logic here
//        resourceRepository.save(resource);
//    }
//
//    @PreAuthorize("hasPermission(#resourceId, 'CLONE')")
//    @CheckAccess(resourceType = "Resource", resourceId = "#resourceId", permissionType = UserResourcePermissionTypes.CLONE)
//    public Resource cloneResource(UUID resourceId) {
//        Resource originalResource = resourceRepository.findById(resourceId)
//                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
//        // Perform cloning logic here
//        Resource clonedResource = new Resource();
//        clonedResource.setVisibility(originalResource.getVisibility());
//        clonedResource.setResourceUUID(UUID.randomUUID());
//        clonedResource.setType(originalResource.getType());
//        clonedResource.setCreateTime(Instant.now());
//        resourceRepository.save(clonedResource);
//
//        AuthUser currentUser = getCurrentUser();
//        accessControlService.assignOwnerPermission(clonedResource, currentUser);
//
//        return clonedResource;
//    }
//
//    private AuthUser getCurrentUser() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        return ((PrincipalDetails) authentication.getPrincipal()).getUser();
//    }
//}
