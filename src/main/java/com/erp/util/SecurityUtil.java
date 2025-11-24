package com.erp.util;

import com.erp.entity.Employee;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Spring Security 관련 유틸리티
 * 현재 로그인한 사용자 정보를 가져오는 메서드 제공
 */
public class SecurityUtil {
    
    /**
     * 현재 인증된 사용자의 Employee 객체를 가져옵니다.
     * 
     * @return 현재 로그인한 Employee 객체
     * @throws IllegalStateException 인증되지 않은 경우
     */
    public static Employee getCurrentEmployee() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("인증되지 않은 사용자입니다.");
        }
        
        Object principal = authentication.getPrincipal();
        
        if (principal instanceof Employee) {
            return (Employee) principal;
        }
        
        // UserDetails를 구현한 Employee인 경우
        if (principal instanceof UserDetails) {
            // Employee는 UserDetails를 구현하므로 캐스팅 가능
            return (Employee) principal;
        }
        
        throw new IllegalStateException("사용자 정보를 가져올 수 없습니다.");
    }
    
    /**
     * 현재 인증된 사용자의 ID를 가져옵니다.
     * 
     * @return 현재 로그인한 사용자의 ID
     * @throws IllegalStateException 인증되지 않은 경우
     */
    public static Long getCurrentEmployeeId() {
        return getCurrentEmployee().getId();
    }
    
    /**
     * 현재 인증된 사용자의 이메일을 가져옵니다.
     * 
     * @return 현재 로그인한 사용자의 이메일
     * @throws IllegalStateException 인증되지 않은 경우
     */
    public static String getCurrentEmployeeEmail() {
        return getCurrentEmployee().getEmail();
    }
    
    /**
     * 현재 사용자가 인증되었는지 확인합니다.
     * 
     * @return 인증되었으면 true, 아니면 false
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && 
               authentication.isAuthenticated() && 
               !"anonymousUser".equals(authentication.getPrincipal());
    }
}
