package com.rsupport.common.domain;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
public class BaseDomain {
    @Column(updatable = false)
    private String createdBy;                 // 생성자
    @Column(updatable = false)
    private LocalDateTime creationDateTime;   // 생성일시
    private String lastUpdatedBy;             // 수정자
    private LocalDateTime lastUpdateDateTime; // 수정일시
}