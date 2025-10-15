package com.project.PlanFit.restaurant.dto;

import com.project.PlanFit.cmmn.dto.BaseDto;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class MenuDto extends BaseDto {
    private Long menuSn;         // 메뉴일련번호
    private Long restaurantSn;   // 맛집일련번호

    private String menuNm;       // 메뉴명
    private BigDecimal price;    // 가격
    private String rcmdYn;       // 추천여부
    private Long fileId;         // 파일아이디

}
