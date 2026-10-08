package com.roadmapapp.roadmapapp.configurations;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public final class GeneratedRoadmapFinal {

    private GeneratedRoadmapFinal() {
    }

    // ============================================================
    // NAME LIMITS
    // ============================================================

    private static final int MIN_NAME_LENGTH = 1;
    private static final int MAX_NAME_LENGTH = 200;


    // ============================================================
    // EMAIL LIMITS
    // ============================================================

    private static final int MIN_EMAIL_LENGTH = 5;
    private static final int MAX_EMAIL_LENGTH = 254;

    private static final int MAX_EMAIL_LOCAL_PART = 64;


    // ============================================================
    // EMAIL STRUCTURE
    // ============================================================

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile(
                    "^[A-Za-z0-9.!#$%&'*+/=?^_`{|}~-]+@" +
                            "[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}" +
                            "[A-Za-z0-9])?" +
                            "(?:\\.[A-Za-z0-9]" +
                            "(?:[A-Za-z0-9-]{0,61}" +
                            "[A-Za-z0-9])?)+$"
            );


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

                            // Script
                            "<\\s*/?\\s*script\\b" +

                            // Dangerous HTML elements
                            "|<\\s*/?\\s*(iframe|object|embed|svg|math|" +
                            "style|link|meta|base|form|input|button|" +
                            "textarea|select|video|audio)\\b" +

                            // Event handlers
                            "|<[^>]*\\bon[a-z]+\\s*=" +

                            // JavaScript
                            "|javascript\\s*:" +

                            // VBScript
                            "|vbscript\\s*:" +

                            // HTML data URL
                            "|data\\s*:\\s*text/html" +

                            // CSS expression
                            "|expression\\s*\\(" +

                            // iframe srcdoc
                            "|srcdoc\\s*=" +

                            // HTML comment
                            "|<!--"
            );


    // ============================================================
    // SQL INJECTION
    // ============================================================

    private static final Pattern SQL_INJECTION_PATTERN =
            Pattern.compile(
                    "(?is)" +

                            // SQL comments
                            "(?:--|/\\*|\\*/)" +

                            // UNION SELECT
                            "|\\bunion\\s+(?:all\\s+)?select\\b" +

                            // SELECT FROM
                            "|\\bselect\\s+.+\\s+from\\b" +

                            // INSERT INTO
                            "|\\binsert\\s+into\\b" +

                            // UPDATE SET
                            "|\\bupdate\\s+.+\\s+set\\b" +

                            // DELETE FROM
                            "|\\bdelete\\s+from\\b" +

                            // DROP
                            "|\\bdrop\\s+(?:table|database|schema)\\b" +

                            // ALTER
                            "|\\balter\\s+(?:table|database|schema)\\b" +

                            // TRUNCATE
                            "|\\btruncate\\s+(?:table|database)\\b" +

                            // EXEC
                            "|\\bexec(?:ute)?\\s*\\b" +

                            // Common authentication bypass
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

                            // Command substitution
                            "\\$\\(" +

                            // Backtick execution
                            "|`[^`]+`" +

                            // Common shells
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
    // MAIN VALIDATOR
    // ============================================================

    public static void validateGenerateRoadmapInput(
            String category,
            String name,
            String difficulty,
            String email
    ) {
        System.out.println("VALIDATOR RUNNING");
        // Category
        validateCategory(category);

        // Roadmap name
        validateName(name);

        // Difficulty
        validateDifficulty(difficulty);

        // Email
        validateEmail(email);
    }


    // ============================================================
    // CATEGORY
    // ============================================================

    private static void validateCategory(String category) {

        requireNotNull(category, "Category");

        validateCommonInjection(category, "Category");
    }


    // ============================================================
    // NAME
    // ============================================================

    private static void validateName(String name) {

        requireNotNull(name, "Name");

        if (name.length() < MIN_NAME_LENGTH) {
            throw securityException(
                    "Name is too short"
            );
        }

        if (name.length() > MAX_NAME_LENGTH) {
            throw securityException(
                    "Name is too long"
            );
        }

        validateCommonInjection(name, "Name");
    }


    // ============================================================
    // DIFFICULTY
    // ============================================================

    private static void validateDifficulty(String difficulty) {

        requireNotNull(difficulty, "Difficulty");

        /*
         * No min/max or strict structure requested.
         * Only suspicious-input validation.
         */
        validateCommonInjection(
                difficulty,
                "Difficulty"
        );
    }


    // ============================================================
    // EMAIL
    // ============================================================

    private static void validateEmail(String email) {

        requireNotNull(email, "Email");

        if (email.length() < MIN_EMAIL_LENGTH) {
            throw securityException(
                    "Email is too short"
            );
        }

        if (email.length() > MAX_EMAIL_LENGTH) {
            throw securityException(
                    "Email is too long"
            );
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw securityException(
                    "Invalid email format"
            );
        }

        int firstAt = email.indexOf('@');
        int lastAt = email.lastIndexOf('@');

        if (firstAt <= 0 || firstAt != lastAt) {
            throw securityException(
                    "Invalid email format"
            );
        }

        String localPart =
                email.substring(0, firstAt);

        if (localPart.length() > MAX_EMAIL_LOCAL_PART) {
            throw securityException(
                    "Email local part is too long"
            );
        }

        if (email.contains("..")) {
            throw securityException(
                    "Invalid email format"
            );
        }

        if (localPart.startsWith(".")
                || localPart.endsWith(".")) {

            throw securityException(
                    "Invalid email format"
            );
        }

        validateCommonInjection(email, "Email");
    }


    // ============================================================
    // COMMON INJECTION VALIDATION
    // ============================================================

    private static void validateCommonInjection(
            String input,
            String field
    ) {

        if (containsControlCharacters(input)) {
            throw securityException(
                    field + " contains control characters"
            );
        }

        // Direct XSS
        if (XSS_PATTERN.matcher(input).find()) {
            throw securityException(
                    "Potential XSS or JavaScript injection detected in "
                            + field
            );
        }

        // Direct SQL injection
        if (SQL_INJECTION_PATTERN.matcher(input).find()) {
            throw securityException(
                    "Potential SQL injection detected in "
                            + field
            );
        }

        // Command injection
        if (COMMAND_INJECTION_PATTERN.matcher(input).find()) {
            throw securityException(
                    "Potential command injection detected in "
                            + field
            );
        }

        // Path traversal
        if (PATH_TRAVERSAL_PATTERN.matcher(input).find()) {
            throw securityException(
                    "Potential path traversal detected in "
                            + field
            );
        }


        // ========================================================
        // URL DECODE
        // ========================================================

        String decoded = decode(input);

        if (!decoded.equals(input)) {

            validateDecodedInjection(
                    decoded,
                    field
            );
        }


        // ========================================================
        // DOUBLE URL DECODE
        // ========================================================

        String doubleDecoded = decode(decoded);

        if (!doubleDecoded.equals(decoded)) {

            validateDecodedInjection(
                    doubleDecoded,
                    field
            );
        }
    }


    // ============================================================
    // DECODED INJECTION VALIDATION
    // ============================================================

    private static void validateDecodedInjection(
            String input,
            String field
    ) {

        if (XSS_PATTERN.matcher(input).find()) {
            throw securityException(
                    "Encoded XSS or JavaScript injection detected in "
                            + field
            );
        }

        if (SQL_INJECTION_PATTERN.matcher(input).find()) {
            throw securityException(
                    "Encoded SQL injection detected in "
                            + field
            );
        }

        if (COMMAND_INJECTION_PATTERN.matcher(input).find()) {
            throw securityException(
                    "Encoded command injection detected in "
                            + field
            );
        }

        if (PATH_TRAVERSAL_PATTERN.matcher(input).find()) {
            throw securityException(
                    "Encoded path traversal detected in "
                            + field
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
    // NULL CHECK
    // ============================================================

    private static void requireNotNull(
            String value,
            String field
    ) {

        if (value == null) {
            throw securityException(
                    field + " cannot be null"
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
