package com.erp.controller;

import com.erp.dto.SalaryDto;
import com.erp.service.SalaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/salary")
@RequiredArgsConstructor
public class SalaryController {
    private final SalaryService salaryService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<SalaryDto.Response> createSalary(@RequestBody SalaryDto.Request request) {
        SalaryDto.Response response = salaryService.createSalary(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<SalaryDto.Response> getSalary(@PathVariable Long id) {
        SalaryDto.Response response = salaryService.getSalary(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER') or #employeeId == authentication.principal.id")
    public ResponseEntity<List<SalaryDto.Response>> getEmployeeSalaries(@PathVariable Long employeeId) {
        List<SalaryDto.Response> responses = salaryService.getEmployeeSalaries(employeeId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/month/{yearMonth}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<List<SalaryDto.Response>> getMonthlySalaries(@PathVariable String yearMonth) {
        YearMonth parsedYearMonth = YearMonth.parse(yearMonth);
        List<SalaryDto.Response> responses = salaryService.getMonthlySalaries(parsedYearMonth);
        return ResponseEntity.ok(responses);
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<SalaryDto.Response> putSalary(@PathVariable Long id, @RequestBody SalaryDto.Request request) {
        SalaryDto.Response response = salaryService.updateSalary(id, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/confirm")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<Void> confirmSalary(@PathVariable Long id) {
        salaryService.confirmSalary(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/pay")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<Void> markAsPaid(@PathVariable Long id) {
        salaryService.markAsPaid(id);
        return ResponseEntity.ok().build();
    }
}
