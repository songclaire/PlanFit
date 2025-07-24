package com.project.PlanFit.schd.dto;

import com.project.PlanFit.cmmn.dto.BaseDto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchdPtcptDto extends BaseDto {

    private Long schdPtcptSn;
    private String ptcptTypeNm;
    private String useYn;

}
