package com.project.PlanFit.cmmn.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "FILE_ATMT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileAtmt extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FILE_ID")
    private Long fileId;

    @Column(name = "ORIGIN_FILE_NM")
    private String originFileNm;

    @Column(name = "STORED_FILE_NM")
    private String storedFileNm;

    @Column(name = "FILE_SIZE")
    private Long fileSize;

    @Column(name = "FILE_EXTENSION")
    private String fileExtension;

    @Column(name = "FILE_PATH")
    private String filePath;

    @Column(name = "MENU_TYPE")
    private String menuType;

    @Column(name = "MENU_ID")
    private Long menuId;

    @Column(name = "IS_THUMB")
    private String isThumb;
}