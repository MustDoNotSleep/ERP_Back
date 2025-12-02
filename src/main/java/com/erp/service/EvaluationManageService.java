package com.erp.service;

import com.erp.dto.EvaluationRequestDto;
import com.erp.entity.Employee;
import com.erp.entity.EvaluationPolicy;
import com.erp.entity.EvaluationPolicyDetail;
import com.erp.repository.EmployeeRepository;
import com.erp.repository.EvaluationPolicyDetailRepository;
import com.erp.repository.EvaluationPolicyRepository;
import com.erp.util.SecurityUtil; // ⭐ SecurityUtil 임포트 필수!
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EvaluationManageService {

    private final EvaluationPolicyRepository policyRepository;
    private final EvaluationPolicyDetailRepository detailRepository;
    private final EmployeeRepository employeeRepository;

    // 파일 업로드 경로 (환경에 맞게 설정 확인)
    private final String UPLOAD_DIR = "C:/erp/uploads/evaluation/";

    // =================================================================================
    // 🔍 [GET] 조회 로직
    // =================================================================================

    /**
     * 1. 평가 시즌 목록 조회
     */
@Transactional(readOnly = true)
    public List<EvaluationRequestDto> findAllPolicies() {
        // ⭐ findAllByOrderByCreatedAtDesc -> findAllByOrderByPolicyIdDesc 로 변경
        return policyRepository.findAllByOrderByPolicyIdDesc().stream()
                .map(policy -> EvaluationRequestDto.builder()
                        .policyId(policy.getPolicyId())
                        .seasonName(policy.getSeasonName())
                        .startDate(policy.getStartDate())
                        .endDate(policy.getEndDate())
                        .build())
                .collect(Collectors.toList());
    }
@Transactional(readOnly = true)
    public EvaluationRequestDto getEvaluationProgress(String seasonName, Long deptId, Long posId) {
        
        // 1. 위에서 만든 Repository 메소드로 조건에 맞는 데이터를 싹 가져옵니다.
        // (예: "2024년" + "개발팀"에 해당하는 사람들 리스트)
        List<EvaluationPolicyDetail> details = detailRepository.findBySearchCriteria(seasonName, deptId, posId);

        long total = details.size();

        long completed = details.stream()
                .filter(d -> d.getFinalScore() != null)
                .count();

        return EvaluationRequestDto.builder()
                .totalCount(total)
                .completedCount(completed)
                .build();
    }


    @Transactional
    public void createEvaluationPolicy(EvaluationRequestDto dto, MultipartFile file) throws IOException {
        
        // ⭐ [수정 핵심] SecurityUtil을 통해 로그인한 사용자의 ID를 가져옵니다.
        // 로그인이 안 되어 있다면 여기서 IllegalStateException이 발생하여 자동으로 막힙니다.
        Long creatorId = SecurityUtil.getCurrentEmployeeId();

        // 2. 파일 저장 로직
        String originalFilename = file.getOriginalFilename();
        String savedFileName = UUID.randomUUID() + "_" + originalFilename;
        String filePath = UPLOAD_DIR + savedFileName;
        File dest = new File(filePath);
        if (!dest.getParentFile().exists()) dest.getParentFile().mkdirs();
        file.transferTo(dest);

        // 3. 엔티티 저장
        // (가중치 null 체크 로직 포함)
        int kpiW = dto.getKpiWeight() != null ? dto.getKpiWeight() : 70;
        int leadW = dto.getLeadershipWeight() != null ? dto.getLeadershipWeight() : 30;

        EvaluationPolicy policy = EvaluationPolicy.builder()
                .seasonName(dto.getSeasonName())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .evaluationType(dto.getEvaluationType())
                .performanceWeight(kpiW)
                .competencyWeight(leadW)
                .targetDepartmentId(dto.getTargetDepartmentId()) 
                .targetPositionId(dto.getTargetPositionId())     
                .evaluationFormPath(filePath)
                .originalFileName(originalFilename)
                
                // ⭐ [수정] 위에서 가져온 creatorId를 자동으로 주입
                .createdById(creatorId) 
                
                .build();

        EvaluationPolicy savedPolicy = policyRepository.save(policy);

        // 4. 엑셀 파싱 및 상세 저장
        parseAndSaveDetails(dest.toPath(), savedPolicy);
    }

    private void parseAndSaveDetails(Path savedFilePath, EvaluationPolicy policy) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(Files.newInputStream(savedFilePath))) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row == null || row.getRowNum() == 0 || isRowEmpty(row)) continue;
                try {
                    Long empId = getLongCellValue(row.getCell(0));
                    Double score = getNumericCellValue(row.getCell(5));
                    if (empId == null || score == null) continue;

                    String grade = calculateGrade(score);
                    
                    // 엑셀에 있는 사원이 실제 존재하는지 확인
                    Employee employee = employeeRepository.findById(empId)
                            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사번(엑셀): " + empId));
                    
                    String teamName = employee.getDepartment() != null ? employee.getDepartment().getTeamName() : null;

                    EvaluationPolicyDetail detail = EvaluationPolicyDetail.builder()
                            .evaluationPolicy(policy)
                            .employee(employee)
                            .employeeName(employee.getName())
                            .teamName(teamName)
                            .finalScore(score)
                            .finalGrade(grade)
                            .build();
                    detailRepository.save(detail);
                } catch (Exception e) {
                    System.err.println("파싱 에러: " + e.getMessage());
                }
            }
        }
    }

    // --- 유틸리티 메소드 (그대로 유지) ---
    private String calculateGrade(double score) {
        if (score >= 90) return "S";
        if (score >= 80) return "A";
        if (score >= 70) return "B";
        if (score >= 60) return "C";
        return "D";
    }

    private boolean isRowEmpty(Row row) {
        for (int i = 0; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK) return false;
        }
        return true;
    }

    private Long getLongCellValue(Cell cell) {
        if (cell == null) return null;
        try {
            if (cell.getCellType() == CellType.NUMERIC) return (long) cell.getNumericCellValue();
            else if (cell.getCellType() == CellType.STRING) return Long.parseLong(cell.getStringCellValue().trim());
            else if (cell.getCellType() == CellType.FORMULA) return (long) cell.getNumericCellValue();
        } catch (Exception ignored) {}
        return null;
    }

    private Double getNumericCellValue(Cell cell) {
        if (cell == null) return null;
        try {
            if (cell.getCellType() == CellType.NUMERIC) return cell.getNumericCellValue();
            else if (cell.getCellType() == CellType.STRING) return Double.parseDouble(cell.getStringCellValue().trim());
            else if (cell.getCellType() == CellType.FORMULA) return cell.getNumericCellValue();
        } catch (Exception ignored) {}
        return null;
    }
}