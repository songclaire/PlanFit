package com.project.PlanFit.schd.repository;

import com.project.PlanFit.schd.entity.Schd;
import com.project.PlanFit.schd.entity.SchdPtcpt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SchdPtcptRepository extends JpaRepository<SchdPtcpt, Long> {

    SchdPtcpt findByPtcptTypeNmAndPtcptNm(String ptcptTypeNm, String ptcptNm);

}
