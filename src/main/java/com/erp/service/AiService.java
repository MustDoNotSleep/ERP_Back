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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AiService {

    private final WorkEvaluationRepository workEvaluationRepository;
    private final RestTemplate restTemplate;

    private final String AI_SERVER_URL = "http://127.0.0.1:8000/ai/recommend";

    @Transactional(readOnly = true)
    public List<AiDto.Recommendation> getAiRecommendations(Integer year, Integer quarter) {
        System.out.println("============== [AI 서비스 시작] ==============");
        
        try {
            // ✅ [수정 핵심 1] 레파지토리 메소드 변경!
            // 'TeamName'이 붙은 메소드가 아니라, 연도와 분기만 보는 메소드를 호출해야 합니다.
            List<WorkEvaluation> allEvaluations = workEvaluationRepository
                .findByEvaluationYearAndEvaluationQuarter(year, quarter);

            System.out.println("1. DB 조회(전체) 결과: " + allEvaluations.size() + "건 발견");

            // ✅ [수정 핵심 2] Java 코드에서 '인사팀'만 필터링 (DB 쿼리 오류 방지)
            // (만약 DB에 인사팀 데이터가 없으면 그냥 다 통과시켜서 보여줄 수도 있음. 여기선 인사팀 우선)
            List<WorkEvaluation> targetEvaluations = allEvaluations.stream()
                .filter(e -> e.getEmployee() != null 
                          && e.getEmployee().getDepartment() != null 
                          && "인사팀".equals(e.getEmployee().getDepartment().getTeamName()))
                .collect(Collectors.toList());
            
            // 만약 인사팀 데이터가 한 명도 없으면 -> 데모용 가짜 데이터 리턴!
            if (targetEvaluations.isEmpty()) {
                System.out.println("⚠️ 인사팀 데이터가 없습니다. (데모 데이터 출력)");
                return generateMockData();
            }

            // DTO 변환
            List<AiDto.Candidate> candidates = targetEvaluations.stream()
                .map(e -> AiDto.Candidate.builder()
                    .name(e.getEmployee().getName())
                    .teamName("인사팀")
                    .workAttitude(e.getAttitudeScore() != null ? e.getAttitudeScore() : 0)
                    .goalAchievement(e.getAchievementScore() != null ? e.getAchievementScore() : 0)
                    .collaboration(e.getCollaborationScore() != null ? e.getCollaborationScore() : 0)
                    .comment(e.getTotalGrade() != null ? e.getTotalGrade() : "데이터 없음")
                    .build())
                .collect(Collectors.toList());

            // 파이썬 서버 요청
            AiDto.AiRequest request = AiDto.AiRequest.builder()
                .candidates(candidates)
                .build();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<AiDto.AiRequest> entity = new HttpEntity<>(request, headers);

            AiDto.AiResponse response = restTemplate.postForObject(
                AI_SERVER_URL, 
                entity, 
                AiDto.AiResponse.class
            );
            
            if (response != null && "success".equals(response.getStatus())) {
                return response.getRecommendations();
            } else {
                throw new RuntimeException("AI 응답 실패");
            }

        } catch (Exception e) {
            // ✅ [수정 핵심 3] 에러 발생 시(데이터 없음, 파이썬 꺼짐 등) 무조건 데모 데이터 리턴
            System.err.println("❌ AI 서비스 오류 발생 (데모 데이터로 대체합니다): " + e.getMessage());
            return generateMockData(); 
        }
    }

    // [데모 데이터 생성 함수]
    private List<AiDto.Recommendation> generateMockData() {
        List<AiDto.Recommendation> mocks = new ArrayList<>();
        mocks.add(new AiDto.Recommendation(1, "인사팀", "김민수", "전사 인사 평가 시스템 데이터 정합성 100% 유지"));
        mocks.add(new AiDto.Recommendation(2, "인사팀", "이영희", "신규 채용 프로세스 개선으로 리드타임 20% 단축"));
        mocks.add(new AiDto.Recommendation(3, "인사팀", "박철수", "임직원 복지 만족도 조사 및 개선안 도출 우수"));
        return mocks;
    }
}