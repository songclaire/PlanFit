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

    @Column(name = "FILE_ID")
    private Long fileId;

    @Builder
    public User(String userId, String userName, String password, String email, LocalDate birthDate,
                String telNo, String roadNmAddr, String roadNmDaddr, LocalDateTime joinDt, LocalDateTime whdwlDt, Long fileId) {
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
        this.fileId = fileId;
    }

    // User.java 내부
    public void patchProfile( String userName, String email, String telNo, String roadNmAddr, String roadNmDaddr, Long fileId ) {
        // 간단 스펙: 문자열은 null이면 무시(기존값 유지), 값 있으면 trim 후 반영
        if (userName != null)     this.userName   = userName.trim();
        if (email != null)        this.email      = email.trim();
        if (telNo != null)        this.telNo      = telNo.trim();
        if (roadNmAddr != null)   this.roadNmAddr = roadNmAddr.trim();
        if (roadNmDaddr != null)  this.roadNmDaddr= roadNmDaddr.trim();

        // 이미지: null이면 삭제(=프로필 사진 제거), 값 있으면 교체
        // 즉, 파일만 'null=지우기' 규칙, 나머지는 'null=무시' 규칙
        if (fileId != this.fileId) {
            this.fileId = fileId;
        }
    }


}
