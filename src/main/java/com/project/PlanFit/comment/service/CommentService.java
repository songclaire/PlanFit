package com.project.PlanFit.comment.service;

import com.project.PlanFit.comment.dto.CommentDto;
import com.project.PlanFit.comment.repository.CommentRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    /**
     * 댓글 저장
     */
    @Transactional
    public CommentDto saveComment(@Valid CommentDto dto) {

    }
}
