package com.roamgram.travelDiary.common.logging.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
public class RequestApiInfo {

    public UUID userId = null;
    public String userName = null;
    public String method = null;
    public String url = null;
    public String name = null;
    public Map<String, String> header = new HashMap<>();
    public Map<String, String> parameters = new HashMap<>();
    public Map<String, String> body = new HashMap<>();
    public String ipAddress = null;
    public final String dateTime = LocalDateTime.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

}