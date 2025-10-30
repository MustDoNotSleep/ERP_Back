package com.erp.util;

import com.erp.entity.Employee;
import com.erp.repository.EmployeeRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.List;

@Component
public class SecurityMigrationUtil {
    
    private final EmployeeRepository employeeRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final KeyGenerator keyGenerator;
    private final Cipher cipher;
    
    public SecurityMigrationUtil(EmployeeRepository employeeRepository) throws Exception {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.keyGenerator = KeyGenerator.getInstance("AES");
        this.keyGenerator.init(256);
        this.cipher = Cipher.getInstance("AES/GCM/NoPadding");
    }
    
    @Transactional
    public void migratePasswords() {
        List<Employee> employees = employeeRepository.findByPasswordStartingWith("NEEDS_HASH:");
        
        for (Employee employee : employees) {
            String plainPassword = employee.getPassword().substring("NEEDS_HASH:".length());
            String hashedPassword = passwordEncoder.encode(plainPassword);
            employee.updatePassword(hashedPassword);
            employeeRepository.save(employee);
        }
    }
    
    @Transactional
    public void migrateRRN() throws Exception {
        List<Employee> employees = employeeRepository.findByRrnStartingWith("NEEDS_ENC:");
        SecretKey key = keyGenerator.generateKey();
        
        for (Employee employee : employees) {
            String plainRRN = employee.getRrn().substring("NEEDS_ENC:".length());
            
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedRRN = cipher.doFinal(plainRRN.getBytes());
            String encodedRRN = "ENC:" + Base64.getEncoder().encodeToString(encryptedRRN);
            
            employee.updateRrn(encodedRRN);
            employeeRepository.save(employee);
        }
        
        // Save encryption key to database
        saveEncryptionKey(key);
    }
    
    private void saveEncryptionKey(SecretKey key) {
        // Implementation for saving key to encryption_keys table
        // This should be implemented securely, possibly using a key management service
    }
}