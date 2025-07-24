package com.project.PlanFit.schd.controller;

import com.project.PlanFit.schd.dto.SchdDto;
import com.project.PlanFit.schd.dto.SchdTypeDto;
import com.project.PlanFit.schd.entity.Schd;
import com.project.PlanFit.schd.service.SchdService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SchdController {

    public final SchdService schdService;

    /**
     * 일정종류 리스트 출력
     */
    @GetMapping(name = "일정종류 리스트 출력", path = "/schdTypeList")
    public List<SchdTypeDto> selectSchdTypeList() {
        List<SchdTypeDto> list = schdService.selectSchdTypeList();
        return list;
    }

    /**
     * 일정 저장
     */
    @PostMapping(name = "일정 저장", path = "/saveSchd")
    public ResponseEntity<?> saveSchd(@RequestBody SchdDto dto) {
        schdService.saveSchd(dto);
        return ResponseEntity.ok().build();
    }

    /**
     * 일정 목록 조회
     */
    @PostMapping(name = "일정 목록 조회", path = "/schdList")
    public List<SchdDto> selectSchdList(@RequestBody SchdDto dto) {
        List<SchdDto> list = schdService.selectSchdList(dto);
        return list;
    }
}
