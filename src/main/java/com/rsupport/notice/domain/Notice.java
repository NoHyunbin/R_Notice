package com.rsupport.notice.domain;

import com.rsupport.common.domain.BaseDomain;
import com.rsupport.notice.repository.noticemapping.NoticeFileMapping;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@DynamicUpdate
public class Notice extends BaseDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noticeId;                        // 공지사항 ID

    @NotNull
    private String title;                         // 제목
    @NotNull
    private String content;                       // 내용
    @NotNull
    private String author;                        // 작성자
    @NotNull
    private LocalDateTime startDateTime;          // 공지 시작일시
    @NotNull
    private LocalDateTime endDateTime;            // 공지 종료일시
    private Long views;                           // 조회수
    private String deleteFlag;                    // 삭제여부

    @Transient
    private Integer pageNo;                       // 페이지 번호
    @Transient
    private Integer pageSize;                     // 페이지의 항목 수
    @Transient
    private List<NoticeFileMapping> noticeFiles;  // 첨부파일

    @PrePersist
    private void prePersist() {
        setCreationDateTime(LocalDateTime.now());
        setCreatedBy(author);
        setLastUpdateDateTime(LocalDateTime.now());
        setLastUpdatedBy(author);
        if(noticeId == null) {
            setViews(0L);
            setDeleteFlag("N");
        }
    }

    @PreUpdate
    private void preUpdate() {
        setLastUpdateDateTime(LocalDateTime.now());
        setLastUpdatedBy(author);
    }
}
