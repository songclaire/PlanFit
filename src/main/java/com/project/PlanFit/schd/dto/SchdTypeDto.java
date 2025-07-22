package com.project.PlanFit.schd.dto;

import com.project.PlanFit.cmmn.dto.BaseDto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchdTypeDto extends BaseDto {

    private Long schdTypeSn;
    private String schdTypeNm;
}
