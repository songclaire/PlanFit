package com.project.PlanFit.cmmn.mapper;

import com.project.PlanFit.comment.dto.CommentDto;
import com.project.PlanFit.comment.entity.Comment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MapStructConverter {

    /**
     * Comment
     */
    // DTO -> Entity
    Comment toEntity(CommentDto dto);
    // Entity -> DTO
    CommentDto toDto(Comment entity);

}
