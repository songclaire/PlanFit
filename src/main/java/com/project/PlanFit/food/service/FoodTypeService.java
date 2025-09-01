package com.project.PlanFit.food.service;

import com.project.PlanFit.food.dto.RecipeDto;
import com.project.PlanFit.food.entity.Recipe;
import com.project.PlanFit.food.repository.RecipeRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;

    @Transactional
    public RecipeDto saveRecipe(@Valid RecipeDto dto) {
        Recipe saved = recipeRepository.save(dto.toEntity());
        return RecipeDto.fromEntity(saved);
    }
}
