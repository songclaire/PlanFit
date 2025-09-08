package com.project.PlanFit.food.dto;

import com.project.PlanFit.cmmn.dto.BaseDto;
import com.project.PlanFit.food.entity.FoodType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodTypeDto extends BaseDto {
    private Long foodTypeSn;
    private String typeNm;

    public static FoodTypeDto fromEntity(FoodType entity) {
        FoodTypeDto dto = new FoodTypeDto();
        dto.setFoodTypeSn(entity.getFoodTypeSn());
        dto.setTypeNm(entity.getTypeNm());
        return dto;
    }
}