package com.project.PlanFit.schd.entity;

import com.project.PlanFit.cmmn.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "SCHD_PTCPT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchdPtcpt extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SCHD_PTCPT_SN")
    private Long schdPtcptSn;

    @Column(name = "PTCPT_TYPE_NM")
    private String ptcptTypeNm;

    @Column(name = "PTCPT_NM")
    private String ptcptNm;

    @Column(name = "USE_YN")
    private String useYn;
}
