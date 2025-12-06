package com.erp.service;

import com.erp.dto.WorkEvaluationDto;
import com.erp.entity.Employee;
import com.erp.entity.WorkEvaluation;
import com.erp.repository.EmployeeRepository;
import com.erp.repository.WorkEvaluationRepository;
import com.erp.util.SecurityUtil;
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

    // 1. 프론트엔드 화면 조회용
    public List<WorkEvaluationDto.Response> getEvaluations(
            Integer evaluationYear,
            String quarterLabel,
            String departmentName 
    ) {
        Integer quarter = parseQuarterLabel(quarterLabel);
        String normalizedDepartmentName = normalizeDepartmentName(departmentName);

        List<WorkEvaluation> evaluations = (normalizedDepartmentName != null)
            ? fetchByDepartment(normalizedDepartmentName, evaluationYear, quarter)
            : fetchWithoutDepartment(evaluationYear, quarter);

        return evaluations.stream()
            .map(WorkEvaluationDto.Response::from)
            .toList();
    }

    // 2. 관리자: 부서별 평가 내역 조회
    public List<WorkEvaluationDto.Response> getEvaluationsByDepartment(
            Long departmentId, 
            Integer evaluationYear, 
            Integer evaluationQuarter, 
            Long employeeId) {

        Employee requester = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new IllegalArgumentException("관리자 정보를 찾을 수 없습니다."));

        if (requester.getPosition() == null 
            || requester.getPosition().getPositionLevel() == null 
            || requester.getPosition().getPositionLevel() < 3) {
            throw new IllegalArgumentException("조회 권한이 없습니다 (관리자 전용).");
        }

        boolean isMyDepartment = requester.getDepartment().getId().equals(departmentId);
        boolean isHrTeam = "인사팀".equals(requester.getDepartment().getDepartmentName());

        if (!isMyDepartment && !isHrTeam) {
            throw new IllegalArgumentException("해당 부서의 평가 내역을 조회할 권한이 없습니다.");
        }

        List<WorkEvaluation> evaluations = workEvaluationRepository
            .findByEmployee_Department_IdAndEvaluationYearAndEvaluationQuarter(
                departmentId, evaluationYear, evaluationQuarter);

        return evaluations.stream()
            .map(WorkEvaluationDto.Response::from)
            .toList();
    }

    // 3. 직원: 본인 평가 조회
    public WorkEvaluationDto.Response getMyEvaluation(
            Long employeeId, 
            Integer evaluationYear, 
            Integer evaluationQuarter) {
        
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new IllegalArgumentException("직원 정보를 찾을 수 없습니다."));

        WorkEvaluation evaluation = workEvaluationRepository
            .findByEmployee_IdAndEvaluationYearAndEvaluationQuarter(
                employeeId, evaluationYear, evaluationQuarter)
            .orElseThrow(() -> new IllegalArgumentException("해당 분기의 평가 내역이 없습니다."));

        return WorkEvaluationDto.Response.from(evaluation);
    }

    // 4. 평가 저장/제출
    @Transactional
    public WorkEvaluationDto.Response saveOrSubmitEvaluation(
            Long employeeId, 
            WorkEvaluationDto.UpdateRequest request) {
        
        if (request.getYear() == null || request.getQuarter() == null) {
            throw new IllegalArgumentException("평가 연도와 분기는 필수입니다.");
        }
        
        Integer quarter = parseQuarterLabel(request.getQuarter());
        if (quarter == null) quarter = 1; 

        Optional<WorkEvaluation> existing = workEvaluationRepository
            .findByEmployee_IdAndEvaluationYearAndEvaluationQuarter(
                employeeId, request.getYear(), quarter);
        
        if (existing.isPresent()) {
            throw new IllegalStateException("이미 해당 분기 평가가 존재합니다. 수정(PUT) 기능을 이용해주세요.");
        }

        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new IllegalArgumentException("직원 정보를 찾을 수 없습니다."));

        // 현재 로그인한 사용자를 평가자로 자동 설정
        Long evaluatorId = SecurityUtil.getCurrentEmployeeId();
        Employee evaluator = employeeRepository.findById(evaluatorId)
            .orElseThrow(() -> new IllegalArgumentException("평가자 정보를 찾을 수 없습니다."));

        WorkEvaluation evaluation = WorkEvaluation.builder()
            .employee(employee)
            .evaluationYear(request.getYear())
            .evaluationQuarter(quarter)
            .attitudeScore(request.getWorkAttitude())
            .achievementScore(request.getGoalAchievement())
            .collaborationScore(request.getCollaboration())
            .contributionGrade(request.getContribution())
            .evaluator(evaluator)
            .build();

        WorkEvaluation saved = workEvaluationRepository.save(evaluation);
        return WorkEvaluationDto.Response.from(saved);
    }

    // 5. 평가 수정
    @Transactional
    public WorkEvaluationDto.Response updateEvaluation(
            Long evaluationId, 
            WorkEvaluationDto.UpdateRequest request) {

        WorkEvaluation evaluation = workEvaluationRepository.findById(evaluationId)
            .orElseThrow(() -> new IllegalArgumentException("평가 정보를 찾을 수 없습니다."));

        // 현재 로그인한 사용자를 평가자로 자동 설정
        Long evaluatorId = SecurityUtil.getCurrentEmployeeId();
        Employee evaluator = employeeRepository.findById(evaluatorId)
            .orElseThrow(() -> new IllegalArgumentException("평가자 정보를 찾을 수 없습니다."));
        
        Integer targetQuarter = (request.getQuarter() != null) 
            ? parseQuarterLabel(request.getQuarter()) 
            : evaluation.getEvaluationQuarter();

        WorkEvaluation updated = WorkEvaluation.builder()
            .evaluationId(evaluation.getEvaluationId())
            .employee(evaluation.getEmployee())
            .evaluationYear(request.getYear() != null ? request.getYear() : evaluation.getEvaluationYear())
            .evaluationQuarter(targetQuarter)
            .attitudeScore(request.getWorkAttitude() != null ? request.getWorkAttitude() : evaluation.getAttitudeScore())
            .achievementScore(request.getGoalAchievement() != null ? request.getGoalAchievement() : evaluation.getAchievementScore())
            .collaborationScore(request.getCollaboration() != null ? request.getCollaboration() : evaluation.getCollaborationScore())
            .contributionGrade(request.getContribution() != null ? request.getContribution() : evaluation.getContributionGrade())
            .evaluator(evaluator)
            .createdAt(evaluation.getCreatedAt())
            .build();

        WorkEvaluation saved = workEvaluationRepository.save(updated);
        return WorkEvaluationDto.Response.from(saved);
    }

    // --- Helper Methods ---

    private Integer parseQuarterLabel(String quarterLabel) {
        if (quarterLabel == null || quarterLabel.isBlank()) return null;
        String normalized = quarterLabel.replaceAll("[^0-9]", "");
        try {
            return Integer.parseInt(normalized);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String normalizeDepartmentName(String departmentName) {
        if (departmentName == null || departmentName.isBlank() || "선택".equals(departmentName)) {
            return null;
        }
        return departmentName;
    }

    private List<WorkEvaluation> fetchWithoutDepartment(Integer year, Integer quarter) {
        if (year != null && quarter != null) return workEvaluationRepository.findByEvaluationYearAndEvaluationQuarter(year, quarter);
        if (year != null) return workEvaluationRepository.findByEvaluationYear(year);
        return workEvaluationRepository.findAll();
    }

    private List<WorkEvaluation> fetchByDepartment(String departmentName, Integer year, Integer quarter) {
        if (year != null && quarter != null) {
            return workEvaluationRepository
                .findByEmployee_Department_DepartmentNameAndEvaluationYearAndEvaluationQuarter(
                    departmentName, year, quarter);
        }
        if (year != null) {
            return workEvaluationRepository
                .findByEmployee_Department_DepartmentNameAndEvaluationYear(departmentName, year);
        }
        return workEvaluationRepository.findByEmployee_Department_DepartmentName(departmentName);
    }
}