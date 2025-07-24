package com.project.PlanFit.schd.service;

import com.project.PlanFit.schd.dto.SchdDto;
import com.project.PlanFit.schd.dto.SchdTypeDto;
import com.project.PlanFit.schd.entity.Schd;
import com.project.PlanFit.schd.repository.SchdPtcptRepository;
import com.project.PlanFit.schd.repository.SchdRepository;
import com.project.PlanFit.schd.repository.SchdTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SchdService {

    public final SchdTypeRepository schdTypeRepository;
    public final SchdRepository schdRepository;
    public final SchdPtcptRepository schdPtcptRepository;

    private Predicate<Schd> getPredicate(SchdDto dto) {
        // 임시방편 조건
        return schd ->
                (dto.getSchdTypeSn() == null || schd.getSchdTypeSn().equals(dto.getSchdTypeSn())) &&
                (dto.getSchdDmnSn() == null || schd.getSchdDmnSn().equals(dto.getSchdDmnSn())) &&
                (dto.getSchdPtcptSn() == null || schd.getSchdPtcptSn().equals(dto.getSchdPtcptSn()));
    }

    public List<SchdTypeDto> selectSchdTypeList() {
        List<SchdTypeDto> list = schdTypeRepository.findByDelYn("N").stream()
                .map(type -> new SchdTypeDto(type.getSchdTypeSn(), type.getSchdTypeNm()))
                .collect(Collectors.toList());

        return list;
    }

    public void saveSchd(SchdDto dto) {
        // ✅ Optional<> : Optional을 사용하여 null 값을 간결하게 처리
//        Optional<SchdPtcpt> schdPtcptOpt = Optional.ofNullable(
//                schdPtcptRepository.findByPtcptTypeNm(dto.getPtcptTypeNm())
//        );

        // ✅ orElseGet : schdPtcptOpt 이 값이 null인 경우만 탐 -> SchdPtcpt에 새로 생성해주는 역할
//        SchdPtcpt schdPtcpt = schdPtcptOpt.orElseGet(() -> {
//            SchdPtcpt newSchdPtcpt = new SchdPtcpt();
//            newSchdPtcpt.setPtcptTypeNm(dto.getPtcptTypeNm());
//            newSchdPtcpt.setUseYn("Y");
//            return schdPtcptRepository.save(newSchdPtcpt);
//        });
        
        // 저장된 또는 찾은 ptcptSn 값 저장
//        Long ptcptSn = schdPtcpt.getSchdPtcptSn();

        // 일정 저장
        Schd schd = new Schd();
        schd.setSchdTypeSn(dto.getSchdTypeSn());
        schd.setSchdDmnSn(1L); // 일정관리
        schd.setSchdPtcptSn(dto.getSchdPtcptSn());
        schd.setSchdTtl(dto.getSchdTtl());
        schd.setStartDt(dto.getStartDt());
        schd.setEndDt(dto.getEndDt());
        schd.setSchdCn(dto.getSchdCn());
        schd.setLocation(dto.getLocation());
        // 🧙🏻‍♂️나중️🧙🏻‍♂️ 지역쪽 추가하기 (현재는 NULL로 들어감)

        schdRepository.save(schd);
    }

    /**
     * 일정 목록 조회
     */
    public List<SchdDto> selectSchdList(SchdDto dto) {
        return schdRepository.findAll().stream()
                .filter(getPredicate(dto))
                .map(SchdDto::fromEntity)
                .collect(Collectors.toList());
    }

}
