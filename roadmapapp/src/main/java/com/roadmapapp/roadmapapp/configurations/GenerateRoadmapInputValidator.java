package com.roadmapapp.roadmapapp.configurations;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public final class GenerateRoadmapInputValidator {

    private GenerateRoadmapInputValidator() {
    }

    // ============================================================
    // CONTROL CHARACTERS
    // ============================================================

    private static final Pattern CONTROL_CHARACTER_PATTERN =
            Pattern.compile(
                    "[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F]"
            );


    // ============================================================
    // XSS / HTML / JAVASCRIPT
    // ============================================================

    private static final Pattern XSS_PATTERN =
            Pattern.compile(
                    "(?is)" +

                            "<\\s*/?\\s*script\\b" +

                            "|<\\s*/?\\s*(iframe|object|embed|svg|math|" +
                            "style|link|meta|base|form|input|button|" +
                            "textarea|select|video|audio)\\b" +

                            "|<[^>]*\\bon[a-z]+\\s*=" +

                            "|javascript\\s*:" +

                            "|vbscript\\s*:" +

                            "|data\\s*:\\s*text/html" +

                            "|expression\\s*\\(" +

                            "|srcdoc\\s*=" +

                            "|<!--"
            );


    // ============================================================
    // SQL INJECTION
    // ============================================================

    private static final Pattern SQL_INJECTION_PATTERN =
            Pattern.compile(
                    "(?is)" +

                            "(?:--|/\\*|\\*/)" +

                            "|\\bunion\\s+(?:all\\s+)?select\\b" +

                            "|\\bselect\\s+.+\\s+from\\b" +

                            "|\\binsert\\s+into\\b" +

                            "|\\bupdate\\s+.+\\s+set\\b" +

                            "|\\bdelete\\s+from\\b" +

                            "|\\bdrop\\s+(?:table|database|schema)\\b" +

                            "|\\balter\\s+(?:table|database|schema)\\b" +

                            "|\\btruncate\\s+(?:table|database)\\b" +

                            "|\\bexec(?:ute)?\\s*\\b" +

                            "|['\"]\\s*(?:or|and)\\s+" +
                            "[^\\r\\n]{0,100}" +
                            "(?:=|<|>)"
            );


    // ============================================================
    // COMMAND INJECTION
    // ============================================================

    private static final Pattern COMMAND_INJECTION_PATTERN =
            Pattern.compile(
                    "(?is)" +

                            "\\$\\(" +

                            "|`[^`]+`" +

                            "|\\b(?:bash|sh|zsh|cmd|powershell|pwsh)\\b"
            );


    // ============================================================
    // PATH TRAVERSAL
    // ============================================================

    private static final Pattern PATH_TRAVERSAL_PATTERN =
            Pattern.compile(
                    "(?i)" +
                            "\\.\\./" +
                            "|\\.\\.\\\\"
            );


    // ============================================================
    // MAIN VALIDATION
    // ============================================================

    public static void validateCategory(String category) {

        if (category == null) {
            throw securityException(
                    "Category cannot be null"
            );
        }

        if (containsControlCharacters(category)) {
            throw securityException(
                    "Category contains control characters"
            );
        }

        checkForInjection(category);
    }


    // ============================================================
    // INJECTION CHECK
    // ============================================================

    private static void checkForInjection(String input) {

        // XSS
        if (XSS_PATTERN.matcher(input).find()) {
            throw securityException(
                    "Potential XSS or JavaScript injection detected"
            );
        }

        // SQL Injection
        if (SQL_INJECTION_PATTERN.matcher(input).find()) {
            throw securityException(
                    "Potential SQL injection detected"
            );
        }

        // Command Injection
        if (COMMAND_INJECTION_PATTERN.matcher(input).find()) {
            throw securityException(
                    "Potential command injection detected"
            );
        }

        // Path Traversal
        if (PATH_TRAVERSAL_PATTERN.matcher(input).find()) {
            throw securityException(
                    "Potential path traversal detected"
            );
        }


        // ========================================================
        // URL DECODED ATTACK
        // ========================================================

        String decoded = decode(input);

        if (!decoded.equals(input)) {
            checkDecodedInjection(decoded);
        }


        // ========================================================
        // DOUBLE URL ENCODED ATTACK
        // ========================================================

        String doubleDecoded = decode(decoded);

        if (!doubleDecoded.equals(decoded)) {
            checkDecodedInjection(doubleDecoded);
        }
    }


    // ============================================================
    // DECODED INPUT CHECK
    // ============================================================

    private static void checkDecodedInjection(String input) {

        if (XSS_PATTERN.matcher(input).find()) {
            throw securityException(
                    "Encoded XSS or JavaScript injection detected"
            );
        }

        if (SQL_INJECTION_PATTERN.matcher(input).find()) {
            throw securityException(
                    "Encoded SQL injection detected"
            );
        }

        if (COMMAND_INJECTION_PATTERN.matcher(input).find()) {
            throw securityException(
                    "Encoded command injection detected"
            );
        }

        if (PATH_TRAVERSAL_PATTERN.matcher(input).find()) {
            throw securityException(
                    "Encoded path traversal detected"
            );
        }
    }


    // ============================================================
    // CONTROL CHARACTERS
    // ============================================================

    private static boolean containsControlCharacters(
            String input
    ) {

        return CONTROL_CHARACTER_PATTERN
                .matcher(input)
                .find();
    }


    // ============================================================
    // URL DECODING
    // ============================================================

    private static String decode(String input) {

        try {

            return URLDecoder.decode(
                    input,
                    StandardCharsets.UTF_8
            );

        } catch (Exception e) {

            throw securityException(
                    "Invalid encoded input"
            );
        }
    }


    // ============================================================
    // SECURITY EXCEPTION
    // ============================================================

    private static SecurityException securityException(
            String message
    ) {

        return new SecurityException(message);
    }
}