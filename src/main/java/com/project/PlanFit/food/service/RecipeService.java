package com.project.PlanFit.food.service;

import com.project.PlanFit.file.repository.FileAtmtRepository;
import com.project.PlanFit.food.dto.RecipeDto;
import com.project.PlanFit.food.entity.Recipe;
import com.project.PlanFit.food.repository.RecipeRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final FileAtmtRepository fileAtmtRepository;

    /**
     * 레시피 저장
     */
    @Transactional
    public RecipeDto saveRecipe(@Valid RecipeDto dto) {
        Recipe saved = recipeRepository.save(dto.toEntity());
        fileAtmtRepository.updateTmprStrgYn(saved.getFileId());
        return RecipeDto.fromEntity(saved);
    }

    /**
     * 레시피 목록 조회
     */
    @Transactional
    public List<RecipeDto> selectRecipeList(RecipeDto dto) {
        return recipeRepository.selectRecipeList(dto);
    }

    /**
     * 레시피 상세정보 조회
     */
    @Transactional
    public RecipeDto selectRecipeDetail(Long recipeSn) {
        Recipe recipe = recipeRepository.findById(recipeSn)
                .orElseThrow(() -> new IllegalArgumentException("레시피 없음"));
        return RecipeDto.fromEntity(recipe);
    }
}
