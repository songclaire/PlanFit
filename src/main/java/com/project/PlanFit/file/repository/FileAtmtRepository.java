package com.project.PlanFit.file.repository;

import com.project.PlanFit.file.entity.FileAtmt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FileAtmtRepository extends JpaRepository<FileAtmt, Long> {

    // 임시저장한 컬럼의 값을 Y에서 N으로 변경
    @Modifying
    @Query("UPDATE FileAtmt a SET a.tmprStrgYn = 'N' WHERE a.fileId = :fileId")
    void updateTmprStrgYn(@Param("fileId") Long fileId);
}
