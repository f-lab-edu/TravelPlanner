package com.example.travelDiary.common.auth.permissions.OPA;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@Deprecated
@Service
public class OpaService {

    private final RestTemplate restTemplate = new RestTemplate();

    public boolean evaluatePolicy(String user, String action, String resource) {
        String opaUrl = "http://localhost:8181/v1/data/example/authz/allow"; // OPA URL
        Map<String, Object> input = new HashMap<>();
        input.put("user", user);
        input.put("action", action);
        input.put("resource", resource);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("input", input);

        Map responseBody = restTemplate.postForObject(opaUrl, requestBody, Map.class);
        return (Boolean) responseBody.get("result");
    }
}
