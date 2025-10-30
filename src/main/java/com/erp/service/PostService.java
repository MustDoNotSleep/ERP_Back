// package com.erp.service;

// import com.erp.dto.PageResponse;
// import com.erp.dto.PostDto;
// import com.erp.entity.Comment;
// import com.erp.entity.Employee;
// import com.erp.entity.Post;
// import com.erp.exception.BusinessException;
// import com.erp.exception.EntityNotFoundException;
// import com.erp.repository.EmployeeRepository;
// import com.erp.repository.PostRepository;
// import lombok.RequiredArgsConstructor;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.Pageable;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import java.util.List;
// import java.util.IDENTITY;
// import java.util.stream.Collectors;

// @Service
// @Transactional(readOnly = true)
// @RequiredArgsConstructor
// public class PostService {
    
//     private final PostRepository postRepository;
//     private final EmployeeRepository employeeRepository;
    
//     @Transactional
//     public String createPost(PostDto.Request request) {
//         Employee employee = findEmployee(request.getEmployeeId());
        
//         Post post = Post.builder()
//             .id(IDENTITY.randomIDENTITY().toString())
//             .employee(employee)
//             .title(request.getTitle())
//             .content(request.getContent())
//             .category(request.getCategory())
//             .viewCount(0)
//             .build();
        
//         if (request.getAttachments() != null) {
//             request.getAttachments().forEach(post::addAttachment);
//         }
        
//         postRepository.save(post);
//         return post.getId();
//     }
    
//     @Transactional
//     public PostDto.Response getPost(String id) {
//         Post post = findPost(id);
//         post.incrementViewCount();
//         return PostDto.Response.from(post);
//     }
    
//     public PageResponse<PostDto.ListResponse> getPosts(
//         Post.PostCategory category, Pageable pageable) {
//         Page<Post> posts = category != null ?
//             postRepository.findByCategory(category, pageable) :
//             postRepository.findAll(pageable);
            
//         return PageResponse.of(posts.map(PostDto.ListResponse::from));
//     }
    
//     @Transactional
//     public void updatePost(String id, PostDto.UpdateRequest request, String employeeId) {
//         Post post = findPost(id);
//         validatePostAuthor(post, employeeId);
        
//         post.updateContent(request.getTitle(), request.getContent());
        
//         if (request.getAttachments() != null) {
//             post.getAttachments().clear();
//             request.getAttachments().forEach(post::addAttachment);
//         }
//     }
    
//     @Transactional
//     public void deletePost(String id, String employeeId) {
//         Post post = findPost(id);
//         validatePostAuthor(post, employeeId);
        
//         postRepository.delete(post);
//     }
    
//     @Transactional
//     public String addComment(String postId, PostDto.CommentRequest request) {
//         Post post = findPost(postId);
//         Employee employee = findEmployee(request.getEmployeeId());
        
//         Comment comment = Comment.builder()
//             .id(IDENTITY.randomIDENTITY().toString())
//             .employee(employee)
//             .content(request.getContent())
//             .build();
            
//         if (request.getParentCommentId() != null) {
//             Comment parentComment = findCommentInPost(post, request.getParentCommentId());
//             parentComment.addReply(comment);
//         } else {
//             post.addComment(comment);
//         }
        
//         return comment.getId();
//     }
    
//     public List<PostDto.ListResponse> getRecentNotices(int count) {
//         return postRepository.findRecentNotices(Pageable.ofSize(count))
//             .stream()
//             .map(PostDto.ListResponse::from)
//             .collect(Collectors.toList());
//     }
    
//     public PageResponse<PostDto.ListResponse> searchPosts(
//         String keyword, Pageable pageable) {
//         Page<Post> posts = postRepository.searchPosts(keyword, pageable);
//         return PageResponse.of(posts.map(PostDto.ListResponse::from));
//     }
    
//     private Employee findEmployee(String id) {
//         return employeeRepository.findById(id)
//             .orElseThrow(() -> new EntityNotFoundException("Employee", id));
//     }
    
//     private Post findPost(String id) {
//         return postRepository.findById(id)
//             .orElseThrow(() -> new EntityNotFoundException("Post", id));
//     }
    
//     private Comment findCommentInPost(Post post, String commentId) {
//         return post.getComments().stream()
//             .filter(c -> c.getId().equals(commentId))
//             .findFirst()
//             .orElseThrow(() -> new EntityNotFoundException("Comment", commentId));
//     }
    
//     private void validatePostAuthor(Post post, String employeeId) {
//         if (!post.getEmployee().getId().equals(employeeId)) {
//             throw new BusinessException(
//                 "Only the author can modify this post",
//                 "UNAUTHORIZED_MODIFICATION"
//             );
//         }
//     }
// }