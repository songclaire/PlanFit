package com.project.PlanFit.file.service;

import com.project.PlanFit.file.dto.FileAtmtDto;
import com.project.PlanFit.file.entity.FileAtmt;
import com.project.PlanFit.file.repository.FileAtmtRepository;
import com.project.PlanFit.file.util.StorageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class FileAtmtService {

    private final FileAtmtRepository fileAtmtRepository;

    public Long saveFileInfo(MultipartFile originalFile, StorageUtil.Saved savedResult, String menuType) {
        FileAtmt fileAtmt = FileAtmt.builder()
                .originFileNm(originalFile.getOriginalFilename())
                .storedFileNm(savedResult.storedName())
                .fileSize(originalFile.getSize())
                .fileExtension(savedResult.ext())
                .filePath(savedResult.dir())
                .menuType(menuType)
                .menuId(null)
                .isThumb("N")
                .tmprStrgYn("Y")
                .build();

        FileAtmt saved = fileAtmtRepository.save(fileAtmt);

        return saved.getFileId();
    }

    public FileAtmtDto downloadFile(Long fileId) {
        FileAtmt fileAtmt = fileAtmtRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("파일을 찾을 수 없습니다." + fileId));

        return FileAtmtDto.fromEntity(fileAtmt);
    }

}
