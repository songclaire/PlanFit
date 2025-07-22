package com.project.PlanFit.schd.dto;

import com.project.PlanFit.cmmn.dto.BaseDto;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchdDto extends BaseDto {
    private Long schdSn;

    private Long schdTypeSn;
    private Long schdDmnSn;
    private Long schdPtcptSn;

    private Long rgnProvcSn;
    private Long rgnCitySn;

    private String schdTtl;
    private LocalDate startDt;
    private LocalDate endDt;
    private String schdCn;
    private String location;

    private String ptcptTypeNm;
    private String ptcptNm;
}
