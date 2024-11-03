package com.rsupport.notice.repository;

import com.rsupport.notice.domain.NoticeFile;
import com.rsupport.notice.repository.noticemapping.NoticeFileMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeFileRepository extends JpaRepository<NoticeFile, Long> {
    List<NoticeFileMapping> findByNotice_NoticeIdAndDeleteFlag(Long noticeId, String deleteFlag);
    List<NoticeFile> findByNotice_NoticeId(Long noticeId);
}
