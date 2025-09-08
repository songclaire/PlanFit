package com.project.PlanFit.comment.dto;

import com.project.PlanFit.cmmn.dto.BaseDto;
import com.project.PlanFit.comment.entity.Comment;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDto extends BaseDto {
    private Long commentId;
    private String menuType;
    private Long menuId;
    private Long parentId;
    private String commentCn;

    public static CommentDto fromEntity(Comment entity) {
        CommentDto dto = new CommentDto();
        dto.setCommentId(entity.getCommentId());
        dto.setMenuType(entity.getMenuType());
        dto.setMenuId(entity.getMenuId());
        dto.setParentId(entity.getParentId());
        dto.setCommentCn(entity.getCommentCn());
        return dto;
    }
}