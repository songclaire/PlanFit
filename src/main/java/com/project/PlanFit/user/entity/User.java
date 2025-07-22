package com.project.PlanFit.user.entity;

import com.project.PlanFit.cmmn.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.persistence.Id;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "USER")
public class User extends BaseEntity {

    @Id
    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "USER_NAME")
    private String userName;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "BRDT")
    private LocalDate birthDate;

    @Column(name = "TELNO")
    private String telNo;

    @Column(name = "ROAD_NM_ADDR")
    private String roadNmAddr;

    @Column(name = "ROAD_NM_DADDR")
    private String roadNmDaddr;

    @Column(name = "JOIN_DT")
    private LocalDateTime joinDt;

    @Column(name = "WHDWL_DT")
    private LocalDateTime whdwlDt;

    @Builder
    public User(String userId, String userName, String password, String email, LocalDate birthDate,
                String telNo, String roadNmAddr, String roadNmDaddr, LocalDateTime joinDt, LocalDateTime whdwlDt) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.birthDate = birthDate;
        this.telNo = telNo;
        this.roadNmAddr = roadNmAddr;
        this.roadNmDaddr = roadNmDaddr;
        this.joinDt = joinDt;
        this.whdwlDt = whdwlDt;
    }

}
