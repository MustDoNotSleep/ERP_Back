package com.erp.controller;

import com.erp.dto.NoticeDto;
import com.erp.dto.PageResponse;
import com.erp.service.NoticeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 공지사항 API
 * 
 * - GET    /notices             : 전체 공지사항 조회 (페이징)
 * - GET    /notices/{id}        : 공지사항 상세 조회
 * - GET    /notices/important   : 중요 공지사항 목록 (최대 5개)
 * - GET    /notices/search      : 제목 검색
 * - POST   /notices             : 공지사항 작성 (관리자, HR)
 * - PUT    /notices/{id}        : 공지사항 수정 (작성자)
 * - DELETE /notices/{id}        : 공지사항 삭제 (작성자, 관리자)
 */
@RestController
@RequestMapping("/notices")
@RequiredArgsConstructor
public class NoticeController {
    
    private final NoticeService noticeService;
    
    /**
     * 전체 공지사항 조회 (페이징)
     * 
     * 예시: GET /notices?page=0&size=20
     * 
     * 응답:
     * {
     *   "success": true,
     *   "data": {
     *     "content": [...],
     *     "pageNumber": 0,
     *     "pageSize": 20,
     *     "totalElements": 50,
     *     "totalPages": 3
     *   }
     * }
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PageResponse<NoticeDto.Response>>> getAllNotices(
            @RequestParam(required = false) String search,
            Pageable pageable) {
        
        Page<NoticeDto.Response> notices;
        
        if (search != null && !search.trim().isEmpty()) {
            notices = noticeService.searchNotices(search, pageable);
        } else {
            notices = noticeService.getAllNotices(pageable);
        }
        
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(notices)));
    }
    
    /**
     * 공지사항 상세 조회 (조회수 증가)
     * 
     * 예시: GET /notices/1
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<NoticeDto.Response>> getNotice(@PathVariable Long id) {
        NoticeDto.Response notice = noticeService.getNotice(id);
        return ResponseEntity.ok(ApiResponse.success(notice));
    }
    
    /**
     * 중요 공지사항 목록 조회 (최대 5개)
     * 
     * 예시: GET /notices/important
     * 
     * 용도: 메인 페이지에 중요 공지 표시
     */
    @GetMapping("/important")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<NoticeDto.Summary>>> getImportantNotices() {
        List<NoticeDto.Summary> notices = noticeService.getImportantNotices();
        return ResponseEntity.ok(ApiResponse.success(notices));
    }
    
    /**
     * 공지사항 작성 (관리자, HR만 가능)
     * 
     * 예시: POST /notices
     * Body:
     * {
     *   "title": "긴급 공지",
     *   "content": "내용...",
     *   "isImportant": true
     * }
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<ApiResponse<NoticeDto.Response>> createNotice(
            @Valid @RequestBody NoticeDto.Request request) {
        NoticeDto.Response created = noticeService.createNotice(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("공지사항이 작성되었습니다.", created));
    }
    
    /**
     * 공지사항 수정 (작성자만 가능)
     * 
     * 예시: PUT /notices/1
     * Body:
     * {
     *   "title": "수정된 제목",
     *   "content": "수정된 내용...",
     *   "isImportant": false
     * }
     */
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<NoticeDto.Response>> updateNotice(
            @PathVariable Long id,
            @Valid @RequestBody NoticeDto.UpdateRequest request) {
        NoticeDto.Response updated = noticeService.updateNotice(id, request);
        return ResponseEntity.ok(ApiResponse.success("공지사항이 수정되었습니다.", updated));
    }
    
    /**
     * 공지사항 삭제 (작성자 또는 관리자만 가능)
     * 
     * 예시: DELETE /notices/1
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deleteNotice(@PathVariable Long id) {
        noticeService.deleteNotice(id);
        return ResponseEntity.ok(ApiResponse.success("공지사항이 삭제되었습니다.", null));
    }
}
