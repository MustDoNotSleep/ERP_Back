package com.erp.service;

import com.erp.dto.AiDto;
import com.erp.entity.WorkEvaluation;
import com.erp.repository.WorkEvaluationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AiService {

    private final WorkEvaluationRepository workEvaluationRepository;
    private final RestTemplate restTemplate;

    // 파이썬 서버 주소
    private final String AI_SERVER_URL = "http://127.0.0.1:8000/ai/recommend";

    @Transactional(readOnly = true)
    public List<AiDto.Recommendation> getAiRecommendations(Integer year, Integer quarter) {
        System.out.println("============== [AI 서비스 시작] ==============");
        System.out.println("1. 조회 조건: " + year + "년 " + quarter + "분기");

        // 1. 평가 데이터 조회
        List<WorkEvaluation> evaluations = workEvaluationRepository
            .findByEvaluationYearAndEvaluationQuarter(year, quarter);

        System.out.println("2. DB 조회 결과: " + evaluations.size() + "건 발견");

        if (evaluations.isEmpty()) {
            System.out.println("❌ 데이터가 없어서 중단합니다.");
            throw new IllegalArgumentException("분석할 평가 데이터가 없습니다 (" + year + "년 " + quarter + "분기)");
        }

        // 2. DTO 변환
        List<AiDto.Candidate> candidates = evaluations.stream()
            .map(e -> AiDto.Candidate.builder()
                .name(e.getEmployee().getName())
                .department(e.getEmployee().getDepartment() != null ? e.getEmployee().getDepartment().getDepartmentName() : "소속미정") // getTeamName() 확인 필요
                .total_score(calcTotalScore(e)) 
                .comment(e.getTotalGrade() != null ? e.getTotalGrade() : "평가 없음") 
                .build())
            .collect(Collectors.toList());

        System.out.println("3. 파이썬으로 보낼 데이터 준비 완료 (" + candidates.size() + "명)");

        // 3. 파이썬 서버 요청
        AiDto.AiRequest request = new AiDto.AiRequest(candidates);
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<AiDto.AiRequest> entity = new HttpEntity<>(request, headers);

            System.out.println("4. 파이썬 서버(" + AI_SERVER_URL + ")로 POST 요청 전송 중...");
            
            AiDto.AiResponse response = restTemplate.postForObject(
                AI_SERVER_URL, 
                entity, 
                AiDto.AiResponse.class
            );
            
            System.out.println("5. 파이썬 응답 수신 완료: " + (response != null ? response.getStatus() : "null"));
            
            if (response != null && "success".equals(response.getStatus())) {
                System.out.println("✅ AI 추천 완료! (" + response.getRecommendations().size() + "명 추천됨)");
                return response.getRecommendations();
            } else {
                throw new RuntimeException("AI 분석 실패: 응답 status가 success가 아님");
            }

        } catch (Exception e) {
            System.err.println("❌ [AI 서버 통신 오류 발생]");
            System.err.println("에러 메시지: " + e.getMessage());
            e.printStackTrace(); // 상세 에러 로그 출력
            throw new RuntimeException("AI 서버 연결 오류: " + e.getMessage());
        }
    }

    private Integer calcTotalScore(WorkEvaluation e) {
        int attitude = e.getAttitudeScore() != null ? e.getAttitudeScore() : 0;
        int achievement = e.getAchievementScore() != null ? e.getAchievementScore() : 0;
        int collaboration = e.getCollaborationScore() != null ? e.getCollaborationScore() : 0;
        return attitude + achievement + collaboration;
    }
}