package com.project.PlanFit.cmmn.dto;

import com.project.PlanFit.cmmn.entity.Comments;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentsDto extends BaseDto {
    private Long commentId;
    private String menuType;
    private Long menuId;
    private Long parentId;
    private String commentCn;

    public static CommentsDto fromEntity(Comments entity) {
        CommentsDto dto = new CommentsDto();
        dto.setCommentId(entity.getCommentId());
        dto.setMenuType(entity.getMenuType());
        dto.setMenuId(entity.getMenuId());
        dto.setParentId(entity.getParentId());
        dto.setCommentCn(entity.getCommentCn());
        return dto;
    }
}