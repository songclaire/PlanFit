package com.project.PlanFit.comment.entity;

import com.project.PlanFit.cmmn.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "COMMENTS")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@SQLDelete(sql = "UPDATE COMMENTS SET DEL_YN = 'Y' WHERE COMMENT_ID = ?")
@Where(clause = "DEL_YN = 'N'")
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