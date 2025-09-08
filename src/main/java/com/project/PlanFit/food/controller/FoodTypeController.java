package com.project.PlanFit.food.controller;

import com.project.PlanFit.food.dto.FoodTypeDto;
import com.project.PlanFit.food.service.FoodTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/foodType")
public class FoodTypeController {

    private final FoodTypeService foodTypeService;

    /**
     * 음식종류 데이터 호출 (레시피 추가 모달)
     * @return
     */
    @GetMapping(path = "/list")
    public List<FoodTypeDto> selectFoodTypeList() {
        List<FoodTypeDto> list = foodTypeService.selectFoodTypeList();
        return list;
    }
}
