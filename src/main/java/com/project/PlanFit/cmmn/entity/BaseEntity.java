package com.project.PlanFit.cmmn.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
public class BaseEntity {

    @CreatedDate
    @Column(name = "REG_DT", updatable = false)
    private LocalDateTime regDt;

    @LastModifiedDate
    @Column(name = "MDFCN_DT")
    private LocalDateTime mdfcnDt;

    @CreatedBy
    @Column(name = "REG_ID", updatable = false)
    private String regId;

    @LastModifiedBy
    @Column(name = "MDFR_ID")
    private String mdfrId;

    @Column(name = "DEL_YN")
    private String delYn = "N";

}
