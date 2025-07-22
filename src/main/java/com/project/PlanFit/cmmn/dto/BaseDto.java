package com.project.PlanFit.cmmn.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public abstract class BaseDto {

    private LocalDateTime regDt;
    private String regId;

    private LocalDateTime mdfcnDt;
    private String mdfrId;

    private String delYn;

    // toEntity 자동 변환 메서드 들어가도 좋음.
}
