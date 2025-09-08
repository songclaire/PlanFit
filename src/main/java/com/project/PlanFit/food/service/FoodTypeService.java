package com.project.PlanFit.food.service;

import com.project.PlanFit.food.dto.FoodTypeDto;
import com.project.PlanFit.food.repository.FoodTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FoodTypeService {

    public final FoodTypeRepository foodTypeRepository;

    /**
     * 음식종류 데이터 호출 (레시피 추가 모달)
     * @return
     */
    @Transactional
    public List<FoodTypeDto> selectFoodTypeList() {
        List<FoodTypeDto> list = foodTypeRepository.findByDelYn("N").stream()
                .map(type -> new FoodTypeDto(type.getFoodTypeSn(), type.getTypeNm()))
                .collect(Collectors.toList());

        return list;
    }
}
