package com.project.PlanFit.role.service;

import com.project.PlanFit.role.dto.RoleDto;
import com.project.PlanFit.role.entity.Role;
import com.project.PlanFit.role.repository.RoleRepository;
import com.project.PlanFit.role.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final UserRoleRepository userRoleRepository;

    @Transactional
    public List<String> findRoleNamesByUserId(String userId) {
        return userRoleRepository.findRoleNamesByUserId(userId);
    }
}
