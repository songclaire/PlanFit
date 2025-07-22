package com.project.PlanFit.schd.entity;

import com.project.PlanFit.cmmn.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "SCHD_DMN")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchdDmn extends BaseEntity {

    @Id
    @Column(name = "SCHD_DMN_SN")
    private Long schdDmnSn;

    @Column(name = "SCHD_DMN_NM")
    private String schdDmnNm;

    @Column(name = "SCHD_DMN_EXPLN")
    private String schdDmnExpln;
}
