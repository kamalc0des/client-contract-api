package com.apifactory.clientcontractapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Handles one-time DB initialization (data.sql) OR backup restore, but never
 * both twice.
 * Runs only after schema creation (ApplicationReadyEvent).
 */
@Configuration
public class BackupConfig {

    private static final Logger logger = LoggerFactory.getLogger(BackupConfig.class);

    private final DataSource dataSource;

    @Value("${backup.folder:./backups}")
    private String backupFolder;

    private final AtomicBoolean executed = new AtomicBoolean(false); // guard against double run

    public BackupConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void afterAppReady() {
        if (!executed.compareAndSet(false, true)) {
            // Already ran in this process (DevTools restarts etc.)
            return;
        }

        ensureBackupFolder();

        // Wait until schema is ready
        if (!waitSchemaReady()) {
            logger.error("❌ Schema not ready after retries. Skipping initialization.");
            return;
        }

        // If DB already has clients: skip data.sql
        if (hasClients()) {
            logger.info("✅ Existing data detected — skipping data.sql.");
            return;
        }

        // No data and no restore performed => run data.sql
        seedFromDataSql();
    }

    private void ensureBackupFolder() {
        File folder = new File(backupFolder);
        if (!folder.exists() && folder.mkdirs()) {
            logger.info("✅ Backup folder created at: {}", folder.getAbsolutePath());
        }
    }

    private boolean waitSchemaReady() {
        int retries = 0;
        while (retries < 20) { // ~10s max
            try (Connection c = dataSource.getConnection()) {
                ResultSet rs = c.getMetaData().getTables(null, null, "CLIENT", null);
                if (rs.next())
                    return true;
            } catch (Exception ignore) {
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {
            }
            retries++;
        }
        return false;
    }

    private boolean hasClients() {
        try (Connection c = dataSource.getConnection();
                var st = c.createStatement();
                var rs = st.executeQuery("SELECT COUNT(*) FROM CLIENT")) {
            rs.next();
            int count = rs.getInt(1);
            logger.info("🧩 CLIENT row count = {}", count);
            return count > 0;
        } catch (Exception e) {
            logger.error("❌ Failed counting CLIENT rows: {}", e.getMessage());
            return false; // fail-open to avoid blocking, but we’ll likely seed
        }
    }

    private void seedFromDataSql() {
        try {
            logger.info("🧩 No data found — initializing database from classpath:data.sql …");
            ResourceDatabasePopulator populator = new ResourceDatabasePopulator(new ClassPathResource("data.sql"));
            populator.execute(dataSource);
            logger.info("✅ data.sql executed successfully.");
        } catch (Exception e) {
            logger.error("❌ Failed to initialize database from data.sql: {}", e.getMessage());
        }
    }

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
