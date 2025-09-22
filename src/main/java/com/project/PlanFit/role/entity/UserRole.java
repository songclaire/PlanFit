package com.project.PlanFit.role.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_role")
@IdClass(UserRoleId.class)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class UserRole {

    @Id
    @Column(name = "user_id", nullable = false, length = 255)
    private String userId;

    @Id
    @Column(name = "role_id", nullable = false)
    private Integer roleId;

    // 공통 감사 필드
    @Column(name = "del_yn", length = 1)
    private String delYn;

    @Column(name = "mdfcn_dt")
    private LocalDateTime mdfcnDt;

    @Column(name = "mdfr_id")
    private String mdfrId;

    @Column(name = "reg_dt")
    private LocalDateTime regDt;

    @Column(name = "reg_id")
    private String regId;

    // (선택) Role 엔티티 연관관계가 필요하면 주석 해제
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "role_id", insertable = false, updatable = false)
    // private Role role;

    @PrePersist
    public void prePersist() {
        if (delYn == null) delYn = "N";
        if (regDt == null) regDt = LocalDateTime.now();
        if (mdfcnDt == null) mdfcnDt = regDt;
    }

    @PreUpdate
    public void preUpdate() {
        mdfcnDt = LocalDateTime.now();
    }
}
