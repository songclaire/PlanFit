package com.project.PlanFit.schd.dto;

import com.project.PlanFit.cmmn.dto.BaseDto;
import com.project.PlanFit.schd.entity.Schd;
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

    private String color;
    private String schdTtl;
    private LocalDate startDt;
    private LocalDate endDt;
    private String schdCn;
    private String location;

    private String ptcptTypeNm;

    public static SchdDto fromEntity(Schd entity) {
        SchdDto dto = new SchdDto();
        dto.setSchdSn(entity.getSchdSn());
        dto.setColor(entity.getColor());
        dto.setSchdTtl(entity.getSchdTtl());
        dto.setStartDt(entity.getStartDt());
        dto.setEndDt(entity.getEndDt());
        dto.setSchdTypeSn(entity.getSchdTypeSn());
        dto.setSchdPtcptSn(entity.getSchdPtcptSn());
        dto.setSchdCn(entity.getSchdCn());
        dto.setLocation(entity.getLocation());
        return dto;
    }
}
