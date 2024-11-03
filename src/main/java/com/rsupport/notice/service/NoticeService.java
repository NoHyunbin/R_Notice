package com.rsupport.notice.service;

import com.rsupport.common.domain.ResultResponse;
import com.rsupport.notice.domain.Notice;
import com.rsupport.notice.domain.NoticeFile;
import com.rsupport.notice.repository.NoticeFileRepository;
import com.rsupport.notice.repository.NoticeRepository;
import com.rsupport.notice.repository.noticemapping.NoticeFileMapping;
import com.rsupport.notice.repository.noticemapping.NoticeMapping;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeService {
    private static final String UPLOAD_FILE_DIR = System.getProperty("user.dir") + "/upload_files/";
    private final NoticeRepository noticeRepository;
    private final NoticeFileRepository noticeFileRepository;

    @Transactional(rollbackFor = IOException.class)
    public ResponseEntity<ResultResponse<Notice>> saveNotice(Notice notice, List<MultipartFile> files, Boolean isUpdate) {
        // 공지사항 정보 저장
        noticeRepository.save(notice);

        // 파일 저장
        // Path 생성
        Path uploadFilePath = Paths.get(UPLOAD_FILE_DIR + notice.getNoticeId());
        if (!Files.exists(uploadFilePath)) {
            try {
                Files.createDirectories(uploadFilePath);
            } catch (IOException e) {
                log.error("", e);
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return ResponseEntity.ok(ResultResponse.res(HttpStatus.OK, "첨부파일 경로 생성 중 오류가 발생했어요.", null));
            }
        }

        // 실제 파일 저장
        for (MultipartFile multipartFile : files) {
            try {
                // 저장할 파일의 경로 생성, 물리 파일 저장
                String filePath = uploadFilePath + "\\" + multipartFile.getOriginalFilename();
                File file = new File(filePath);
                multipartFile.transferTo(file);

                // DB에 파일 정보 저장
                NoticeFile noticeFile = new NoticeFile();
                noticeFile.setFileName(multipartFile.getOriginalFilename());
                noticeFile.setFilePath(uploadFilePath + "\\");
                noticeFile.setCreatedBy(notice.getAuthor());
                noticeFile.setNotice(notice);
                noticeFileRepository.save(noticeFile);
            } catch (IOException e) {
                log.error("", e);
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return ResponseEntity.ok(ResultResponse.res(HttpStatus.OK, "파일 저장에 실패했어요.", null));
            }
        }
        if (isUpdate != null && isUpdate) {
            return ResponseEntity.ok(ResultResponse.res(HttpStatus.OK, "공지 수정에 성공했어요.", notice));
        } else {
            return ResponseEntity.ok(ResultResponse.res(HttpStatus.OK, "공지 등록에 성공했어요.", notice));
        }
    }

    public ResponseEntity<ResultResponse<Optional<Notice>>> getNotice(Long noticeId) {
        // 공지사항 조회
        Optional<Notice> optionalNotice = noticeRepository.findById(noticeId);

        if (optionalNotice.isPresent()) {
            // 조회수 증가
            Notice notice = optionalNotice.get();
            notice.setViews(notice.getViews() + 1);
            noticeRepository.save(notice);

            // 첨부파일 조회
            List<NoticeFileMapping> noticeFiles = noticeFileRepository.findByNotice_NoticeId(noticeId);
            optionalNotice.get().setNoticeFiles(noticeFiles);
            return ResponseEntity.ok(ResultResponse.res(HttpStatus.OK, "조회가 완료 되었어요.", optionalNotice));
        }
        return ResponseEntity.ok(ResultResponse.res(HttpStatus.OK, "조회가 결과가 없어요.", optionalNotice));
    }

    public ResponseEntity<ResultResponse<List<NoticeMapping>>> getNotices(Notice notice) {
        Integer pageNo = notice.getPageNo();
        Integer pageSize = notice.getPageSize();
        if (pageNo == null) {
            pageNo = 1;
        }
        if (pageSize == null) {
            pageSize = 10;
        }
        pageNo = (pageNo - 1) * pageSize;
        List<NoticeMapping> notices = noticeRepository.findActiveNotices(pageNo, pageSize);

        return ResponseEntity.ok(ResultResponse.res(HttpStatus.OK, "조회가 완료 되었어요.", notices));
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<ResultResponse<Notice>> updateNotice(Notice notice, List<MultipartFile> files) {
        // 공지사항 유효성 검증
        if (notice.getNoticeId() == null) {
            return ResponseEntity.ok(ResultResponse.res(HttpStatus.OK, "noticeId가 없어요. 수정하기 위해서 noticeId를 확인해주세요."));
        }
        Notice originNotice = noticeRepository.findById(notice.getNoticeId()).orElse(null);
        if (originNotice == null) {
            return ResponseEntity.ok(ResultResponse.res(HttpStatus.OK, "noticeId : " + notice.getNoticeId() + "번 공지사항이 존재하지 않아요."));
        }
        notice.setViews(originNotice.getViews());
        notice.setDeleteFlag(originNotice.getDeleteFlag());

        // 첨부파일 수정
        List<NoticeFile> noticeFiles = noticeFileRepository.findByNotice_NoticeIdAndDeleteFlag(notice.getNoticeId(), "N");
        noticeFiles.forEach(noticeFile -> {
            noticeFile.setDeleteFlag("Y");
            noticeFile.setDeletedBy(notice.getAuthor());
            noticeFile.setDeleteDateTime(LocalDateTime.now());
        });
        noticeFileRepository.saveAll(noticeFiles);

        return this.saveNotice(notice, files, Boolean.TRUE);
    }

    public ResponseEntity<ResultResponse<Optional<Notice>>> deleteNotice(Notice delNotice) {
        Optional<Notice> optionalNotice = noticeRepository.findById(delNotice.getNoticeId());

        if (optionalNotice.isPresent()) {
            Notice notice = optionalNotice.get();
            if (notice.getAuthor().equals(delNotice.getAuthor())) {
                notice.setDeleteFlag("Y");
                notice.setLastUpdateDateTime(LocalDateTime.now());
                noticeRepository.save(notice);
                return ResponseEntity.ok(ResultResponse.res(HttpStatus.OK, "삭제가 완료되었어요."));
            } else {
                return ResponseEntity.ok(ResultResponse.res(HttpStatus.OK, "공지사항 작성자가 달라요. 작성자가 같은 경우에만 삭제할 수 있어요."));
            }
        }
        return ResponseEntity.ok(ResultResponse.res(HttpStatus.OK, delNotice.getNoticeId() + "번 공사항은 존재하지 않아요."));
    }
}
