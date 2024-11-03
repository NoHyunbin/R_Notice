package com.rsupport.notice.controller;

import com.rsupport.common.domain.ResultResponse;
import com.rsupport.notice.domain.Notice;
import com.rsupport.notice.repository.noticemapping.NoticeMapping;
import com.rsupport.notice.service.NoticeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @PostMapping
    public ResponseEntity<ResultResponse<Notice>> createNotice(@Valid Notice notice, @RequestPart(required = false) List<MultipartFile> files) {
        if (files.size() > 10) {
            return ResponseEntity.ok(ResultResponse.res(HttpStatus.OK, "파일은 10개까지만 첨부 가능해요.", null));
        }
        return noticeService.saveNotice(notice, files, null);
    }

    @GetMapping("/{noticeId}")
    public ResponseEntity<ResultResponse<Optional<Notice>>> getNotice(@PathVariable Long noticeId) {
        return noticeService.getNotice(noticeId);
    }

    @GetMapping()
    public ResponseEntity<ResultResponse<List<NoticeMapping>>> getNotices(@RequestBody Notice notice) {
        return noticeService.getNotices(notice);
    }

    @PutMapping()
    public ResponseEntity<ResultResponse<Notice>> updateNotices(@Valid Notice notice, @RequestPart(required = false) List<MultipartFile> files) {
        return noticeService.updateNotice(notice, files);
    }

    @DeleteMapping()
    public ResponseEntity<ResultResponse<Optional<Notice>>> deleteNotices(@RequestBody Notice notice) {
        return noticeService.deleteNotice(notice);
    }
}
