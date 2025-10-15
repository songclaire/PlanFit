package com.project.PlanFit.restaurant.entity;

import com.project.PlanFit.cmmn.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "RESTAURANT")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Restaurant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RESTAURANT_SN")
    private Long restaurantSn;

    @Column(name = "FOOD_TYPE_SN", nullable = false)
    private Long foodTypeSn;

    @Column(name = "RGN_PROVC_SN", nullable = false)
    private Long rgnProvcSn;

    @Column(name = "ADDR")
    private String addr;

    @Column(name = "RTRT_NM", nullable = false)
    private String rtrtNm;

    @Column(name = "ZIP_CODE")
    private String zipCode;

    @Column(name = "ADDR_ROAD")
    private String addrRoad;

    @Column(name = "ADDR_DETAIL")
    private String addrDetail;

    @Column(name = "FILE_ID")
    private Long fileId;

    @Column(precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(precision = 10, scale = 7) 
    private BigDecimal longitude;

    @Column(name = "KAKAO_PLACE_ID", length = 64)
    private String kakaoPlaceId;

    @Column(name = "RATING")
    private Integer rating;

    @Column(name = "MEMO")
    private String memo;
}
