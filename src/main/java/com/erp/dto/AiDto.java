package com.erp.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AiDto {

    // 1. 파이썬으로 보낼 데이터 (요청)
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AiRequest {
        private List<Candidate> candidates;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Candidate {
        private String name;
        private String teamName;
        private Integer workAttitude;
        private Integer goalAchievement;
        private Integer collaboration;
        private String comment;
    }

    // 2. 파이썬에서 받을 데이터 (응답)
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AiResponse {
        private String status;
        private List<Recommendation> recommendations;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Recommendation {
        private int rank;
        private String name;
        private String teamName;
        private String reason;
    }
}

