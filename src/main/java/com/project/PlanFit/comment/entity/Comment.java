package com.project.PlanFit.comment.entity;

import com.project.PlanFit.cmmn.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "COMMENTS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COMMENT_ID")
    private Long commentId;

    @Column(name = "MENU_TYPE")
    private String menuType;

    @Column(name = "MENU_ID")
    private Long menuId;

    @Column(name = "PARENT_ID")
    private Long parentId;

    @Column(name = "COMMENT_CN")
    private String commentCn;
}