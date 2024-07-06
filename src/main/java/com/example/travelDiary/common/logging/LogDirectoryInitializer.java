package com.example.travelDiary.common.logging;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class LogDirectoryInitializer {

    @PostConstruct
    public void init() {
        String userHome = System.getProperty("user.home");
        File logDirectory = new File(userHome + "/logs/roamgram");
        if (!logDirectory.exists()) {
            boolean dirsCreated = logDirectory.mkdirs();
            if (!dirsCreated) {
                throw new RuntimeException("Failed to create log directory: " + logDirectory.getAbsolutePath());
            }
        }
    }
}
