package com.rsupport.notice.repository;

import com.rsupport.notice.domain.NoticeFile;
import com.rsupport.notice.repository.noticemapping.NoticeFileMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeFileRepository extends JpaRepository<NoticeFile, Long> {
    List<NoticeFileMapping> findByNotice_NoticeId(Long noticeId);
    List<NoticeFile> findByNotice_NoticeIdAndDeleteFlag(Long noticeId, String deleteFlag);
}
