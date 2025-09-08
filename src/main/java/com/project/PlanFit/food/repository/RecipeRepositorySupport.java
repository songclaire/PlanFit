package com.project.PlanFit.food.repository;

import com.project.PlanFit.food.dto.RecipeDto;

import java.util.List;

public interface RecipeRepositorySupport {

    List<RecipeDto> selectRecipeList(RecipeDto dto);

}
