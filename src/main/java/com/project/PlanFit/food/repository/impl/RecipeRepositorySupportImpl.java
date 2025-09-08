package com.project.PlanFit.food.repository.impl;

import com.project.PlanFit.food.dto.RecipeDto;
import com.project.PlanFit.food.entity.QFoodType;
import com.project.PlanFit.food.entity.QRecipe;
import com.project.PlanFit.food.repository.RecipeRepositorySupport;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.query.JpaQueryMethodFactory;
import org.springframework.expression.spel.ast.Projection;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class RecipeRepositorySupportImpl implements RecipeRepositorySupport {

    // 스프링이 JPA의 핵심 객체(EntityManager)를 넣어줌
    @PersistenceContext
    private EntityManager em;

    // 필요할 때 즉석에서 JPAQueryFactory 생성 (별도 @Bean 불필요)
    private JPAQueryFactory qf() {
        return new JPAQueryFactory(em);
    }

    @Override
    public List<RecipeDto> selectRecipeList(RecipeDto dto) {
        QRecipe qRecipe = QRecipe.recipe;
        QFoodType qFoodType = QFoodType.foodType;

        BooleanBuilder where = new BooleanBuilder();

        if (dto != null) {
            if (dto.getFoodTypeSn() != null) where.and(qRecipe.foodTypeSn.eq(dto.getFoodTypeSn()));
            if (dto.getDifficulty() != null)  where.and(qRecipe.difficulty.eq(dto.getDifficulty()));
            if (dto.getRecipeNm() != null && !dto.getRecipeNm().isBlank())
                where.and(qRecipe.recipeNm.containsIgnoreCase(dto.getRecipeNm().trim()));
            if (dto.getIngredients() != null && !dto.getIngredients().isBlank())
                where.and(qRecipe.ingredients.containsIgnoreCase(dto.getIngredients().trim()));
        }

        return qf()
                .select(Projections.fields(RecipeDto.class,
                        qRecipe.recipeSn,
                        qRecipe.foodTypeSn,
                        qRecipe.recipeNm,
                        qRecipe.ingredients,
                        qRecipe.recipeCn,
                        qRecipe.difficulty,
                        qRecipe.recipeDate,
                        qRecipe.fileId,
                        qFoodType.typeNm.as("typeNm")
                        ))
                .from(qRecipe)
                .leftJoin(qRecipe.foodType, qFoodType)
                .where(where)
                .orderBy(qRecipe.recipeDate.desc(), qRecipe.recipeSn.desc())
                .fetch();
    }
}
