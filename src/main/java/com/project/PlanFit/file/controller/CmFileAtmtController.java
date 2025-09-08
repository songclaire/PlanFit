package com.project.PlanFit.file.controller;

import com.project.PlanFit.file.dto.FileAtmtDto;
import com.project.PlanFit.file.service.FileAtmtService;
import com.project.PlanFit.file.util.StorageUtil;
import org.springframework.core.io.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CmFileAtmtController {

    private final FileAtmtService fileAtmtService;
    private final StorageUtil storageUtil;

    /**
     * 파일 업로드(저장)
     * @param file
     * @param menuType
     * @return
     */
    @PostMapping("/file/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("menuType") String menuType) throws IOException {

        // 파일 저장
        StorageUtil.Saved result = storageUtil.save(file);
        // db에 메타정보 저장
        Long fileId = fileAtmtService.saveFileInfo(file, result, menuType);
        // 응답 반환
        return ResponseEntity.ok(Map.of("fileId", fileId));
    }

    /**
     * 파일 다운로드
     * @param fileId
     * @return
     * @throws Exception
     */
    @GetMapping("/file/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId) throws Exception {
        // DB에서 파일 정보 조회
        FileAtmtDto fileAtmtDto = fileAtmtService.downloadFile(fileId);

        // StorageUtil로 파일 리소스 가져오기 (filePath, storedFileNm)
        Resource fileRes = storageUtil.asResource(fileAtmtDto.getFilePath(), fileAtmtDto.getStoredFileNm());

        // ResponseEntity로 반환
        return ResponseEntity.ok(fileRes);
    }
}
