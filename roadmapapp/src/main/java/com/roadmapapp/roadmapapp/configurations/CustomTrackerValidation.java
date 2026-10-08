package com.roadmapapp.roadmapapp.configurations;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.regex.Pattern;

public final class CustomTrackerValidation {

    private CustomTrackerValidation() {
    }

    // =========================================================
    // CONTROL CHARACTERS
    // =========================================================

    private static final Pattern CONTROL_PATTERN =
            Pattern.compile("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F]");


    // =========================================================
    // XSS / HTML / JAVASCRIPT
    // =========================================================

    private static final Pattern XSS_PATTERN =
            Pattern.compile(
                    "(?i)(" +
                            "<\\s*script" +
                            "|</\\s*script\\s*>" +
                            "|javascript\\s*:" +
                            "|vbscript\\s*:" +
                            "|data\\s*:\\s*text/html" +
                            "|on\\w+\\s*=" +
                            "|<\\s*(iframe|object|embed|svg|img|style|link|meta|form)" +
                            "|expression\\s*\\(" +
                            "|eval\\s*\\(" +
                            "|document\\s*\\." +
                            "|window\\s*\\." +
                            "|alert\\s*\\(" +
                            ")"
            );


    // =========================================================
    // SQL INJECTION
    // =========================================================

    private static final Pattern SQL_INJECTION_PATTERN =
            Pattern.compile(
                    "(?i)(" +
                            "(\\b(select|insert|update|delete|drop|alter|truncate|create|replace|merge)\\b" +
                            "\\s+.{0,100}\\b(from|into|table|where|set)\\b)" +
                            "|\\bunion\\b\\s+(all\\s+)?\\bselect\\b" +
                            "|\\bor\\b\\s+['\"]?\\d+['\"]?\\s*=\\s*['\"]?\\d+" +
                            "|\\band\\b\\s+['\"]?\\d+['\"]?\\s*=\\s*['\"]?\\d+" +
                            "|--" +
                            "|/\\*" +
                            "|\\*/" +
                            "|;\\s*(select|insert|update|delete|drop|alter|truncate)" +
                            ")"
            );


    // =========================================================
    // COMMAND INJECTION
    // =========================================================

    private static final Pattern COMMAND_INJECTION_PATTERN =
            Pattern.compile(
                    "(?i)(" +
                            "[;&|`]" +
                            "|\\$\\(" +
                            "|\\b(cmd|powershell|bash|sh|zsh|wget|curl|nc|netcat)\\b\\s*[-/]" +
                            "|\\bexec\\s*\\(" +
                            "|\\bsystem\\s*\\(" +
                            ")"
            );


    // =========================================================
    // PATH TRAVERSAL
    // =========================================================

    private static final Pattern PATH_TRAVERSAL_PATTERN =
            Pattern.compile(
                    "(?i)(" +
                            "\\.\\./" +
                            "|\\.\\.\\\\" +
                            "|%2e%2e" +
                            "|%252e%252e" +
                            ")"
            );


    // =========================================================
    // MAIN VALIDATION METHOD
    // =========================================================

    public static void validateTracker(
            Map<String, Object> tracker
    ) {

        if (tracker == null) {
            throw new IllegalArgumentException(
                    "Tracker data cannot be null"
            );
        }


        // =====================================================
        // STRING FIELDS
        // =====================================================

        validateStringField(
                "Username",
                tracker.get("Username")
        );

        validateStringField(
                "UserEmail",
                tracker.get("UserEmail")
        );

        validateStringField(
                "RoadmapName",
                tracker.get("RoadmapName")
        );

        validateStringField(
                "type",
                tracker.get("type")
        );

        validateStringField(
                "status",
                tracker.get("status")
        );


        // =====================================================
        // DATE/TIME FIELDS
        // =====================================================

        validateDateTime(
                "createdAt",
                tracker.get("createdAt")
        );

        validateDateTime(
                "updatedAt",
                tracker.get("updatedAt")
        );


        // =====================================================
        // INTEGER FIELDS
        // =====================================================

        validateInteger(
                "completedSteps",
                tracker.get("completedSteps")
        );

        validateInteger(
                "totalSteps",
                tracker.get("totalSteps")
        );


        // =====================================================
        // PERCENTAGE
        // =====================================================

        validatePercentage(
                "completedpercentage",
                tracker.get("completedpercentage")
        );
    }


    // =========================================================
    // STRING VALIDATION
    // =========================================================

    private static void validateStringField(
            String fieldName,
            Object value
    ) {

        if (value == null) {
            return;
        }

        if (!(value instanceof String input)) {
            throw new IllegalArgumentException(
                    fieldName + " must be a string"
            );
        }


        // Control characters
        if (CONTROL_PATTERN.matcher(input).find()) {

            throw new IllegalArgumentException(
                    "Invalid control characters in "
                            + fieldName
            );
        }


        // Original value
        checkSuspiciousInput(
                fieldName,
                input
        );


        // URL decoded value
        String decoded = decode(input);

        if (!decoded.equals(input)) {

            checkSuspiciousInput(
                    fieldName,
                    decoded
            );
        }


        // Double URL decoded value
        String doubleDecoded = decode(decoded);

        if (!doubleDecoded.equals(decoded)) {

            checkSuspiciousInput(
                    fieldName,
                    doubleDecoded
            );
        }
    }


    // =========================================================
    // INTEGER VALIDATION
    // =========================================================

    private static void validateInteger(
            String fieldName,
            Object value
    ) {

        if (value == null) {
            return;
        }

        if (!(value instanceof Number number)) {

            throw new IllegalArgumentException(
                    fieldName + " must be a number"
            );
        }

        double numericValue = number.doubleValue();

        if (!Double.isFinite(numericValue)) {

            throw new IllegalArgumentException(
                    fieldName + " contains an invalid number"
            );
        }

        if (numericValue != Math.floor(numericValue)) {

            throw new IllegalArgumentException(
                    fieldName + " must be an integer"
            );
        }

        if (numericValue < 0) {

            throw new IllegalArgumentException(
                    fieldName + " cannot be negative"
            );
        }
    }


    // =========================================================
    // PERCENTAGE VALIDATION
    // =========================================================

    private static void validatePercentage(
            String fieldName,
            Object value
    ) {

        if (value == null) {
            return;
        }

        if (!(value instanceof Number number)) {

            throw new IllegalArgumentException(
                    fieldName + " must be a number"
            );
        }

        double percentage = number.doubleValue();

        if (!Double.isFinite(percentage)) {

            throw new IllegalArgumentException(
                    fieldName + " contains an invalid number"
            );
        }

        if (percentage < 0 || percentage > 100) {

            throw new IllegalArgumentException(
                    fieldName +
                            " must be between 0 and 100"
            );
        }
    }


    // =========================================================
    // DATE/TIME VALIDATION
    // =========================================================

    private static void validateDateTime(
            String fieldName,
            Object value
    ) {

        if (value == null) {
            return;
        }

        if (!(value instanceof String input)) {

            throw new IllegalArgumentException(
                    fieldName + " must be a string"
            );
        }


        // First check for suspicious payloads
        validateStringField(
                fieldName,
                input
        );


        /*
         * Your frontend uses:
         *
         * new Date().toISOString()
         *
         * Example:
         * 2026-10-01T14:46:10.123Z
         *
         * OffsetDateTime accepts ISO-8601
         * timestamps containing an offset/Z.
         */
        try {

            OffsetDateTime.parse(input);

        } catch (DateTimeParseException e) {

            throw new IllegalArgumentException(
                    fieldName +
                            " must be a valid ISO-8601 date/time"
            );
        }
    }


    // =========================================================
    // SUSPICIOUS INPUT CHECK
    // =========================================================

    private static void checkSuspiciousInput(
            String fieldName,
            String input
    ) {

        if (XSS_PATTERN.matcher(input).find()) {

            throw new IllegalArgumentException(
                    "Suspicious XSS input detected in "
                            + fieldName
            );
        }


        if (SQL_INJECTION_PATTERN.matcher(input).find()) {

            throw new IllegalArgumentException(
                    "Suspicious SQL injection input detected in "
                            + fieldName
            );
        }


        if (COMMAND_INJECTION_PATTERN.matcher(input).find()) {

            throw new IllegalArgumentException(
                    "Suspicious command injection input detected in "
                            + fieldName
            );
        }


        if (PATH_TRAVERSAL_PATTERN.matcher(input).find()) {

            throw new IllegalArgumentException(
                    "Suspicious path traversal input detected in "
                            + fieldName
            );
        }
    }


    // =========================================================
    // URL DECODING
    // =========================================================

    private static String decode(
            String input
    ) {

        try {

            return URLDecoder.decode(
                    input,
                    StandardCharsets.UTF_8
            );

        } catch (IllegalArgumentException e) {

            throw new IllegalArgumentException(
                    "Invalid URL encoding"
            );
        }
    }
}