package com.project.PlanFit.comment.controller;

import com.project.PlanFit.comment.dto.CommentDto;
import com.project.PlanFit.comment.service.CommentService;
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
@RequestMapping("/api/comment")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/recipe/save")
    public ResponseEntity<CommentDto> saveComment(@RequestBody @Valid CommentDto dto) {
        CommentDto saved = commentService.saveComment(dto);
        return ResponseEntity.ok(saved);
    }

}
