package com.project.PlanFit.food.repository;

import com.project.PlanFit.food.entity.Recipe;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long>, RecipeRepositorySupport {

    @EntityGraph(attributePaths = "foodType") // ← fetch join 효과
    List<Recipe> findAll();

//    @EntityGraph(attributePaths = "foodType")
//    List<Recipe> findAll(Specification<Recipe> spec, Sort sort);

}
