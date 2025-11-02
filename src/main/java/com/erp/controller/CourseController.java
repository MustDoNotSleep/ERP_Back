package com.erp.controller;

import com.erp.dto.CourseApplicationDto;
import com.erp.dto.CourseDto;
import com.erp.dto.PageResponse;
import com.erp.service.CourseApplicationService;
import com.erp.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final CourseApplicationService courseApplicationService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PageResponse<CourseDto.Response>>> getAllCourses(
            @RequestParam(required = false) String courseName,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String dateStatus,
            Pageable pageable) {
        Page<CourseDto.Response> courses;
        
        // 필터 파라미터가 있으면 검색, 없으면 전체 조회
        if ((courseName != null && !courseName.trim().isEmpty()) || 
            (status != null && !status.trim().isEmpty()) ||
            (dateStatus != null && !dateStatus.trim().isEmpty())) {
            courses = courseService.searchCourses(courseName, status, dateStatus, pageable);
        } else {
            courses = courseService.getAllCourses(pageable);
        }
        
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(courses)));
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_HR_MANAGER') or #employeeId == authentication.principal.id")
    public ResponseEntity<ApiResponse<List<CourseDto.Response>>> getCoursesByEmployeeId(
            @PathVariable Long employeeId) {
        List<CourseDto.Response> courses = courseService.getCoursesByEmployeeId(employeeId);
        return ResponseEntity.ok(ApiResponse.success(courses));
    }

    @GetMapping("/{courseId}/applications")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_HR_MANAGER')")
    public ResponseEntity<ApiResponse<List<CourseApplicationDto.Response>>> getApplicationsByCourseId(
            @PathVariable Long courseId) {
        List<CourseApplicationDto.Response> applications = courseApplicationService.getApplicationsByCourseId(courseId);
        return ResponseEntity.ok(ApiResponse.success(applications));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_HR_MANAGER')")
    public ResponseEntity<ApiResponse<CourseDto.Response>> createCourse(
            @Valid @RequestBody CourseDto.Request request) {
        CourseDto.Response created = courseService.createCourse(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_HR_MANAGER')")
    public ResponseEntity<ApiResponse<CourseDto.Response>> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody CourseDto.UpdateRequest request) {
        CourseDto.Response updated = courseService.updateCourse(id, request);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    @PutMapping("/{id}/approval")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_HR_MANAGER')")
    public ResponseEntity<ApiResponse<CourseDto.Response>> approveCourse(
            @PathVariable Long id,
            @Valid @RequestBody CourseDto.ApprovalRequest request) {
        CourseDto.Response updated = courseService.approveCourse(id, request);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_HR_MANAGER')")
    public ResponseEntity<ApiResponse<Void>> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
