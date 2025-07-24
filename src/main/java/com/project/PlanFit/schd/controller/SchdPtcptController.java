package com.project.PlanFit.schd.controller;

import com.project.PlanFit.schd.dto.SchdPtcptDto;
import com.project.PlanFit.schd.dto.SchdTypeDto;
import com.project.PlanFit.schd.service.SchdPtcptService;
import com.project.PlanFit.schd.service.SchdService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SchdPtcptController {

    public final SchdPtcptService schdPtcptService;

    /**
     * 일정 참여자 리스트
     */
    @GetMapping(name = "일정종류 리스트 출력", path = "/schdPtcptList")
    public List<SchdPtcptDto> selectSchdPtcptList() {
        List<SchdPtcptDto> list = schdPtcptService.selectSchdPtcptList();
        return list;
    }

}
