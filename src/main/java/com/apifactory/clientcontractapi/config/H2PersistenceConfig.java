package com.apifactory.clientcontractapi.config;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Ensures the H2 database folder exists before data source initialization.
 * Uses @ConfigurationProperties to load values after Spring Boot reads the application.yml (really important, do not remove)
 */
@Configuration
@ConfigurationProperties(prefix = "spring.datasource")
public class H2PersistenceConfig {

    private String url;

    private static final Logger logger = LoggerFactory.getLogger(H2PersistenceConfig.class);

    public void setUrl(String url) {
        this.url = url;
    }

    @PostConstruct
    public void ensureDatabaseFolderExists() {
        try {
            if (url != null && url.startsWith("jdbc:h2:file:")) {
                String path = url.replace("jdbc:h2:file:", "");

                File dbFile = new File(path);
                File folder = dbFile.getParentFile();

                if (folder != null && !folder.exists()) {
                    boolean created = folder.mkdirs();
                    if (created) {
                        logger.info("✅ Created H2 database folder: " + folder.getAbsolutePath());
                    } else {
                        logger.warn("⚠️ Failed to create H2 database folder: " + folder.getAbsolutePath());
                    }
                } else if (folder != null) {
                    logger.info("ℹ️ H2 database folder already exists: " + folder.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            logger.error("❌ Error while ensuring H2 folder: " + e.getMessage());
        }
    }
}
