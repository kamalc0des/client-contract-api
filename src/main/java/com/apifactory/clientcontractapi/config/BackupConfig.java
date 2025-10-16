package com.apifactory.clientcontractapi.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Configuration class responsible for managing backup initialization.
 * Ensures that the backup directory exists and creates an initial backup file at startup.
 */
@Configuration
@EnableScheduling
public class BackupConfig {

    @Value("${backup.folder:./backups}")
    private String backupFolder;

    private static final Logger logger = LoggerFactory.getLogger(BackupConfig.class);

    /**
     * Called once the Spring context is fully initialized.
     * Creates the backup directory and an initial backup file at startup.
     */
    @PostConstruct
    public void initBackupDirectory() {
        File folder = new File(backupFolder);

        if (!folder.exists()) {
            boolean created = folder.mkdirs();
            if (created) {
                logger.info("✅ Backup folder created at: " + folder.getAbsolutePath());
            } else {
                logger.warn("⚠️ Failed to create backup folder: " + folder.getAbsolutePath());
            }
        }

        // Create an initial empty backup file to ensure persistence works
        createInitialBackupFile();
    }

    /**
     * Creates an empty backup file named with the current timestamp.
     * This simulates an initial backup at application startup.
     */
    private void createInitialBackupFile() {
        try {
            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            File initialBackup = new File(backupFolder, "initial_backup_" + timestamp + ".sql");

            if (initialBackup.createNewFile()) {
               logger.info("🗂️ Initial backup file created: " + initialBackup.getName());
            } else {
               logger.info("ℹ️ Initial backup file already exists.");
            }

        } catch (Exception e) {
            logger.error("❌ Failed to create initial backup file: " + e.getMessage());
        }
    }
}
