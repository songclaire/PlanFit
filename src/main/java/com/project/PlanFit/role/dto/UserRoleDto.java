package com.project.PlanFit.role.dto;

import com.project.PlanFit.role.entity.UserRole;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class UserRoleDto {

    private String  userId;
    private Integer roleId;

    private String delYn;
    private LocalDateTime mdfcnDt;
    private String mdfrId;
    private LocalDateTime regDt;
    private String regId;

    // 엔티티 → DTO
    public static UserRoleDto fromEntity(UserRole ur) {
        if (ur == null) return null;
        return UserRoleDto.builder()
                .userId(ur.getUserId())
                .roleId(ur.getRoleId())
                .delYn(ur.getDelYn())
                .mdfcnDt(ur.getMdfcnDt())
                .mdfrId(ur.getMdfrId())
                .regDt(ur.getRegDt())
                .regId(ur.getRegId())
                .build();
    }

    // (선택) DTO → 엔티티
    public UserRole toEntity() {
        return UserRole.builder()
                .userId(userId)
                .roleId(roleId)
                .delYn(delYn)
                .mdfcnDt(mdfcnDt)
                .mdfrId(mdfrId)
                .regDt(regDt)
                .regId(regId)
                .build();
    }
}
