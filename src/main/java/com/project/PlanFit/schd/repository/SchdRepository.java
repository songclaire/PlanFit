package com.project.PlanFit.schd.repository;

import com.project.PlanFit.schd.dto.SchdDto;
import com.project.PlanFit.schd.entity.Schd;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SchdRepository extends JpaRepository<Schd, Long> {

    List<Schd> findAll();
}
