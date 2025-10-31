package com.erp.controller;

import com.erp.dto.CourseDto;
import com.erp.dto.PageResponse;
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

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PageResponse<CourseDto.Response>>> getAllCourses(Pageable pageable) {
        Page<CourseDto.Response> courses = courseService.getAllCourses(pageable);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(courses)));
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER') or #employeeId == authentication.principal.id")
    public ResponseEntity<ApiResponse<List<CourseDto.Response>>> getCoursesByEmployeeId(
            @PathVariable Long employeeId) {
        List<CourseDto.Response> courses = courseService.getCoursesByEmployeeId(employeeId);
        return ResponseEntity.ok(ApiResponse.success(courses));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<ApiResponse<CourseDto.Response>> createCourse(
            @Valid @RequestBody CourseDto.Request request) {
        CourseDto.Response created = courseService.createCourse(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<ApiResponse<CourseDto.Response>> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody CourseDto.UpdateRequest request) {
        CourseDto.Response updated = courseService.updateCourse(id, request);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<ApiResponse<Void>> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
