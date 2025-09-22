package com.project.PlanFit.role.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "role")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Integer roleId;

    @Column(name = "role_name", nullable = false, length = 50, unique = true)
    private String roleName; // "admin" / "user"

    // 공통 감사 필드
    @Column(name = "del_yn", length = 1)
    private String delYn; // 'N' 기본 권장

    @Column(name = "mdfcn_dt")
    private LocalDateTime mdfcnDt;

    @Column(name = "mdfr_id")
    private String mdfrId;

    @Column(name = "reg_dt")
    private LocalDateTime regDt;

    @Column(name = "reg_id")
    private String regId;

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
