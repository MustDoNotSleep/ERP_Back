// package com.erp.entity;

// import jakarta.persistence.*;
// import lombok.*;

// import java.util.ArrayList;
// import java.util.List;

// @Entity
// @Table(name = "posts")
// @Getter
// @NoArgsConstructor(access = AccessLevel.PROTECTED)
// @AllArgsConstructor
// @Builder
// public class Post extends BaseEntity {
    
//     @Id
//     @GeneratedValue(strategy = GenerationType.UUID)
//     @Column(name = "postId")
//     private String id;
    
//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "employeeId")
//     private Employee employee;
    
//     @Column(nullable = false)
//     private String title;
    
//     @Column(nullable = false, columnDefinition = "TEXT")
//     private String content;
    
//     @Enumerated(EnumType.STRING)
//     @Column(nullable = false)
//     private PostCategory category;
    
//     private Integer viewCount;
    
//     @ElementCollection
//     @CollectionTable(name = "post_attachments",
//         joinColumns = @JoinColumn(name = "postId"))
//     @Column(name = "file_url")
//     private List<String> attachments = new ArrayList<>();
    
//     public enum PostCategory {
//         NOTICE, NEWS, EVENT, GENERAL
//     }
    
//     // Business methods
//     public void incrementViewCount() {
//         this.viewCount = (this.viewCount == null ? 1 : this.viewCount + 1);
//     }
    
//     public void updateContent(String title, String content) {
//         this.title = title;
//         this.content = content;
//     }
    
//     public void addComment(Comment comment) {
//         this.comments.add(comment);
//         comment.setPost(this);
//     }
    
//     public void addAttachment(String fileUrl) {
//         this.attachments.add(fileUrl);
//     }
// }

// @Entity
// @Table(name = "comments")
// @Getter
// @NoArgsConstructor(access = AccessLevel.PROTECTED)
// @AllArgsConstructor
// @Builder
// class Comment extends BaseEntity {
    
//     @Id
//     @GeneratedValue(strategy = GenerationType.UUID)
//     @Column(name = "commentId")
//     private String id;
    
//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "postId")
//     private Post post;
    
//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "employeeId")
//     private Employee employee;
    
//     @Column(nullable = false)
//     private String content;
    
//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "parentId")
//     private Comment parent;
    
//     @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
//     private List<Comment> replies = new ArrayList<>();
    
//     // Package private setter for bidirectional relationship
//     void setPost(Post post) {
//         this.post = post;
//     }
    
//     public void addReply(Comment reply) {
//         this.replies.add(reply);
//         reply.parent = this;
//     }
// }