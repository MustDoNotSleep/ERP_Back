package com.erp.controller;

import com.erp.dto.ResignationApplicationDto;
import com.erp.entity.enums.ApplicationStatus;
import com.erp.service.ResignationApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 퇴직 신청 컨트롤러
 * 
 * - POST   /api/resignations              : 퇴직 신청 생성
 * - GET    /api/resignations              : 전체 퇴직 신청 조회 (페이징)
 * - GET    /api/resignations/{id}         : 퇴직 신청 상세 조회
 * - GET    /api/resignations/employee/{employeeId} : 특정 직원의 퇴직 신청 조회
 * - GET    /api/resignations/status/{status}       : 상태별 퇴직 신청 조회
 * - PUT    /api/resignations/{id}/process : 퇴직 신청 승인/반려
 * - PUT    /api/resignations/{id}/cancel  : 퇴직 신청 취소
 * - DELETE /api/resignations/{id}         : 퇴직 신청 삭제
 * - GET    /api/resignations/statistics   : 퇴직 신청 통계
 */
@RestController
@RequestMapping("/api/resignations")
@RequiredArgsConstructor
public class ResignationApplicationController {
    
    private final ResignationApplicationService resignationApplicationService;
    
    /**
     * 퇴직 신청 생성
     * 
     * @param request 퇴직 신청 정보
     * @return 생성된 퇴직 신청
     * 
     * Example Request (본인 신청 - employeeId 생략 가능):
     * POST /api/resignations
     * {
     *   "desiredResignationDate": "2025-12-31",
     *   "reason": "개인 사유",
     *   "detailedReason": "가족 간병을 위한 퇴직"
     * }
     * 
     * Example Request (관리자가 대신 신청):
     * POST /api/resignations
     * {
     *   "employeeId": 12345,
     *   "desiredResignationDate": "2025-12-31",
     *   "reason": "개인 사유"
     * }
     */
    @PostMapping
    public ResponseEntity<ResignationApplicationDto.Response> createApplication(
            @Valid @RequestBody ResignationApplicationDto.Request request) {
        
        ResignationApplicationDto.Response response = resignationApplicationService.createApplication(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * 전체 퇴직 신청 조회 (페이징)
     * 
     * @param pageable 페이징 정보 (기본: 최신순, 20개)
     * @return 퇴직 신청 목록
     * 
     * Example:
     * GET /api/resignations
     * GET /api/resignations?page=0&size=10&sort=applicationDate,desc
     */
    @GetMapping
    public ResponseEntity<Page<ResignationApplicationDto.Response>> getAllApplications(
            @PageableDefault(size = 20, sort = "applicationDate", direction = Sort.Direction.DESC) Pageable pageable) {
        
        Page<ResignationApplicationDto.Response> responses = resignationApplicationService.getAllApplications(pageable);
        return ResponseEntity.ok(responses);
    }
    
    /**
     * 퇴직 신청 상세 조회
     * 
     * @param id 퇴직 신청 ID
     * @return 퇴직 신청 상세
     * 
     * Example:
     * GET /api/resignations/1
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResignationApplicationDto.Response> getApplicationById(@PathVariable Long id) {
        ResignationApplicationDto.Response response = resignationApplicationService.getApplicationById(id);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 특정 직원의 퇴직 신청 조회
     * 
     * @param employeeId 직원 ID
     * @return 해당 직원의 퇴직 신청 목록
     * 
     * Example:
     * GET /api/resignations/employee/12345
     */
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<ResignationApplicationDto.Response>> getApplicationsByEmployeeId(
            @PathVariable Long employeeId) {
        
        List<ResignationApplicationDto.Response> responses = resignationApplicationService.getApplicationsByEmployeeId(employeeId);
        return ResponseEntity.ok(responses);
    }
    
    /**
     * 상태별 퇴직 신청 조회
     * 
     * @param status 신청 상태 (PENDING, APPROVED, REJECTED)
     * @return 해당 상태의 퇴직 신청 목록
     * 
     * Example:
     * GET /api/resignations/status/PENDING
     * GET /api/resignations/status/APPROVED
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<ResignationApplicationDto.Response>> getApplicationsByStatus(
            @PathVariable ApplicationStatus status) {
        
        List<ResignationApplicationDto.Response> responses = resignationApplicationService.getApplicationsByStatus(status);
        return ResponseEntity.ok(responses);
    }
    
    /**
     * 퇴직 신청 승인/반려
     * 
     * @param id 퇴직 신청 ID
     * @param request 승인/반려 정보
     * @return 처리된 퇴직 신청
     * 
     * Example Request (승인 - processorId 생략 가능):
     * PUT /api/resignations/1/process
     * {
     *   "approved": true,
     *   "finalResignationDate": "2025-12-31"
     * }
     * 
     * Example Request (반려 - processorId 생략 가능):
     * PUT /api/resignations/1/process
     * {
     *   "approved": false,
     *   "rejectionReason": "퇴직 시기 조정 필요"
     * }
     */
    @PutMapping("/{id}/process")
    public ResponseEntity<ResignationApplicationDto.Response> processApplication(
            @PathVariable Long id,
            @Valid @RequestBody ResignationApplicationDto.ApprovalRequest request) {
        
        ResignationApplicationDto.Response response = resignationApplicationService.approveOrReject(id, request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 퇴직 신청 취소 (신청자만, PENDING 상태만)
     * 
     * @param id 퇴직 신청 ID
     * @return 취소된 퇴직 신청
     * 
     * Example:
     * PUT /api/resignations/1/cancel
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<ResignationApplicationDto.Response> cancelApplication(@PathVariable Long id) {
        ResignationApplicationDto.Response response = resignationApplicationService.cancelApplication(id);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 퇴직 신청 삭제 (관리자만)
     * 
     * @param id 퇴직 신청 ID
     * @return 204 No Content
     * 
     * Example:
     * DELETE /api/resignations/1
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApplication(@PathVariable Long id) {
        resignationApplicationService.deleteApplication(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * 퇴직 신청 통계 조회
     * 
     * @return 퇴직 신청 통계 (전체, 대기, 승인, 반려)
     * 
     * Example:
     * GET /api/resignations/statistics
     * 
     * Response:
     * {
     *   "totalApplications": 100,
     *   "pendingApplications": 10,
     *   "approvedApplications": 80,
     *   "rejectedApplications": 10
     * }
     */
    @GetMapping("/statistics")
    public ResponseEntity<ResignationApplicationDto.Statistics> getStatistics() {
        ResignationApplicationDto.Statistics statistics = resignationApplicationService.getStatistics();
        return ResponseEntity.ok(statistics);
    }
}
