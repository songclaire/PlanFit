package com.project.PlanFit.food.entity;

import com.project.PlanFit.cmmn.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "MENU")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Menu extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MENU_SN")
    private Long menuSn;

    @Column(name = "RESTAURANT_SN", nullable = false)
    private Long restaurantSn;

    @Column(name = "MENU_NM", nullable = false)
    private String menuNm;

    @Column(name = "PRICE")
    private BigDecimal price;

    @Column(name = "RCMD_YN")
    private String rcmdYn;

    @Column(name = "FILE_ID")
    private Long fileId;

}
