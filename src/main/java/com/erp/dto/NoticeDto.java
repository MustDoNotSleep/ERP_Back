package com.erp.dto;

import com.erp.entity.Notice;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class NoticeDto {
    
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        @NotBlank(message = "제목은 필수입니다")
        private String title;
        
        @NotBlank(message = "내용은 필수입니다")
        private String content;
        
        @NotNull(message = "중요 공지 여부는 필수입니다")
        private Boolean isImportant;
    }
    
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateRequest {
        @NotBlank(message = "제목은 필수입니다")
        private String title;
        
        @NotBlank(message = "내용은 필수입니다")
        private String content;
        
        @NotNull(message = "중요 공지 여부는 필수입니다")
        private Boolean isImportant;
    }
    
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String title;
        private String content;
        private Long authorId;
        private String authorName;
        private String authorDepartment;
        private Boolean isImportant;
        private Boolean isActive;
        private Integer viewCount;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        
        public static Response from(Notice notice) {
            return Response.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .authorId(notice.getAuthor().getId())
                .authorName(notice.getAuthor().getName())
                .authorDepartment(notice.getAuthor().getDepartment() != null ? 
                    notice.getAuthor().getDepartment().getDepartmentName() : null)
                .isImportant(notice.getIsImportant())
                .isActive(notice.getIsActive())
                .viewCount(notice.getViewCount())
                .createdAt(notice.getCreatedAt())
                .updatedAt(notice.getUpdatedAt())
                .build();
        }
    }
    
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Summary {
        private Long id;
        private String title;
        private String authorName;
        private Boolean isImportant;
        private Integer viewCount;
        private LocalDateTime createdAt;
        
        public static Summary from(Notice notice) {
            return Summary.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .authorName(notice.getAuthor().getName())
                .isImportant(notice.getIsImportant())
                .viewCount(notice.getViewCount())
                .createdAt(notice.getCreatedAt())
                .build();
        }
    }
}
