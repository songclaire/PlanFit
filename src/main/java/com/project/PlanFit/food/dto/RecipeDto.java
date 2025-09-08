package com.project.PlanFit.food.dto;

import com.project.PlanFit.cmmn.dto.BaseDto;
import com.project.PlanFit.food.entity.Recipe;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeDto extends BaseDto {

    private Long recipeSn;
    private Long foodTypeSn;
    private String recipeNm;
    private String ingredients;
    private String recipeCn;
    private Integer difficulty;
    private LocalDate recipeDate;
    private Long fileId;

    // 조회용 추가 필드
    private String foodTypeNm;

    private FoodTypeDto foodTypeInfo;
    private String typeNm;

    public static RecipeDto fromEntity(Recipe entity) {
        RecipeDto dto = new RecipeDto();
        dto.setRecipeSn(entity.getRecipeSn());
        dto.setFoodTypeSn(entity.getFoodTypeSn());
        dto.setRecipeNm(entity.getRecipeNm());
        dto.setIngredients(entity.getIngredients());
        dto.setRecipeCn(entity.getRecipeCn());
        dto.setDifficulty(entity.getDifficulty());
        dto.setRecipeDate(entity.getRecipeDate());
        dto.setFileId(entity.getFileId());

        if (entity.getFoodType() != null) {
            var ft = FoodTypeDto.fromEntity(entity.getFoodType());
            dto.setFoodTypeInfo(ft);
            dto.setTypeNm(ft.getTypeNm());
        }
        return dto;
    }

    public Recipe toEntity() {
        return Recipe.builder()
            .recipeSn(this.recipeSn)      // null이면 신규 INSERT, 값 있으면 UPDATE 후보
            .foodTypeSn(this.foodTypeSn)
            .recipeNm(this.recipeNm)
            .ingredients(this.ingredients)
            .recipeCn(this.recipeCn)
            .difficulty(this.difficulty)
            .recipeDate(this.recipeDate)
            .fileId(this.fileId)
            .build();
    }
}