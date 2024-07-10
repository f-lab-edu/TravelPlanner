package com.roamgram.travelDiary.common.logging;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@Profile("prod")
public class LogDirectoryInitializer {

    @PostConstruct
    public void init() {
        File logDirectory = new File("/var/log/spring-boot");
        if (!logDirectory.exists()) {
            boolean dirsCreated = logDirectory.mkdirs();
            if (!dirsCreated) {
                throw new RuntimeException("Failed to create log directory: " + logDirectory.getAbsolutePath());
            }
        }
    }
}
