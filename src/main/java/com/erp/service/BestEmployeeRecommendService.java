package com.erp.service;

import com.erp.config.AppConfig;
import com.erp.dto.BestEmployeeDto;
import com.erp.dto.RecommendRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BestEmployeeRecommendService {
    @Value("${lim.api.url}")
    private String llmApiUrl;

    @Value("${openai.api.key}")
    private String openAiApiKey;

    @Value("${ai.model.name}")
    private String llmModelName;

    private final RestTemplate restTemplate;
    private final com.erp.repository.BestEmployeeRecommendRepository recommendRepository;

    /**
     * 평가 데이터 집계 → LLM 프롬프트 생성 → LLM 호출 → 결과 파싱
     */
    public List<BestEmployeeDto> recommendBestEmployees(RecommendRequest request) {
        // 1. 평가 데이터 집계: DB에서 평가 결과 조회
        List<com.erp.entity.WorkEvaluation> evaluations = recommendRepository.findByRecommendRequest(
            request.getEvaluationYear(),
            request.getEvaluationQuarter(),
            request.getDepartmentId()
        );

        // 2. 프롬프트 생성: 평가 데이터 기반 LLM 프롬프트 생성
        String prompt = createPrompt(request, evaluations);

        // 3. LLM API 호출: 프롬프트와 topN 전달, 결과 파싱
        List<BestEmployeeDto> result = callLlmApi(prompt, request.getTopN());

        return result;
    }

    private String createPrompt(RecommendRequest request, List<com.erp.entity.WorkEvaluation> evaluations) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%d년 %d분기 %s 부서의 근무 평가 결과를 바탕으로 우수사원을 추천해줘.\n", 
                request.getEvaluationYear(),
                request.getEvaluationQuarter(),
                request.getDepartmentId() != null ? request.getDepartmentId() : "전체"));
        sb.append("평가 데이터:\n");
        for (com.erp.entity.WorkEvaluation eval : evaluations) {
            String employeeName = null;
            if (eval.getEmployee() != null && eval.getEmployee().getName() != null) {
                employeeName = eval.getEmployee().getName();
            } else {
                employeeName = "";
            }
            Integer attitudeScore = eval.getAttitudeScore();
            Integer achievementScore = eval.getAchievementScore();
            Integer collaborationScore = eval.getCollaborationScore();
            String totalGrade = eval.getTotalGrade();
            String contributionGrade = eval.getContributionGrade();
            sb.append(String.format("- %s: 태도 %d, 성과 %d, 협업 %d, 총점 %s, 등급 %s\n",
                employeeName,
                attitudeScore != null ? attitudeScore : 0,
                achievementScore != null ? achievementScore : 0,
                collaborationScore != null ? collaborationScore : 0,
                totalGrade != null ? totalGrade : "",
                contributionGrade != null ? contributionGrade : ""
            ));
        }
        return sb.toString();
    }

    private List<BestEmployeeDto> callLlmApi(String prompt, Integer topN) {
        // OpenAI Chat API 요청 예시
        try {
            var headers = new org.springframework.http.HttpHeaders();
            headers.set("Authorization", "Bearer " + openAiApiKey);
            headers.set("Content-Type", "application/json");

            var requestBody = new java.util.HashMap<String, Object>();
            requestBody.put("model", llmModelName);
            var messages = new java.util.ArrayList<java.util.Map<String, String>>();
            messages.add(java.util.Map.of("role", "user", "content", prompt));
            requestBody.put("messages", messages);
            requestBody.put("n", topN);

            var entity = new org.springframework.http.HttpEntity<>(requestBody, headers);
            var response = restTemplate.postForEntity(llmApiUrl, entity, String.class);

            var objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
            var root = objectMapper.readTree(response.getBody());
            var choices = root.path("choices");
            java.util.List<BestEmployeeDto> result = new java.util.ArrayList<>();
            for (var choice : choices) {
                String content = choice.path("message").path("content").asText();
                String[] lines = content.split("\n");
                int rank = 1;
                for (String line : lines) {
                    if (line.trim().isEmpty()) continue;
                    String[] parts = line.split("-", 2);
                    if (parts.length == 2) {
                        String name = parts[0].replaceAll("[0-9. ]", "").trim();
                        String reason = parts[1].trim();
                        result.add(BestEmployeeDto.builder()
                            .name(name)
                            .reason(reason)
                            .rank(rank++)
                            .build());
                    }
                }
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
}
