package com.erp.service;

import com.erp.dto.WorkEvaluationDto;
import com.erp.entity.WorkEvaluation;
import com.erp.entity.Employee;
import com.erp.repository.WorkEvaluationRepository;
import com.erp.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WorkEvaluationService {
	private final WorkEvaluationRepository workEvaluationRepository;
	private final EmployeeRepository employeeRepository;

	// 1. 관리자: 부서별 평가 내역 조회
	public List<WorkEvaluationDto.Response> getEvaluationsByDepartment(Long departmentId, int year, int quarter, Long managerId) {
		// 1) 관리자 검증: managerId가 해당 부서의 관리자 직급인지 확인
		Employee manager = employeeRepository.findById(managerId)
			.orElseThrow(() -> new IllegalArgumentException("관리자 정보 없음"));
		if (!manager.isManager() || !manager.getDepartment().getId().equals(departmentId)) {
			throw new IllegalAccessError("해당 부서의 관리자만 조회할 수 있습니다.");
		}

		// 2) 해당 부서의 평가 내역 반환
		List<WorkEvaluation> evaluations = workEvaluationRepository.findByDepartmentIdAndEvaluationYearAndEvaluationQuarter(departmentId, year, quarter);
		return evaluations.stream()
			.map(WorkEvaluationDto.Response::from)
			.toList();
	}

	// 2. 직원: 본인 평가 조회(분기별 1회만 가능)
	public WorkEvaluationDto.Response getMyEvaluation(Long employeeId, int year, int quarter) {
		// TODO: 실제 구현 필요
		throw new UnsupportedOperationException("Not implemented yet");
	}

	// 3. 직원: 평가 저장/제출 (분기별 1회만 가능)
	public WorkEvaluationDto.Response saveOrSubmitEvaluation(Long employeeId, WorkEvaluationDto.UpdateRequest request, int year, int quarter) {
		// 이미 해당 분기 평가가 있으면 수정 불가
		Optional<WorkEvaluation> existing = workEvaluationRepository.findByEmployeeIdAndEvaluationYearAndEvaluationQuarter(employeeId, year, quarter);
		if (existing.isPresent()) {
			throw new IllegalStateException("이미 해당 분기 평가가 존재합니다.");
		}

		Employee employee = employeeRepository.findById(employeeId)
			.orElseThrow(() -> new IllegalArgumentException("직원 정보 없음"));

		WorkEvaluation evaluation = WorkEvaluation.builder()
			.employee(employee)
			.evaluationYear(year)
			.evaluationQuarter(quarter)
			.attitudeScore(request.getAttitudeScore())
			.achievementScore(request.getAchievementScore())
			.collaborationScore(request.getCollaborationScore())
			.contributionGrade(request.getContributionGrade())
			.totalGrade(request.getTotalGrade())
			.status(request.getStatus()) // "임시저장" 또는 "제출완료" 그대로 저장
			.build();

		workEvaluationRepository.save(evaluation);
		return WorkEvaluationDto.Response.from(evaluation);
	}

	// 4. 평가 수정 (제출 전까지만 가능)
	public WorkEvaluationDto.Response updateEvaluation(Long evaluationId, WorkEvaluationDto.UpdateRequest request, Long employeeId) {
		// TODO: 실제 구현 필요
		throw new UnsupportedOperationException("Not implemented yet");
	}
}
