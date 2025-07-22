package com.project.PlanFit.schd.entity;

import com.project.PlanFit.cmmn.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "SCHD")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Schd extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SCHD_SN")
    private Long schdSn;

    @Column(name = "SCHD_TYPE_SN")
    private Long schdTypeSn;

    @Column(name = "SCHD_DMN_SN")
    private Long schdDmnSn;

    @Column(name = "SCHD_PTCPT_SN")
    private Long schdPtcptSn;

    @Column(name = "RGN_PROVC_SN")
    private Long rgnProvcSn;

    @Column(name = "RGN_CITY_SN")
    private Long rgnCitySn;

    @Column(name = "SCHD_TTL")
    private String schdTtl;

    @Column(name = "START_DT")
    private LocalDate startDt;

    @Column(name = "END_DT")
    private LocalDate endDt;

    @Column(name = "SCHD_CN")
    private String schdCn;

    @Column(name = "LOCATION")
    private String location;
}
