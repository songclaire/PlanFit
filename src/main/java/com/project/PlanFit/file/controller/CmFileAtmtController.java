package com.project.PlanFit.file.controller;

import com.project.PlanFit.file.service.FileAtmtService;
import com.project.PlanFit.file.util.StorageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CmFileController {

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
}
