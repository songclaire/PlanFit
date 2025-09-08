package com.project.PlanFit.food.entity;

import com.project.PlanFit.cmmn.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "RECIPE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recipe extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RECIPE_SN")
    private Long recipeSn;

    @Column(name = "FOOD_TYPE_SN")
    private Long foodTypeSn;

    @Column(name = "RECIPE_NM")
    private String recipeNm;

    @Column(name = "INGREDIENTS")
    private String ingredients;

    @Column(name = "RECIPE_CN")
    private String recipeCn;

    @Column(name = "DIFFICULTY")
    private Integer difficulty;

    @Column(name = "RECIPE_DATE")
    private LocalDate recipeDate;

    @Column(name = "FILE_ID")
    private Long fileId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FOOD_TYPE_SN", insertable = false, updatable = false)
    private FoodType foodType;
}