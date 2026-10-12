
package com.roadmapapp.roadmapapp.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RecaptchaService {

    @Value("${recaptcha.secret-key}")
    private String secretKey;

    @Value("${recaptcha.allowed-hostnames}")
    private String allowedHostnamesConfig;

    // Create these directly instead of injecting an ObjectMapper bean.
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    public boolean verify(String token) {

        if (token == null || token.isBlank()
                || secretKey == null || secretKey.isBlank()) {
            System.err.println(
                    "reCAPTCHA rejected: token or secret is missing."
            );
            return false;
        }

        try {
            String requestBody =
                    "secret=" + encode(secretKey)
                            + "&response=" + encode(token);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(
                            "https://www.google.com/recaptcha/api/siteverify"
                    ))
                    .timeout(Duration.ofSeconds(10))
                    .header(
                            "Content-Type",
                            "application/x-www-form-urlencoded"
                    )
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() != 200) {
                System.err.println(
                        "Google reCAPTCHA HTTP status: "
                                + response.statusCode()
                );
                return false;
            }

            JsonNode result = objectMapper.readTree(response.body());

            boolean success =
                    result.path("success").asBoolean(false);

            String hostname =
                    result.path("hostname").asText("");

            Set<String> allowedHostnames = Arrays.stream(
                            allowedHostnamesConfig.split(",")
                    )
                    .map(String::trim)
                    .map(host -> host.toLowerCase(Locale.ROOT))
                    .filter(host -> !host.isEmpty())
                    .collect(Collectors.toSet());

            boolean hostnameAllowed = allowedHostnames.contains(
                    hostname.toLowerCase(Locale.ROOT)
            );



            return success && hostnameAllowed;

        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            System.err.println("reCAPTCHA verification interrupted.");
            return false;

        } catch (Exception ex) {
            System.err.println(
                    "reCAPTCHA verification failed: "
                            + ex.getClass().getSimpleName()
            );
            return false;
        }
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

}