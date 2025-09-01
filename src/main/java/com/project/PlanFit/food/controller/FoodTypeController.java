package com.project.PlanFit.food.controller;

import com.project.PlanFit.food.dto.RecipeDto;
import com.project.PlanFit.food.service.RecipeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recipe")
public class RecipeController {

    private final RecipeService recipeService;

    @PostMapping("/save")
    public ResponseEntity<RecipeDto> saveRecipe(@RequestBody @Valid RecipeDto dto) {
        RecipeDto saved = recipeService.saveRecipe(dto);
        return ResponseEntity.ok(saved);
    }

}
