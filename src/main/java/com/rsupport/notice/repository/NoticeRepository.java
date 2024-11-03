package com.rsupport.notice.repository;

import com.rsupport.notice.domain.Notice;
import com.rsupport.notice.repository.noticemapping.NoticeMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    @Query(nativeQuery = true, value = "SELECT * FROM notice n WHERE NOW() BETWEEN n.start_date_time AND n.end_date_time AND n.delete_flag = 'N' ORDER BY n.notice_id LIMIT :pageNo, :pageSize")
    List<NoticeMapping> findActiveNotices(Integer pageNo, Integer pageSize);
}
