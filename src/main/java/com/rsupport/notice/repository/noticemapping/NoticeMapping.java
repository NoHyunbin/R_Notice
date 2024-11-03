package com.rsupport.notice.repository.noticemapping;

import java.time.LocalDateTime;

public interface NoticeMapping {
    Long getNoticeId();                // 공지사항 ID
    String getTitle();                 // 제목
    String getContent();               // 내용
    String getAuthor();                // 작성자
    LocalDateTime getStartDateTime();  // 공지 시작일시
    LocalDateTime getEndDateTime();    // 공지 종료일시
    Long getViews();                   // 조회수
    String getDeleteFlag();            // 삭제여부
}
