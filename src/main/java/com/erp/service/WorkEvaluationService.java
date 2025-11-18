package com.erp.service;

import com.erp.dto.WorkEvaluationDto;
import com.erp.entity.WorkEvaluation;
import com.erp.entity.Employee;
import com.erp.repository.WorkEvaluationRepository;
import com.erp.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkEvaluationService {
    private final WorkEvaluationRepository workEvaluationRepository;
    private final EmployeeRepository employeeRepository;

    // 1. 관리자: 부서별 평가 내역 조회
    public List<WorkEvaluationDto.Response> getEvaluationsByDepartment(
            Long departmentId, 
            Integer evaluationYear, 
            Integer evaluationQuarter, 
            Long employeeId) {
        
        // 1) 관리자 검증: employeeId가 해당 부서의 관리자 직급인지 확인
        Employee manager = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new IllegalArgumentException("관리자 정보를 찾을 수 없습니다."));
        
        // position level 3 이상이면 관리자
        if (manager.getPosition() == null 
            || manager.getPosition().getPositionLevel() == null 
            || manager.getPosition().getPositionLevel() < 3
            || !manager.getDepartment().getId().equals(departmentId)) {
            throw new IllegalArgumentException("해당 부서의 관리자만 조회할 수 있습니다.");
        }

        // 2) 해당 부서의 평가 내역 반환
        List<WorkEvaluation> evaluations = workEvaluationRepository
            .findByEmployee_Department_IdAndEvaluationYearAndEvaluationQuarter(
                departmentId, evaluationYear, evaluationQuarter);
        
        return evaluations.stream()
            .map(WorkEvaluationDto.Response::from)
            .toList();
    }

    // 2. 직원: 본인 평가 조회(분기별 1회만 가능)
    public WorkEvaluationDto.Response getMyEvaluation(
            Long employeeId, 
            Integer evaluationYear, 
            Integer evaluationQuarter) {
        
        // 직원 존재 여부 확인
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new IllegalArgumentException("직원 정보를 찾을 수 없습니다."));
        
        // 해당 분기 평가 조회
        WorkEvaluation evaluation = workEvaluationRepository
            .findByEmployee_IdAndEvaluationYearAndEvaluationQuarter(
                employeeId, evaluationYear, evaluationQuarter)
            .orElseThrow(() -> new IllegalArgumentException("해당 분기의 평가 내역이 없습니다."));
        
        return WorkEvaluationDto.Response.from(evaluation);
    }

    // 3. 직원: 평가 저장/제출 (분기별 1회만 가능)
    @Transactional
    public WorkEvaluationDto.Response saveOrSubmitEvaluation(
            Long employeeId, 
            WorkEvaluationDto.UpdateRequest request) {
        
        // 요청 데이터 검증
        if (request.getEvaluationYear() == null || request.getEvaluationQuarter() == null) {
            throw new IllegalArgumentException("평가 연도와 분기는 필수입니다.");
        }
        
        // 이미 해당 분기 평가가 있으면 수정 불가
        Optional<WorkEvaluation> existing = workEvaluationRepository
            .findByEmployee_IdAndEvaluationYearAndEvaluationQuarter(
                employeeId, request.getEvaluationYear(), request.getEvaluationQuarter());
        
        if (existing.isPresent()) {
            throw new IllegalStateException("이미 해당 분기 평가가 존재합니다. 수정은 PUT 요청을 사용하세요.");
        }

        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new IllegalArgumentException("직원 정보를 찾을 수 없습니다."));

        // 평가자 정보 처리 (있을 경우)
        Employee evaluator = null;
        if (request.getEvaluatorId() != null) {
            evaluator = employeeRepository.findById(request.getEvaluatorId())
                .orElseThrow(() -> new IllegalArgumentException("평가자 정보를 찾을 수 없습니다."));
        }

        WorkEvaluation evaluation = WorkEvaluation.builder()
            .employee(employee)
            .evaluationYear(request.getEvaluationYear())
            .evaluationQuarter(request.getEvaluationQuarter())
            .attitudeScore(request.getAttitudeScore())
            .achievementScore(request.getAchievementScore())
            .collaborationScore(request.getCollaborationScore())
            .contributionGrade(request.getContributionGrade())
            .totalGrade(request.getTotalGrade())
            .status(request.getStatus() != null ? request.getStatus() : "DRAFT") // 기본값: 임시저장
            .evaluator(evaluator)
            .build();

        WorkEvaluation saved = workEvaluationRepository.save(evaluation);
        return WorkEvaluationDto.Response.from(saved);
    }

    // 4. 평가 수정 (제출 전까지만 가능)
    @Transactional
    public WorkEvaluationDto.Response updateEvaluation(
            Long evaluationId, 
            WorkEvaluationDto.UpdateRequest request) {
        
        // 평가 존재 여부 확인
        WorkEvaluation evaluation = workEvaluationRepository.findById(evaluationId)
            .orElseThrow(() -> new IllegalArgumentException("평가 정보를 찾을 수 없습니다."));
        
        // 제출 완료 상태면 수정 불가
        if ("SUBMITTED".equals(evaluation.getStatus())) {
            throw new IllegalStateException("제출 완료된 평가는 수정할 수 없습니다.");
        }

        // 평가자 정보 업데이트 (있을 경우)
        Employee evaluator = null;
        if (request.getEvaluatorId() != null) {
            evaluator = employeeRepository.findById(request.getEvaluatorId())
                .orElseThrow(() -> new IllegalArgumentException("평가자 정보를 찾을 수 없습니다."));
        }

        // 필드 업데이트 (Builder 패턴으로 새 객체 생성)
        WorkEvaluation updated = WorkEvaluation.builder()
            .evaluationId(evaluation.getEvaluationId())
            .employee(evaluation.getEmployee())
            .evaluationYear(request.getEvaluationYear() != null ? request.getEvaluationYear() : evaluation.getEvaluationYear())
            .evaluationQuarter(request.getEvaluationQuarter() != null ? request.getEvaluationQuarter() : evaluation.getEvaluationQuarter())
            .attitudeScore(request.getAttitudeScore() != null ? request.getAttitudeScore() : evaluation.getAttitudeScore())
            .achievementScore(request.getAchievementScore() != null ? request.getAchievementScore() : evaluation.getAchievementScore())
            .collaborationScore(request.getCollaborationScore() != null ? request.getCollaborationScore() : evaluation.getCollaborationScore())
            .contributionGrade(request.getContributionGrade() != null ? request.getContributionGrade() : evaluation.getContributionGrade())
            .totalGrade(request.getTotalGrade() != null ? request.getTotalGrade() : evaluation.getTotalGrade())
            .status(request.getStatus() != null ? request.getStatus() : evaluation.getStatus())
            .evaluator(evaluator != null ? evaluator : evaluation.getEvaluator())
            .createdAt(evaluation.getCreatedAt())
            .build();

        WorkEvaluation saved = workEvaluationRepository.save(updated);
        return WorkEvaluationDto.Response.from(saved);
    }
}