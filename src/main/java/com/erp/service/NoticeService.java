package com.erp.service;

import com.erp.dto.NoticeDto;
import com.erp.entity.Employee;
import com.erp.entity.Notice;
import com.erp.exception.EntityNotFoundException;
import com.erp.repository.EmployeeRepository;
import com.erp.repository.NoticeRepository;
import com.erp.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class NoticeService {
    
    private final NoticeRepository noticeRepository;
    private final EmployeeRepository employeeRepository;
    
    /**
     * 전체 공지사항 조회 (페이징)
     */
    public Page<NoticeDto.Response> getAllNotices(Pageable pageable) {
        return noticeRepository.findAllActive(pageable)
                .map(NoticeDto.Response::from);
    }
    
    /**
     * 제목 검색 (페이징)
     */
    public Page<NoticeDto.Response> searchNotices(String keyword, Pageable pageable) {
        return noticeRepository.searchByTitle(keyword, pageable)
                .map(NoticeDto.Response::from);
    }
    
    /**
     * 공지사항 상세 조회 (조회수 증가)
     */
    @Transactional
    public NoticeDto.Response getNotice(Long id) {
        Notice notice = noticeRepository.findActiveById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notice", id.toString()));
        
        // 조회수 증가
        notice.incrementViewCount();
        noticeRepository.save(notice);
        
        log.info("공지사항 조회 - ID: {}, 제목: {}, 조회수: {}", 
            id, notice.getTitle(), notice.getViewCount());
        
        return NoticeDto.Response.from(notice);
    }
    
    /**
     * 중요 공지사항 목록 조회 (최대 5개)
     */
    public List<NoticeDto.Summary> getImportantNotices() {
        return noticeRepository.findImportantNotices().stream()
                .limit(5)
                .map(NoticeDto.Summary::from)
                .collect(Collectors.toList());
    }
    
    /**
     * 공지사항 작성
     */
    @Transactional
    public NoticeDto.Response createNotice(NoticeDto.Request request) {
        // 현재 로그인한 사용자 정보
        Long authorId = SecurityUtil.getCurrentEmployeeId();
        Employee author = employeeRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Employee", authorId.toString()));
        
        Notice notice = Notice.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .author(author)
                .isImportant(request.getIsImportant())
                .isActive(true)
                .viewCount(0)
                .build();
        
        Notice saved = noticeRepository.save(notice);
        
        log.info("공지사항 작성 완료 - ID: {}, 작성자: {}, 제목: {}, 중요: {}", 
            saved.getId(), author.getName(), saved.getTitle(), saved.getIsImportant());
        
        return NoticeDto.Response.from(saved);
    }
    
    /**
     * 공지사항 수정
     */
    @Transactional
    public NoticeDto.Response updateNotice(Long id, NoticeDto.UpdateRequest request) {
        Notice notice = noticeRepository.findActiveById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notice", id.toString()));
        
        // 작성자 확인
        Long currentUserId = SecurityUtil.getCurrentEmployeeId();
        if (!notice.getAuthor().getId().equals(currentUserId)) {
            throw new IllegalStateException("본인이 작성한 공지사항만 수정할 수 있습니다.");
        }
        
        notice.setTitle(request.getTitle());
        notice.setContent(request.getContent());
        notice.setIsImportant(request.getIsImportant());
        
        Notice updated = noticeRepository.save(notice);
        
        log.info("공지사항 수정 완료 - ID: {}, 제목: {}", id, updated.getTitle());
        
        return NoticeDto.Response.from(updated);
    }
    
    /**
     * 공지사항 삭제 (비활성화)
     */
    @Transactional
    public void deleteNotice(Long id) {
        Notice notice = noticeRepository.findActiveById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notice", id.toString()));
        
        // 작성자 또는 관리자만 삭제 가능
        Long currentUserId = SecurityUtil.getCurrentEmployeeId();
        Employee currentUser = employeeRepository.findById(currentUserId)
                .orElseThrow(() -> new EntityNotFoundException("Employee", currentUserId.toString()));
        
        boolean isAuthor = notice.getAuthor().getId().equals(currentUserId);
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN") || 
                                 auth.getAuthority().equals("ROLE_HR"));
        
        if (!isAuthor && !isAdmin) {
            throw new IllegalStateException("본인이 작성한 공지사항이거나 관리자만 삭제할 수 있습니다.");
        }
        
        notice.deactivate();
        noticeRepository.save(notice);
        
        log.info("공지사항 삭제 완료 - ID: {}, 삭제자: {}", id, currentUser.getName());
    }
}
