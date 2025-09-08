package com.project.PlanFit.food.repository;

import com.project.PlanFit.food.entity.FoodType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodTypeRepository extends JpaRepository<FoodType, Long> {

    List<FoodType> findByDelYn(String delYn);

}
