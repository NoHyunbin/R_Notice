package com.rsupport.notice.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.wildfly.common.annotation.NotNull;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class NoticeFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noticeFileId;               // 공지사항 파일 ID

    @NotNull
    private String fileName;                 // 파일명
    private String filePath;                 // 파일경로
    private String deleteFlag;               // 삭제여부
    @Column(updatable = false)
    private String createdBy;                // 생성자
    @Column(updatable = false)
    private LocalDateTime creationDateTime;  // 생성일시
    private String deletedBy;                // 삭제자
    private LocalDateTime deleteDateTime;    // 삭제일시

    @ManyToOne
    @JoinColumn(name = "notice_id")
    private Notice notice;

    @PrePersist
    private void prePersist() {
        setCreationDateTime(LocalDateTime.now());
        setDeleteFlag("N");
    }
}
