package com.project.PlanFit.food.entity;

import com.project.PlanFit.cmmn.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "FOOD_TYPE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodType extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FOOD_TYPE_SN")
    private Long foodTypeSn;

    @Column(name = "TYPE_NM")
    private String typeNm;
}