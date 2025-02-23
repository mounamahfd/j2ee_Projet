package com.j2ee.oauth2.backend.services;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import java.util.Map;

@Service
public class GoogleAuthService {

    private final String CLIENT_ID = "148159148384-0sj8pt8qrd4primt71kjnq4grsf8i78e.apps.googleusercontent.com";  // Remplace par le vrai Client ID
    private final String CLIENT_SECRET = "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"; // Remplace par le vrai Client Secret
    private final String REDIRECT_URI = "http://localhost:8080/auth/callback";

    public String exchangeCodeForToken(String code) {
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("client_id", CLIENT_ID);
        requestBody.add("client_secret", CLIENT_SECRET);
        requestBody.add("redirect_uri", REDIRECT_URI);
        requestBody.add("grant_type", "authorization_code");
        requestBody.add("code", code);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                "https://oauth2.googleapis.com/token", HttpMethod.POST, request, Map.class
        );

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return response.getBody().get("access_token").toString();
        } else {
            throw new RuntimeException("Failed to get access token from Google");
        }
    }
}