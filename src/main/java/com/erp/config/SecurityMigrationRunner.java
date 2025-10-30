package com.erp.config;

import com.erp.util.SecurityMigrationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("migration")
@RequiredArgsConstructor
public class SecurityMigrationRunner implements CommandLineRunner {

    private final SecurityMigrationUtil securityMigrationUtil;

    @Override
    public void run(String... args) {
        try {
            log.info("Starting security migration process...");
            
            log.info("Migrating passwords to BCrypt...");
            securityMigrationUtil.migratePasswords();
            
            log.info("Migrating RRN to encrypted format...");
            securityMigrationUtil.migrateRRN();
            
            log.info("Security migration completed successfully");
        } catch (Exception e) {
            log.error("Error during security migration", e);
            throw new RuntimeException("Security migration failed", e);
        }
    }
}