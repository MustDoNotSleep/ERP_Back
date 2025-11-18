// package com.erp.controller;

// import com.erp.dto.BestEmployeeDto;
// import com.erp.dto.RecommendRequest;
// import com.erp.service.BestEmployeeRecommendService;
// import lombok.RequiredArgsConstructor;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import java.util.List;

// @RestController
// @RequiredArgsConstructor
// @RequestMapping("/api/recommend")
// public class BestEmployeeRecommendController {
//     private final BestEmployeeRecommendService recommendService;

//         @PostMapping("/best-employees")
//         @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
//         public ResponseEntity<List<BestEmployeeDto>> recommendBestEmployees(@RequestBody RecommendRequest request) {
//             List<BestEmployeeDto> result = recommendService.recommendBestEmployees(request);
//             return ResponseEntity.ok(result);
//         }
// }
