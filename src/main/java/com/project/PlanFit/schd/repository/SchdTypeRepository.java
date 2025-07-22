package com.project.PlanFit.schd.repository;

import com.project.PlanFit.schd.entity.SchdType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SchdTypeRepository extends JpaRepository<SchdType, Long> {

    List<SchdType> findByDelYn(String delYn);
}
