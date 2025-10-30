// package com.erp.dto;

// import com.erp.entity.Comment;
// import com.erp.entity.Post;
// import lombok.Builder;
// import lombok.Getter;

// import java.time.LocalDateTime;
// import java.util.List;
// import java.util.stream.Collectors;

// public class PostDto {
    
//     @Getter
//     @Builder
//     public static class Request {
//         private String employeeId;
//         private String title;
//         private String content;
//         private Post.PostCategory category;
//         private List<String> attachments;
//     }
    
//     @Getter
//     @Builder
//     public static class Response {
//         private String id;
//         private String title;
//         private String content;
//         private Post.PostCategory category;
//         private String authorName;
//         private String authorDepartment;
//         private LocalDateTime createdAt;
//         private LocalDateTime updatedAt;
//         private Integer viewCount;
//         private List<String> attachments;
//         private List<CommentResponse> comments;
        
//         public static Response from(Post post) {
//             return Response.builder()
//                 .id(post.getId())
//                 .title(post.getTitle())
//                 .content(post.getContent())
//                 .category(post.getCategory())
//                 .authorName(post.getEmployee().getName())
//                 .authorDepartment(post.getEmployee().getDepartment().getName())
//                 .createdAt(post.getCreatedAt())
//                 .updatedAt(post.getUpdatedAt())
//                 .viewCount(post.getViewCount())
//                 .attachments(post.getAttachments())
//                 .comments(post.getComments().stream()
//                     .filter(comment -> comment.getParent() == null)
//                     .map(CommentResponse::from)
//                     .collect(Collectors.toList()))
//                 .build();
//         }
//     }
    
//     @Getter
//     @Builder
//     public static class UpdateRequest {
//         private String title;
//         private String content;
//         private List<String> attachments;
//     }
    
//     @Getter
//     @Builder
//     public static class CommentRequest {
//         private String employeeId;
//         private String content;
//         private String parentCommentId;
//     }
    
//     @Getter
//     @Builder
//     public static class CommentResponse {
//         private String id;
//         private String content;
//         private String authorName;
//         private String authorDepartment;
//         private LocalDateTime createdAt;
//         private List<CommentResponse> replies;
        
//         public static CommentResponse from(Comment comment) {
//             return CommentResponse.builder()
//                 .id(comment.getId())
//                 .content(comment.getContent())
//                 .authorName(comment.getEmployee().getName())
//                 .authorDepartment(comment.getEmployee().getDepartment().getName())
//                 .createdAt(comment.getCreatedAt())
//                 .replies(comment.getReplies().stream()
//                     .map(CommentResponse::from)
//                     .collect(Collectors.toList()))
//                 .build();
//         }
//     }
    
//     @Getter
//     @Builder
//     public static class ListResponse {
//         private String id;
//         private String title;
//         private Post.PostCategory category;
//         private String authorName;
//         private LocalDateTime createdAt;
//         private Integer viewCount;
//         private Integer commentCount;
        
//         public static ListResponse from(Post post) {
//             return ListResponse.builder()
//                 .id(post.getId())
//                 .title(post.getTitle())
//                 .category(post.getCategory())
//                 .authorName(post.getEmployee().getName())
//                 .createdAt(post.getCreatedAt())
//                 .viewCount(post.getViewCount())
//                 .commentCount(countTotalComments(post))
//                 .build();
//         }
        
//         private static int countTotalComments(Post post) {
//             return post.getComments().size();
//         }
//     }
// }