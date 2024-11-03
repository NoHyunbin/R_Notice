package com.rsupport.notice.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.rsupport.common.domain.ResultResponse;
import com.rsupport.notice.domain.Notice;
import com.rsupport.notice.domain.NoticeFile;
import com.rsupport.notice.repository.NoticeFileRepository;
import com.rsupport.notice.repository.NoticeRepository;
import com.rsupport.notice.repository.noticemapping.NoticeFileMapping;
import com.rsupport.notice.repository.noticemapping.NoticeMapping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

public class NoticeServiceTest {

    @InjectMocks
    private NoticeService noticeService;

    @Mock
    private NoticeRepository noticeRepository;

    @Mock
    private NoticeFileRepository noticeFileRepository;

    @Mock
    private MultipartFile multipartFile;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void 공지사항_등록_성공() throws Exception {
        // 공지사항 정보 셋팅, 저장
        Notice notice = new Notice();
        notice.setNoticeId(1L);
        when(noticeRepository.save(notice)).thenReturn(notice);

        // 파일 정보 셋팅, 실제 파일/db 저장
        List<MultipartFile> files = Collections.singletonList(multipartFile);
        doNothing().when(multipartFile).transferTo((File) any());
        when(multipartFile.getOriginalFilename()).thenReturn("filename");

        // 메소드 실행
        ResponseEntity<ResultResponse<Notice>> response = noticeService.saveNotice(notice, files, null);

        // 결과
        verify(noticeRepository, times(1)).save(notice);
        verify(noticeFileRepository, times(1)).save(any(NoticeFile.class));
        assert response.getStatusCode() == HttpStatus.OK;
        assert response.getBody().getResultMessage().equals("공지 등록에 성공했어요.");
    }

    @Test
    public void 공지사항_등록_파일저장_실패() throws Exception {
        // 공지사항 정보 셋팅, 저장
        Notice notice = new Notice();
        notice.setNoticeId(1L);
        when(noticeRepository.save(notice)).thenReturn(notice);

        // 파일 정보 셋팅
        List<MultipartFile> files = Collections.singletonList(multipartFile);

        // 파일 저장 시 예외 발생
        doThrow(new IOException("File save error")).when(multipartFile).transferTo((File) any());
        MockedStatic<TransactionAspectSupport> mockedStatic = mockStatic(TransactionAspectSupport.class);
        mockedStatic.when(TransactionAspectSupport::currentTransactionStatus).thenReturn(mock(TransactionStatus.class));

        // 메소드 실행
        ResponseEntity<ResultResponse<Notice>> response = noticeService.saveNotice(notice, files, null);

        // 결과
        verify(noticeRepository, times(1)).save(notice);
        verify(noticeFileRepository, never()).save(any(NoticeFile.class));
        assert response.getStatusCode() == HttpStatus.OK;
        assert response.getBody().getResultMessage().equals("파일 저장에 실패했어요.");
    }

    @Test
    void 공지사항_단건_조회_성공() {
        // 공지사항 정보 셋팅, 저장
        Notice notice = new Notice();
        notice.setNoticeId(1L);
        notice.setViews(1L);
        when(noticeRepository.findById(1L)).thenReturn(Optional.of(notice));
        when(noticeRepository.save(notice)).thenReturn(notice);

        // 메소드 실행
        ResponseEntity<ResultResponse<Optional<Notice>>> response = noticeService.getNotice(1L);

        // 결과
        verify(noticeRepository, times(1)).findById(1L);
        verify(noticeRepository, times(1)).save(notice);
        verify(noticeFileRepository, times(1)).findByNotice_NoticeIdAndDeleteFlag(1L, "N");
        assert response.getStatusCode() == HttpStatus.OK;
        assert response.getBody().getResultMessage().equals("조회가 완료 되었어요.");
    }

    @Test
    void 공지사항_단건_조회_결과가_없는_경우() {
        // 공지사항 정보 셋팅, 저장
        Notice notice = new Notice();
        notice.setNoticeId(1L);
        notice.setViews(1L);
        when(noticeRepository.findById(1L)).thenReturn(Optional.empty());

        // 메소드 실행
        ResponseEntity<ResultResponse<Optional<Notice>>> response = noticeService.getNotice(1L);

        // 결과
        verify(noticeRepository, times(1)).findById(1L);
        verify(noticeRepository, times(0)).save(notice);
        verify(noticeFileRepository, times(0)).findByNotice_NoticeIdAndDeleteFlag(1L, "N");
        assert response.getStatusCode() == HttpStatus.OK;
        assert response.getBody().getResultMessage().equals("조회가 결과가 없어요.");
    }

    @Test
    void 공지사항_목록_조회_파라미터가_없는_경우() {
        // 공지사항 정보 셋팅, 저장
        Notice notice = new Notice();

        when(noticeRepository.findActiveNotices(1, 10)).thenReturn(Collections.singletonList(mock(NoticeMapping.class)));

        // 메소드 실행
        ResponseEntity<ResultResponse<List<NoticeMapping>>> response = noticeService.getNotices(notice);

        // 결과
        verify(noticeRepository, times(1)).findActiveNotices(0, 10);
        assert response.getStatusCode() == HttpStatus.OK;
        assert response.getBody().getResultMessage().equals("조회가 완료 되었어요.");
    }

    @Test
    void 공지사항_목록_조회_페이지번호만_있는_경우() {
        // 공지사항 정보 셋팅, 저장
        Notice notice = new Notice();
        notice.setPageNo(1);

        when(noticeRepository.findActiveNotices(1, 10)).thenReturn(Collections.singletonList(mock(NoticeMapping.class)));

        // 메소드 실행
        ResponseEntity<ResultResponse<List<NoticeMapping>>> response = noticeService.getNotices(notice);

        // 결과
        verify(noticeRepository, times(1)).findActiveNotices(0, 10);
        assert response.getStatusCode() == HttpStatus.OK;
        assert response.getBody().getResultMessage().equals("조회가 완료 되었어요.");
    }

    @Test
    void 공지사항_목록_조회_페이지번호와_페이지크기가_모두_있는_경우() {
        // 공지사항 정보 셋팅, 저장
        Notice notice = new Notice();
        notice.setPageNo(2);
        notice.setPageSize(5);

        when(noticeRepository.findActiveNotices(2, 5)).thenReturn(Collections.singletonList(mock(NoticeMapping.class)));

        // 메소드 실행
        ResponseEntity<ResultResponse<List<NoticeMapping>>> response = noticeService.getNotices(notice);

        // 결과
        verify(noticeRepository, times(1)).findActiveNotices(5, 5);
        assert response.getStatusCode() == HttpStatus.OK;
        assert response.getBody().getResultMessage().equals("조회가 완료 되었어요.");
    }

    @Test
    void 공지사항_수정_PK가_없는_경우() {
        // 공지사항 정보 셋팅, 저장
        Notice notice = new Notice();

        // 파일 정보 셋팅
        List<MultipartFile> files = Collections.singletonList(multipartFile);

        // 메소드 실행
        ResponseEntity<ResultResponse<Notice>> response = noticeService.updateNotice(notice, files);

        // 결과
        assert response.getStatusCode() == HttpStatus.OK;
        assert response.getBody().getResultMessage().equals("noticeId가 없어요. 수정하기 위해서 noticeId를 확인해주세요.");
    }

    @Test
    void 공지사항_수정_noticeId에_해당하는_데이터가_없는_경우() {
        // 공지사항 정보 셋팅, 저장
        Notice notice = new Notice();
        notice.setNoticeId(1L);

        // 파일 정보 셋팅
        List<MultipartFile> files = Collections.singletonList(multipartFile);
        when(noticeRepository.findById(1L)).thenReturn(Optional.empty());

        // 메소드 실행
        ResponseEntity<ResultResponse<Notice>> response = noticeService.updateNotice(notice, files);

        // 결과
        verify(noticeRepository, times(1)).findById(1L);
        assert response.getStatusCode() == HttpStatus.OK;
        assert response.getBody().getResultMessage().equals("noticeId : 1번 공지사항이 존재하지 않아요.");
    }

    @Test
    void 공지사항_수정_성공() {
        // 공지사항 정보 셋팅, 저장
        Notice notice = new Notice();
        notice.setNoticeId(1L);

        // 파일 정보 셋팅
        List<MultipartFile> files = Collections.singletonList(multipartFile);
        List<NoticeFile> noticeFiles = Collections.singletonList(mock(NoticeFile.class));
        when(noticeRepository.findById(1L)).thenReturn(Optional.of(notice));
        when(noticeFileRepository.findByNotice_NoticeId(1L)).thenReturn(noticeFiles);
        when(noticeFileRepository.saveAll(noticeFiles)).thenReturn(noticeFiles);

        // 메소드 실행
        ResponseEntity<ResultResponse<Notice>> response = noticeService.updateNotice(notice, files);

        // 결과
        verify(noticeRepository, times(1)).findById(1L);
        verify(noticeFileRepository, times(1)).findByNotice_NoticeId(1L);
        verify(noticeFileRepository, times(1)).saveAll(noticeFiles);
        assert response.getStatusCode() == HttpStatus.OK;
        assert response.getBody().getResultMessage().equals("공지 수정에 성공했어요.");
    }

    @Test
    void 공지사항_삭제_성공() {
        // 공지사항 정보 셋팅, 저장
        Notice notice = new Notice();
        notice.setNoticeId(14L);
        notice.setAuthor("author");
        when(noticeRepository.findById(14L)).thenReturn(Optional.of(notice));
        when(noticeRepository.save(notice)).thenReturn(notice);

        // 메소드 실행
        ResponseEntity<ResultResponse<Optional<Notice>>> response = noticeService.deleteNotice(notice);

        // 결과
        verify(noticeRepository, times(1)).findById(14L);
        verify(noticeRepository, times(1)).save(notice);
        assert response.getStatusCode() == HttpStatus.OK;
        assert response.getBody().getResultMessage().equals("삭제가 완료되었어요.");
    }

    @Test
    void 공지사항_삭제_작성자가_다른_경우() {
        // 공지사항 정보 셋팅, 저장
        Notice notice = new Notice();
        notice.setNoticeId(14L);
        notice.setAuthor("author2");
        Notice noticeOrigin = new Notice();
        noticeOrigin.setNoticeId(14L);
        noticeOrigin.setAuthor("author");
        when(noticeRepository.findById(14L)).thenReturn(Optional.of(noticeOrigin));

        // 메소드 실행
        ResponseEntity<ResultResponse<Optional<Notice>>> response = noticeService.deleteNotice(notice);

        // 결과
        verify(noticeRepository, times(1)).findById(14L);
        verify(noticeRepository, times(0)).save(notice);
        assert response.getStatusCode() == HttpStatus.OK;
        assert response.getBody().getResultMessage().equals("공지사항 작성자가 달라요. 작성자가 같은 경우에만 삭제할 수 있어요.");
    }

    @Test
    void 공지사항_삭제_삭제하려는_번호의_공지사항이_없는_경우() {
        // 공지사항 정보 셋팅, 저장
        Notice notice = new Notice();
        notice.setNoticeId(14L);
        notice.setAuthor("author");
        when(noticeRepository.findById(14L)).thenReturn(Optional.empty());

        // 메소드 실행
        ResponseEntity<ResultResponse<Optional<Notice>>> response = noticeService.deleteNotice(notice);

        // 결과
        verify(noticeRepository, times(1)).findById(14L);
        verify(noticeRepository, times(0)).save(notice);
        assert response.getStatusCode() == HttpStatus.OK;
        assert response.getBody().getResultMessage().equals("14번 공사항은 존재하지 않아요.");
    }
}
