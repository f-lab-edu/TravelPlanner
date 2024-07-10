package com.roamgram.travelDiary.common.logging.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogInfo {
    private String url;
    private String name;
    private String method;
    private Map<String, String> header;
    private String parameters;
    private String body;
    private String ipAddress;
    private UUID userId;
    private String userName;
    private String exception;

    // Constructor without exception field
    public LogInfo(String url, String name, String method, Map<String, String> header, String parameters, String body, String ipAddress, UUID userId, String userName) {
        this.url = url;
        this.name = name;
        this.method = method;
        this.header = header;
        this.parameters = parameters;
        this.body = body;
        this.ipAddress = ipAddress;
        this.userId = userId;
        this.userName = userName;
    }
}
