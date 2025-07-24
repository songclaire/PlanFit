package com.project.PlanFit.schd.repository;

import com.project.PlanFit.schd.dto.SchdPtcptDto;
import com.project.PlanFit.schd.entity.SchdPtcpt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SchdPtcptRepository extends JpaRepository<SchdPtcpt, Long> {

    @Query("SELECT s FROM SchdPtcpt s WHERE s.useYn = 'Y'")
    List<SchdPtcpt> findByPtcptTypeNm();

}
