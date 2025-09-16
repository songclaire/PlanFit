package com.project.PlanFit.cmmn.mapper;

import com.project.PlanFit.comment.dto.CommentDto;
import com.project.PlanFit.comment.entity.Comment;
import com.project.PlanFit.user.dto.UserDto;
import com.project.PlanFit.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface MapStructConverter {

    /**
     * Comment
     */
    // DTO -> Entity
    Comment toEntity(CommentDto dto);
    // Entity -> DTO
    @Mappings({
            @Mapping(target = "fileId", ignore = true),
            @Mapping(target = "userName", ignore = true)
    })
    CommentDto toDto(Comment entity);

    /**
     * User
     */
    User toEntity(UserDto dto);
    UserDto toDto(User entity);

}
