////package com.roadmapapp.roadmapapp.service;
////
////
////
////
////import com.fasterxml.jackson.databind.JsonNode;
////import com.fasterxml.jackson.databind.ObjectMapper;
////import org.springframework.beans.factory.annotation.Value;
////import org.springframework.stereotype.Service;
////
////import java.net.URI;
////import java.net.URLEncoder;
////import java.net.http.HttpClient;
////import java.net.http.HttpRequest;
////import java.net.http.HttpResponse;
////import java.nio.charset.StandardCharsets;
////import java.time.Duration;
////import java.util.Arrays;
////import java.util.Set;
////import java.util.stream.Collectors;
////
////@Service
////public class RecaptchaService {
////
////    @Value("${recaptcha.secret-key:}")
////    private String secretKey;
////
////    @Value("${recaptcha.min-score:0.5}")
////    private double minScore;
////
////    @Value("${recaptcha.allowed-hostnames:clarity-e9p.pages.dev}")
////    private String allowedHostnamesConfig;
////
////    private final ObjectMapper objectMapper;
////    private final HttpClient httpClient;
////
////    public RecaptchaService(ObjectMapper objectMapper) {
////        this.objectMapper = objectMapper;
////
////        this.httpClient = HttpClient.newBuilder()
////                .connectTimeout(Duration.ofSeconds(5))
////                .build();
////    }
////
////    public boolean verify(String token, String expectedAction) {
////
////        if (token == null || token.isBlank()
////                || secretKey == null || secretKey.isBlank()) {
////            return false;
////        }
////
////        if (!Set.of("register", "login").contains(expectedAction)) {
////            return false;
////        }
////
////        try {
////            String requestBody =
////                    "secret=" + encode(secretKey)
////                            + "&response=" + encode(token);
////
////            HttpRequest request = HttpRequest.newBuilder()
////                    .uri(URI.create(
////                            "https://www.google.com/recaptcha/api/siteverify"
////                    ))
////                    .timeout(Duration.ofSeconds(8))
////                    .header(
////                            "Content-Type",
////                            "application/x-www-form-urlencoded"
////                    )
////                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
////                    .build();
////
////            HttpResponse<String> response = httpClient.send(
////                    request,
////                    HttpResponse.BodyHandlers.ofString()
////            );
////
////            if (response.statusCode() != 200) {
////                return false;
////            }
////
////            JsonNode result = objectMapper.readTree(response.body());
////
////            boolean success = result.path("success").asBoolean(false);
////            String action = result.path("action").asText("");
////            String hostname = result.path("hostname").asText("");
////            double score = result.path("score").asDouble(-1);
////
////            Set<String> allowedHostnames = Arrays.stream(
////                            allowedHostnamesConfig.split(",")
////                    )
////                    .map(String::trim)
////                    .filter(host -> !host.isEmpty())
////                    .collect(Collectors.toSet());
////
////            return success
////                    && expectedAction.equals(action)
////                    && score >= minScore
////                    && score <= 1.0
////                    && allowedHostnames.contains(hostname);
////
////        } catch (InterruptedException ex) {
////            Thread.currentThread().interrupt();
////            return false;
////
////        } catch (Exception ex) {
////            // Fail closed: if Google's verification fails, reject the request.
////            return false;
////        }
////    }
////
////    private String encode(String value) {
////        return URLEncoder.encode(value, StandardCharsets.UTF_8);
////    }
////}
//
//package com.roadmapapp.roadmapapp.service;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import java.net.URI;
//import java.net.URLEncoder;
//import java.net.http.HttpClient;
//import java.net.http.HttpRequest;
//import java.net.http.HttpResponse;
//import java.nio.charset.StandardCharsets;
//import java.time.Duration;
//import java.util.Arrays;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//@Service
//public class RecaptchaService {
//
//    @Value("${recaptcha.min-score:0.5}")
//    private double minScore;
//
//    @Value("${RECAPTCHA_ALLOWED_HOSTNAMES:clarity-e9p.pages.dev,127.0.0.1,localhost}")
//    private String allowedHostnamesConfig;
//
//    private final ObjectMapper objectMapper;
//    private final HttpClient httpClient;
//
//    // FIX: Create ObjectMapper directly instead of injecting a Spring bean.
//    public RecaptchaService() {
//        this.objectMapper = new ObjectMapper();
//
//        this.httpClient = HttpClient.newBuilder()
//                .connectTimeout(Duration.ofSeconds(5))
//                .build();
//    }
//
//    public boolean verify(String token, String expectedAction) {
//
//        //    @Value("${recaptcha.secret-key:}")
//        String secretKey = "6LctKOgtAAAAAFswEmHnhnVkJqDYWKkKPkK0TPJm";
//        if (token == null || token.isBlank()
//                || secretKey == null || secretKey.isBlank()) {
//            return false;
//        }
//
//        if (!Set.of("register", "login").contains(expectedAction)) {
//            return false;
//        }
//
//        try {
//            String requestBody =
//                    "secret=" + encode(secretKey)
//                            + "&response=" + encode(token);
//
//            HttpRequest request = HttpRequest.newBuilder()
//                    .uri(URI.create(
//                            "https://www.google.com/recaptcha/api/siteverify"
//                    ))
//                    .timeout(Duration.ofSeconds(8))
//                    .header(
//                            "Content-Type",
//                            "application/x-www-form-urlencoded"
//                    )
//                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
//                    .build();
//
//            HttpResponse<String> response = httpClient.send(
//                    request,
//                    HttpResponse.BodyHandlers.ofString()
//            );
//
//            if (response.statusCode() != 200) {
//                return false;
//            }
//
//            JsonNode result = objectMapper.readTree(response.body());
//
//            boolean success = result.path("success").asBoolean(false);
//            String action = result.path("action").asText("");
//            String hostname = result.path("hostname").asText("");
//            double score = result.path("score").asDouble(-1);
//            System.out.println("reCAPTCHA success: " + success);
//            System.out.println("reCAPTCHA action: " + action);
//            System.out.println("reCAPTCHA hostname: " + hostname);
//            System.out.println("reCAPTCHA score: " + score);
//            System.out.println(
//                    "reCAPTCHA error codes: "
//                            + result.path("error-codes")
//            );
//            Set<String> allowedHostnames = Arrays.stream(
//                            allowedHostnamesConfig.split(",")
//                    )
//                    .map(String::trim)
//                    .filter(host -> !host.isEmpty())
//                    .collect(Collectors.toSet());
//
//            return success
//                    && expectedAction.equals(action)
//                    && score >= minScore
//                    && score <= 1.0
//                    && allowedHostnames.contains(hostname);
//
//        } catch (InterruptedException ex) {
//            Thread.currentThread().interrupt();
//            return false;
//
//        } catch (Exception ex) {
//            // Fail closed if Google verification fails.
//            return false;
//        }
//    }
//
//    private String encode(String value) {
//        return URLEncoder.encode(value, StandardCharsets.UTF_8);
//    }
//}
//
//package com.roadmapapp.roadmapapp.service;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import java.net.URI;
//import java.net.URLEncoder;
//import java.net.http.HttpClient;
//import java.net.http.HttpRequest;
//import java.net.http.HttpResponse;
//import java.nio.charset.StandardCharsets;
//import java.time.Duration;
//import java.util.Arrays;
//import java.util.Locale;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//@Service
//public class RecaptchaService {
//
//    private String secretKey="6LfZQ-gtAAAAABkI9nermvcnTBwkwgTCvSweGPcw";
//
//    @Value("${RECAPTCHA_ALLOWED_HOSTNAMES:clarity-e9p.pages.dev,127.0.0.1,localhost}")
//    private String allowedHostnamesConfig;
//
//    private final ObjectMapper objectMapper;
//    private final HttpClient httpClient;
//
//    public RecaptchaService(ObjectMapper objectMapper) {
//        this.objectMapper = objectMapper;
//        this.httpClient = HttpClient.newBuilder()
//                .connectTimeout(Duration.ofSeconds(5))
//                .build();
//    }
//
//    public boolean verify(String token) {
//
//        if (token == null || token.isBlank()
//                || secretKey == null || secretKey.isBlank()) {
//            System.err.println(
//                    "reCAPTCHA rejected: token or secret is missing."
//            );
//            return false;
//        }
//
//        try {
//            String requestBody =
//                    "secret=" + encode(secretKey)
//                            + "&response=" + encode(token);
//
//            HttpRequest request = HttpRequest.newBuilder()
//                    .uri(URI.create(
//                            "https://www.google.com/recaptcha/api/siteverify"
//                    ))
//                    .timeout(Duration.ofSeconds(10))
//                    .header(
//                            "Content-Type",
//                            "application/x-www-form-urlencoded"
//                    )
//                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
//                    .build();
//
//            HttpResponse<String> response = httpClient.send(
//                    request,
//                    HttpResponse.BodyHandlers.ofString()
//            );
//
//            if (response.statusCode() != 200) {
//                System.err.println(
//                        "reCAPTCHA rejected: Google HTTP "
//                                + response.statusCode()
//                );
//                return false;
//            }
//
//            JsonNode result = objectMapper.readTree(response.body());
//
//            boolean success =
//                    result.path("success").asBoolean(false);
//
//            String hostname =
//                    result.path("hostname").asText("");
//
//            Set<String> allowedHostnames = Arrays.stream(
//                            allowedHostnamesConfig.split(",")
//                    )
//                    .map(String::trim)
//                    .map(host -> host.toLowerCase(Locale.ROOT))
//                    .filter(host -> !host.isEmpty())
//                    .collect(Collectors.toSet());
//
//            boolean hostnameAllowed = allowedHostnames.contains(
//                    hostname.toLowerCase(Locale.ROOT)
//            );
//
//            // Log verification status, not the CAPTCHA token or secret.
//            System.out.println("reCAPTCHA v2 success: " + success);
//            System.out.println("reCAPTCHA hostname: " + hostname);
//            System.out.println(
//                    "reCAPTCHA error codes: "
//                            + result.path("error-codes")
//            );
//
//            return success && hostnameAllowed;
//
//        } catch (InterruptedException ex) {
//            Thread.currentThread().interrupt();
//            System.err.println(
//                    "reCAPTCHA verification was interrupted."
//            );
//            return false;
//
//        } catch (Exception ex) {
//            System.err.println(
//                    "reCAPTCHA verification failed: "
//                            + ex.getClass().getSimpleName()
//            );
//            return false;
//        }
//    }
//
//    private String encode(String value) {
//        return URLEncoder.encode(value, StandardCharsets.UTF_8);
//    }
//
//}
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

//    @Value("${recaptcha.secret-key:}")
    private String secretKey="6LfZQ-gtAAAAABkI9nermvcnTBwkwgTCvSweGPcw";

    @Value("${recaptcha.allowed-hostnames:clarity-e9p.pages.dev,localhost,127.0.0.1}")
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

            System.out.println("reCAPTCHA v2 success: " + success);
            System.out.println("reCAPTCHA hostname: " + hostname);
            System.out.println(
                    "reCAPTCHA error codes: "
                            + result.path("error-codes")
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