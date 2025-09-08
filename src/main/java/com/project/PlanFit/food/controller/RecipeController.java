package com.project.PlanFit.food.controller;

import com.project.PlanFit.food.dto.RecipeDto;
import com.project.PlanFit.food.service.RecipeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recipe")
public class RecipeController {

    private final RecipeService recipeService;

    /**
     * 레시피 저장
     */
    @PostMapping("/save")
    public ResponseEntity<RecipeDto> saveRecipe(@RequestBody @Valid RecipeDto dto) {
        RecipeDto saved = recipeService.saveRecipe(dto);
        return ResponseEntity.ok(saved);
    }

    /**
     * 레시피 목록 조회
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping(name = "레시피 목록 조회", path = "/list")
    public List<RecipeDto> selectRecipeList(@RequestBody(required = false) RecipeDto dto) {
        return recipeService.selectRecipeList(dto != null ? dto : new RecipeDto());
    }

    /**
     * 레시피 상세정보 조회
     */
    @GetMapping(name = "레시피 상세정보 조회", path = "/detail")
    public RecipeDto selectRecipeDetail(@RequestParam("recipeSn") Long recipeSn) {
        return recipeService.selectRecipeDetail(recipeSn);
    }
}
