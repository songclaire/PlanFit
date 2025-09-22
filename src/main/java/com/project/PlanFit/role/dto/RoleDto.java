package com.project.PlanFit.role.dto;

import com.project.PlanFit.role.entity.Role;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class RoleDto {
    private Integer roleId;
    private String roleName;

    private String delYn;
    private LocalDateTime mdfcnDt;
    private String mdfrId;
    private LocalDateTime regDt;
    private String regId;

    public static RoleDto fromEntity(Role role) {
        if (role == null) return null;
        return RoleDto.builder()
                .roleId(role.getRoleId())
                .roleName(role.getRoleName())
                .delYn(role.getDelYn())
                .mdfcnDt(role.getMdfcnDt())
                .mdfrId(role.getMdfrId())
                .regDt(role.getRegDt())
                .regId(role.getRegId())
                .build();
    }
}

