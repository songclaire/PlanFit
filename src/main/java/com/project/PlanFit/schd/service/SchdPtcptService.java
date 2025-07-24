package com.project.PlanFit.schd.service;

import com.project.PlanFit.schd.dto.SchdPtcptDto;
import com.project.PlanFit.schd.repository.SchdPtcptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SchdPtcptService {

    public final SchdPtcptRepository schdPtcptRepository;

    public List<SchdPtcptDto> selectSchdPtcptList() {
        return schdPtcptRepository.findByPtcptTypeNm().stream()
                .map(e -> new SchdPtcptDto(e.getSchdPtcptSn(), e.getPtcptTypeNm(), e.getUseYn()))
                .collect(Collectors.toList());
    }
}
