package com.project.PlanFit.schd.dto;

import com.project.PlanFit.cmmn.dto.BaseDto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchdDmnDto extends BaseDto {
    private Long schdDmnSn;
    private String schdDmnNm;
    private String schdDmnExpln;
}
