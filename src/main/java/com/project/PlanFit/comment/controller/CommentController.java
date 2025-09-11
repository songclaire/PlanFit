package com.project.PlanFit.comment.controller;

import com.project.PlanFit.comment.dto.CommentDto;
import com.project.PlanFit.comment.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comment")
public class CommentController {

    private final CommentService commentService;

    /**
     * 댓글 저장
     * @param dto
     * @return
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/save")
    public ResponseEntity<CommentDto> saveComment(@RequestBody @Valid CommentDto dto) {
        CommentDto saved = commentService.saveComment(dto);
        return ResponseEntity.ok(saved);
    }

    /**
     * 댓글 조회
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/list")
    public List<CommentDto> selectCommentList(@RequestParam("menuType") String menuType, @RequestParam("menuId") Long menuId) {
        List<CommentDto> list = commentService.selectCommentList(menuType, menuId);
        return list;
    }

    /**
     * 댓글 수정
     */
    @PostMapping("/update")
    public CommentDto updateComment(@RequestBody @Valid CommentDto dto) {
        CommentDto updated = commentService.updateComment(dto);
        return updated;
    }

    /**
     * 댓글 삭제
     */
    @DeleteMapping("/delete")
    public void deleteComment(@RequestParam("commentId") Long commentId) {
        commentService.deleteComment(commentId);
    }

}
