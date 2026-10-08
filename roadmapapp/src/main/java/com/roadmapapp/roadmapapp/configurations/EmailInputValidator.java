package com.roadmapapp.roadmapapp.configurations;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public final class EmailInputValidator {

    private EmailInputValidator() {
    }

    // ============================================================
    // LIMITS
    // ============================================================

    private static final int MIN_EMAIL_LENGTH = 5;
    private static final int MAX_EMAIL_LENGTH = 254;
    private static final int MAX_LOCAL_PART_LENGTH = 64;


    // ============================================================
    // EMAIL FORMAT
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
    // PUBLIC VALIDATOR
    // ============================================================

    public static void validateEmail(String email) {

        // --------------------------------------------------------
        // Null
        // --------------------------------------------------------

        if (email == null) {
            throw securityException(
                    "Email cannot be null"
            );
        }


        // --------------------------------------------------------
        // Length
        // --------------------------------------------------------

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


        // --------------------------------------------------------
        // Surrounding spaces
        // --------------------------------------------------------

        if (!email.equals(email.trim())) {
            throw securityException(
                    "Email cannot contain surrounding spaces"
            );
        }


        // --------------------------------------------------------
        // Control characters
        // --------------------------------------------------------

        if (containsControlCharacters(email)) {
            throw securityException(
                    "Email contains control characters"
            );
        }


        // --------------------------------------------------------
        // Email format
        // --------------------------------------------------------

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw securityException(
                    "Invalid email format"
            );
        }


        // --------------------------------------------------------
        // Exactly one @
        // --------------------------------------------------------

        int firstAt = email.indexOf('@');
        int lastAt = email.lastIndexOf('@');

        if (firstAt <= 0 || firstAt != lastAt) {
            throw securityException(
                    "Invalid email format"
            );
        }


        // --------------------------------------------------------
        // Local part
        // --------------------------------------------------------

        String localPart =
                email.substring(0, firstAt);

        if (localPart.length() > MAX_LOCAL_PART_LENGTH) {
            throw securityException(
                    "Email local part is too long"
            );
        }


        // --------------------------------------------------------
        // Basic structural checks
        // --------------------------------------------------------

        if (localPart.startsWith(".")
                || localPart.endsWith(".")) {

            throw securityException(
                    "Invalid email format"
            );
        }

        if (email.contains("..")) {
            throw securityException(
                    "Invalid email format"
            );
        }


        // --------------------------------------------------------
        // Injection checks
        // --------------------------------------------------------

        checkForInjection(email);
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

        // SQL injection
        if (SQL_INJECTION_PATTERN.matcher(input).find()) {
            throw securityException(
                    "Potential SQL injection detected"
            );
        }

        // Command injection
        if (COMMAND_INJECTION_PATTERN.matcher(input).find()) {
            throw securityException(
                    "Potential command injection detected"
            );
        }

        // Path traversal
        if (PATH_TRAVERSAL_PATTERN.matcher(input).find()) {
            throw securityException(
                    "Potential path traversal detected"
            );
        }


        // ========================================================
        // URL DECODE
        // ========================================================

        String decoded = decode(input);

        if (!decoded.equals(input)) {
            checkDecodedInjection(decoded);
        }


        // ========================================================
        // DOUBLE URL DECODE
        // ========================================================

        String doubleDecoded = decode(decoded);

        if (!doubleDecoded.equals(decoded)) {
            checkDecodedInjection(doubleDecoded);
        }
    }


    // ============================================================
    // DECODED INJECTION CHECK
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
    // CONTROL CHARACTER CHECK
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
                    "Invalid encoded email"
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