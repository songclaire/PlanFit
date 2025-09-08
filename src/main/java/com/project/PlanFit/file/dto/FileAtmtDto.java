package com.project.PlanFit.file.dto;

import com.project.PlanFit.cmmn.dto.BaseDto;
import com.project.PlanFit.file.entity.FileAtmt;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileAtmtDto extends BaseDto {
    private Long fileId;
    private String originFileNm;
    private String storedFileNm;
    private Long fileSize;
    private String fileExtension;
    private String filePath;
    private String menuType;
    private Long menuId;
    private String isThumb;
    private String tmprStrgYn;

    public static FileAtmtDto fromEntity(FileAtmt entity) {
        FileAtmtDto dto = new FileAtmtDto();
        dto.setFileId(entity.getFileId());
        dto.setOriginFileNm(entity.getOriginFileNm());
        dto.setStoredFileNm(entity.getStoredFileNm());
        dto.setFileSize(entity.getFileSize());
        dto.setFileExtension(entity.getFileExtension());
        dto.setFilePath(entity.getFilePath());
        dto.setMenuType(entity.getMenuType());
        dto.setMenuId(entity.getMenuId());
        dto.setIsThumb(entity.getIsThumb());
        dto.setTmprStrgYn(entity.getTmprStrgYn());
        return dto;
    }

    public FileAtmt toEntity() {
        return FileAtmt.builder()
                .fileId(this.fileId)
                .originFileNm(this.originFileNm)
                .storedFileNm(this.storedFileNm)
                .fileSize(this.fileSize)
                .fileExtension(this.fileExtension)
                .filePath(this.filePath)
                .menuType(this.menuType)
                .menuId(this.menuId)
                .isThumb(this.isThumb)
                .tmprStrgYn(this.tmprStrgYn)
                .build();
    }

}