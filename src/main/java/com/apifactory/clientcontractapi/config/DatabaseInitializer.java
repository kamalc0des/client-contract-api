package com.apifactory.clientcontractapi.config;

import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;

/**
 * Handles initial database population:
 * - Executes data.sql only if DB is empty and no backup is found.
 */
@Component
public class DatabaseInitializer {

    private static final Logger logger = LoggerFactory.getLogger(BackupConfig.class);

    private final DataSource dataSource;

    @Value("${backup.folder:./backups}")
    private String backupFolder;

    public DatabaseInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    @Transactional
    public void initializeDatabase() {
        try (Connection conn = dataSource.getConnection()) {
            // Check if backup exists
            File backupDir = new File(backupFolder);
            boolean backupExists = backupDir.exists() &&
                    backupDir.listFiles((dir, name) -> name.endsWith(".sql")).length > 0;

            if (backupExists) {
                logger.info("Backup detected — skipping data.sql initialization.");
                return;
            }

            // Check if database already has data
            ResultSet rs = conn.createStatement().executeQuery("SELECT COUNT(*) FROM CLIENT");
            rs.next();
            long count = rs.getLong(1);
            if (count > 0) {
                logger.info("Existing data found — skipping data.sql initialization.");
                return;
            }

            // Execute data.sql manually
            logger.info("No data or backup found — initializing database from data.sql...");
            ResourceDatabasePopulator populator = new ResourceDatabasePopulator(new ClassPathResource("data.sql"));
            populator.execute(dataSource);

            logger.info("✅ Database populated successfully from data.sql.");
        } catch (Exception e) {
            logger.error("❌ Failed to initialize database: " + e.getMessage());
        }
    }
}