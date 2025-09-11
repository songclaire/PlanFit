package com.project.PlanFit.comment.mapper;

import com.project.PlanFit.comment.dto.CommentDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CommentQueryMapper {

    List<CommentDto> findCommentsByMenu(@Param("menuType") String menuType, @Param("menuId") Long menuId);

}
