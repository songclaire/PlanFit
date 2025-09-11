package com.project.PlanFit.comment.service;

import com.project.PlanFit.cmmn.mapper.MapStructConverter;
import com.project.PlanFit.comment.dto.CommentDto;
import com.project.PlanFit.comment.entity.Comment;
import com.project.PlanFit.comment.mapper.CommentQueryMapper;
import com.project.PlanFit.comment.repository.CommentRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;

    private final MapStructConverter commentMapper;

    private final CommentQueryMapper commentQueryMapper;

    /**
     * 댓글 저장
     */
    @Transactional
    public CommentDto saveComment(@Valid CommentDto dto) {

        // 매퍼를 통해 엔티티로 변환
        Comment entity = commentMapper.toEntity(dto);
        // JPA 저장
        Comment saved = commentRepository.save(entity);

        return commentMapper.toDto(saved);
    }

    /**
     * 댓글, 대댓글 조회
     */
    public List<CommentDto> selectCommentList(String menuType, Long menuId) {
        try {
            List<CommentDto> list = commentQueryMapper.findCommentsByMenu(menuType, menuId);
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 댓글, 대댓글 수정
     */
    @Transactional
    public CommentDto updateComment(@Valid CommentDto dto) {
        log.info(">>> updateComment called with " + dto.getCommentId());

        Comment commentInfo = commentRepository.findById(dto.getCommentId())
                        .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));
        commentInfo.setCommentCn(dto.getCommentCn());

        Comment saved = commentRepository.save(commentInfo);
        return commentMapper.toDto(saved);
    }

    /**
     * 댓글, 대댓글 삭제
     */
    @Transactional
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }
}
