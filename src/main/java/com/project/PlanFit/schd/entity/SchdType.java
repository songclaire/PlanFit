package com.project.PlanFit.schd.entity;

import com.project.PlanFit.cmmn.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "SCHD_TYPE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchdType extends BaseEntity {
    @Id
    @Column(name = "SCHD_TYPE_SN")
    private Long schdTypeSn;

    @Column(name = "SCHD_TYPE_NM")
    private String schdTypeNm;
}
