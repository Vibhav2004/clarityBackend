package com.roadmapapp.roadmapapp.configurations;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public final class SecurityConfig {

    private SecurityConfig() {
    }

    // ============================================================
    // LIMITS
    // ============================================================

    private static final int MIN_USERNAME_LENGTH = 3;
    private static final int MAX_USERNAME_LENGTH = 30;

    private static final int MAX_EMAIL_LENGTH = 254;
    private static final int MAX_EMAIL_LOCAL_PART = 64;

    private static final int MIN_PASSWORD_LENGTH = 12;
    private static final int MAX_PASSWORD_LENGTH = 128;


    // ============================================================
    // USERNAME
    // ============================================================

    /*
     * Allowed:
     * A-Z
     * a-z
     * 0-9
     * _
     * -
     * .
     */
    private static final Pattern USERNAME_PATTERN =
            Pattern.compile("^[A-Za-z0-9_.-]{3,30}$");


    // ============================================================
    // EMAIL
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

                            // <script>
                            "<\\s*/?\\s*script\\b" +

                            // Dangerous HTML elements
                            "|<\\s*/?\\s*(iframe|object|embed|svg|math|" +
                            "style|link|meta|base|form|input|button|" +
                            "textarea|select|video|audio)\\b" +

                            // HTML event handlers
                            "|<[^>]*\\bon[a-z]+\\s*=" +

                            // JavaScript / VBScript
                            "|javascript\\s*:" +
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
    // SQL INJECTION PATTERNS
    // ============================================================

    private static final Pattern SQL_INJECTION_PATTERN =
            Pattern.compile(
                    "(?is)" +

                            // SQL comments
                            "(?:--|/\\*|\\*/)" +

                            // UNION SELECT
                            "|\\bunion\\s+(?:all\\s+)?select\\b" +

                            // SELECT ... FROM
                            "|\\bselect\\s+.+\\s+from\\b" +

                            // INSERT INTO
                            "|\\binsert\\s+into\\b" +

                            // UPDATE ... SET
                            "|\\bupdate\\s+.+\\s+set\\b" +

                            // DELETE FROM
                            "|\\bdelete\\s+from\\b" +

                            // DROP
                            "|\\bdrop\\s+(?:table|database|schema)\\b" +

                            // ALTER
                            "|\\balter\\s+(?:table|database|schema)\\b" +

                            // TRUNCATE
                            "|\\btruncate\\s+(?:table|database)\\b" +

                            // EXEC / EXECUTE
                            "|\\bexec(?:ute)?\\s*\\b" +

                            // Common SQL authentication bypass
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

                            // Backtick command execution
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
    // REGISTER
    // ============================================================

    public static void validateRegisterInput(
            String username,
            String email,
            String password
    ) {

        validateUsername(username);
        validateEmail(email);
        validatePasswordForRegister(password);
    }


    // ============================================================
    // LOGIN
    // ============================================================

    public static void validateLoginInput(
            String email,
            String password
    ) {

        validateEmail(email);
        validatePasswordForLogin(password);
    }


    // ============================================================
    // USERNAME VALIDATION
    // ============================================================

    public static void validateUsername(String username) {

        requireNotNull(username, "Username");

        if (username.length() < MIN_USERNAME_LENGTH) {
            throw securityException(
                    "Username is too short"
            );
        }

        if (username.length() > MAX_USERNAME_LENGTH) {
            throw securityException(
                    "Username is too long"
            );
        }

        if (!username.equals(username.trim())) {
            throw securityException(
                    "Username cannot contain surrounding spaces"
            );
        }

        if (containsControlCharacters(username)) {
            throw securityException(
                    "Username contains control characters"
            );
        }

        /*
         * Strong allowlist.
         *
         * This automatically rejects:
         * <script>
         * quotes
         * spaces
         * /
         * \
         * ;
         * SQL symbols
         * HTML
         * etc.
         */
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            throw securityException(
                    "Username contains SQL XSS characters"
            );
        }

        checkForInjection(username);
    }


    // ============================================================
    // EMAIL VALIDATION
    // ============================================================

    public static void validateEmail(String email) {

        requireNotNull(email, "Email");

        if (email.length() > MAX_EMAIL_LENGTH) {
            throw securityException(
                    "Email is too long"
            );
        }

        if (!email.equals(email.trim())) {
            throw securityException(
                    "Email cannot contain surrounding spaces"
            );
        }

        if (containsControlCharacters(email)) {
            throw securityException(
                    "Email contains control characters"
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

        String domain =
                email.substring(firstAt + 1);

        if (localPart.length() > MAX_EMAIL_LOCAL_PART) {
            throw securityException(
                    "Email local part is too long"
            );
        }

        if (domain.isEmpty()) {
            throw securityException(
                    "Email domain is missing"
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

        checkForInjection(email);
    }


    // ============================================================
    // REGISTER PASSWORD
    // ============================================================

    public static void validatePasswordForRegister(
            String password
    ) {

        requireNotNull(password, "Password");

        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw securityException(
                    "Password must contain at least 12 characters"
            );
        }

        if (password.length() > MAX_PASSWORD_LENGTH) {
            throw securityException(
                    "Password is too long"
            );
        }

        if (containsControlCharacters(password)) {
            throw securityException(
                    "Password contains invalid control characters"
            );
        }

        /*
         * IMPORTANT:
         *
         * Do NOT reject passwords because they contain:
         *
         * < >
         * '
         * "
         * ;
         * --
         * SELECT
         * UNION
         * javascript:
         *
         * These can be legitimate password characters.
         *
         * Password security is handled separately through:
         * - HTTPS
         * - password hashing
         * - parameterized database operations
         */
    }


    // ============================================================
    // LOGIN PASSWORD
    // ============================================================

    public static void validatePasswordForLogin(
            String password
    ) {

        requireNotNull(password, "Password");

        /*
         * Login should not require the password to satisfy
         * registration rules such as minimum length.
         *
         * Otherwise an existing older account could become
         * impossible to log into.
         */

        if (password.length() > MAX_PASSWORD_LENGTH) {
            throw securityException(
                    "Password is too long"
            );
        }

        if (containsControlCharacters(password)) {
            throw securityException(
                    "Password contains invalid control characters"
            );
        }
    }


    // ============================================================
    // INJECTION VALIDATION
    // ============================================================

    private static void checkForInjection(String input) {

        if (input == null || input.isEmpty()) {
            return;
        }

        // Direct XSS
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

        /*
         * Check URL-encoded input.
         *
         * Example:
         * %3Cscript%3E
         */
        String decoded = decode(input);

        if (!decoded.equals(input)) {

            checkDecodedInjection(decoded);
        }

        /*
         * Check double URL encoding.
         *
         * Example:
         * %253Cscript%253E
         */
        String doubleDecoded = decode(decoded);

        if (!doubleDecoded.equals(decoded)) {

            checkDecodedInjection(doubleDecoded);
        }
    }


    // ============================================================
    // DECODED INJECTION VALIDATION
    // ============================================================

    private static void checkDecodedInjection(
            String input
    ) {

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