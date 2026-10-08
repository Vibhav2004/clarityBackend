package com.roadmapapp.roadmapapp.configurations;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public final class CustomroadmapValidation {

    private CustomroadmapValidation() {
    }

    // =========================================================
    // FIELD LENGTH LIMITS
    // =========================================================

    private static final int MAX_TOPIC_LENGTH = 200;
    private static final int MAX_CATEGORY_LENGTH = 100;
    private static final int MAX_STEP_NAME_LENGTH = 200;
    private static final int MAX_DESCRIPTION_LENGTH = 5000;
    private static final int MAX_REFERENCE_LINK_LENGTH = 2048;
    private static final int MAX_NODE_ID_LENGTH = 200;

    // =========================================================
    // POSITION LIMITS
    // =========================================================

    private static final double MIN_POSITION = -1_000_000;
    private static final double MAX_POSITION = 1_000_000;

    // =========================================================
    // CONTROL CHARACTERS
    // =========================================================

    private static final Pattern CONTROL_PATTERN =
            Pattern.compile(
                    "[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F]"
            );

    // =========================================================
    // HTML TAG DETECTION
    // =========================================================

    private static final Pattern HTML_TAG_PATTERN =
            Pattern.compile(
                    "(?is)<\\s*/?\\s*[a-z][^>]*>"
            );

    // =========================================================
    // XSS / JAVASCRIPT
    // =========================================================

    private static final Pattern XSS_PATTERN =
            Pattern.compile(
                    "(?i)(" +
                            "<\\s*script" +
                            "|</\\s*script\\s*>" +
                            "|javascript\\s*:" +
                            "|vbscript\\s*:" +
                            "|data\\s*:\\s*(text/html|application/javascript)" +
                            "|on[a-z]+\\s*=" +
                            "|expression\\s*\\(" +
                            "|eval\\s*\\(" +
                            "|document\\s*\\." +
                            "|window\\s*\\." +
                            "|alert\\s*\\(" +
                            "|prompt\\s*\\(" +
                            "|confirm\\s*\\(" +
                            "|fetch\\s*\\(" +
                            "|XMLHttpRequest" +
                            "|<\\s*(iframe|object|embed|svg|img|style|link|meta|form|base|video|audio)" +
                            ")"
            );

    // =========================================================
    // SQL INJECTION
    //
    // This is an additional security check.
    // Database queries must STILL use JPA / prepared statements /
    // parameterized queries.
    // =========================================================

    private static final Pattern SQL_INJECTION_PATTERN =
            Pattern.compile(
                    "(?i)(" +
                            "\\bunion\\s+(all\\s+)?select\\b" +
                            "|\\b(select|insert|update|delete|drop|alter|truncate|create|replace|merge)\\b" +
                            "\\s+.{0,200}\\b(from|into|table|where|set)\\b" +
                            "|\\bor\\s+['\"]?\\d+['\"]?\\s*=\\s*['\"]?\\d+" +
                            "|\\band\\s+['\"]?\\d+['\"]?\\s*=\\s*['\"]?\\d+" +
                            "|--" +
                            "/\\*" +
                            "\\*/" +
                            "|;\\s*(select|insert|update|delete|drop|alter|truncate|create)" +
                            ")"
            );

    // =========================================================
    // COMMAND INJECTION
    // =========================================================

    private static final Pattern COMMAND_INJECTION_PATTERN =
            Pattern.compile(
                    "(?i)(" +
                            "\\$\\(" +
                            "|`[^`]*`" +
                            "|\\b(cmd|powershell|bash|sh|zsh|wget|curl|nc|netcat)\\b\\s*[-/]" +
                            "|\\b(exec|system|Runtime\\.getRuntime)\\s*\\(" +
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
    // ALLOWED VALUES
    // =========================================================

    /*
     * Change these ONLY if your frontend uses different values.
     */

    private static final Set<String> ALLOWED_DIFFICULTIES =
            Set.of(
                    "Beginner",
                    "Intermediate",
                    "Advanced"
            );

    private static final Set<String> ALLOWED_SHAPES =
            Set.of(
                    "rectangle",
                    "round",
                    "diamond",
                    "circle",
                    "default"
            );

    // =========================================================
    // COLOR
    // =========================================================

    private static final Pattern HEX_COLOR_PATTERN =
            Pattern.compile(
                    "^#[0-9a-fA-F]{6}$"
            );

    // =========================================================
    // EMAIL
    // =========================================================

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile(
                    "^[A-Za-z0-9.!#$%&'*+/=?^_`{|}~-]+@"
                            + "[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?"
                            + "(?:\\.[A-Za-z0-9]"
                            + "(?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?)+$"
            );

    // =========================================================
    // PUBLIC VALIDATOR
    // =========================================================

    public static void validateCustomRoadmap(
            Map<String, Object> roadmap
    ) {

        if (roadmap == null) {
            throw new IllegalArgumentException(
                    "Roadmap data cannot be null"
            );
        }

        // -----------------------------------------------------
        // Allowed top-level fields
        // -----------------------------------------------------

        Set<String> allowedTopLevelFields = Set.of(
                "userEmail",
                "topic",
                "category",
                "nodes",
                "edges"
        );

        for (String key : roadmap.keySet()) {

            if (!allowedTopLevelFields.contains(key)) {
                throw new IllegalArgumentException(
                        "Unexpected roadmap field: " + key
                );
            }
        }

        // -----------------------------------------------------
        // Topic
        // -----------------------------------------------------

        validateRequiredString(
                "topic",
                roadmap.get("topic"),
                MAX_TOPIC_LENGTH
        );

        // -----------------------------------------------------
        // Category
        // -----------------------------------------------------

        validateRequiredString(
                "category",
                roadmap.get("category"),
                MAX_CATEGORY_LENGTH
        );

        // -----------------------------------------------------
        // User email
        // -----------------------------------------------------

        if (roadmap.containsKey("userEmail")
                && roadmap.get("userEmail") != null) {

            validateEmail(
                    "userEmail",
                    roadmap.get("userEmail")
            );
        }

        // -----------------------------------------------------
        // Nodes
        // -----------------------------------------------------

        Object nodesObject = roadmap.get("nodes");

        if (nodesObject == null) {

            throw new IllegalArgumentException(
                    "Nodes cannot be null"
            );
        }

        if (!(nodesObject instanceof Iterable<?> nodes)) {

            throw new IllegalArgumentException(
                    "Invalid nodes data"
            );
        }

        /*
         * No maximum node count.
         *
         * Every node is still individually validated.
         */

        for (Object nodeObject : nodes) {

            if (!(nodeObject instanceof Map<?, ?> node)) {

                throw new IllegalArgumentException(
                        "Invalid node data"
                );
            }

            validateNode(node);
        }

        // -----------------------------------------------------
        // Edges
        // -----------------------------------------------------

        Object edgesObject = roadmap.get("edges");

        if (edgesObject == null) {

            throw new IllegalArgumentException(
                    "Edges cannot be null"
            );
        }

        if (!(edgesObject instanceof Iterable<?> edges)) {

            throw new IllegalArgumentException(
                    "Invalid edges data"
            );
        }

        /*
         * No maximum edge count.
         *
         * Every edge is still individually validated.
         */

        for (Object edgeObject : edges) {

            if (!(edgeObject instanceof Map<?, ?> edge)) {

                throw new IllegalArgumentException(
                        "Invalid edge data"
                );
            }

            validateEdge(edge);
        }
    }

    // =========================================================
    // NODE VALIDATION
    // =========================================================

    private static void validateNode(
            Map<?, ?> node
    ) {

        Set<String> allowedNodeFields = Set.of(
                "step_name",
                "description",
                "reference_link",
                "difficulty",
                "color",
                "shape",
                "position"
        );

        for (Object keyObject : node.keySet()) {

            if (!(keyObject instanceof String key)) {

                throw new IllegalArgumentException(
                        "Invalid node field"
                );
            }

            if (!allowedNodeFields.contains(key)) {

                throw new IllegalArgumentException(
                        "Unexpected node field: " + key
                );
            }
        }

        // -----------------------------------------------------
        // Step name
        // -----------------------------------------------------

        validateRequiredString(
                "step_name",
                node.get("step_name"),
                MAX_STEP_NAME_LENGTH
        );

        // -----------------------------------------------------
        // Description
        // -----------------------------------------------------

        validateRequiredString(
                "description",
                node.get("description"),
                MAX_DESCRIPTION_LENGTH
        );

        // -----------------------------------------------------
        // Reference link
        // -----------------------------------------------------

        validateReferenceLink(
                node.get("reference_link")
        );

        // -----------------------------------------------------
        // Difficulty
        // -----------------------------------------------------

        validateDifficulty(
                node.get("difficulty")
        );

        // -----------------------------------------------------
        // Color
        // -----------------------------------------------------

        validateColor(
                node.get("color")
        );

        // -----------------------------------------------------
        // Shape
        // -----------------------------------------------------

        validateShape(
                node.get("shape")
        );

        // -----------------------------------------------------
        // Position
        // -----------------------------------------------------

        Object positionObject = node.get("position");

        if (positionObject == null) {

            throw new IllegalArgumentException(
                    "Position cannot be null"
            );
        }

        if (!(positionObject instanceof Map<?, ?> position)) {

            throw new IllegalArgumentException(
                    "Invalid position data"
            );
        }

        validatePosition(position);
    }

    // =========================================================
    // EDGE VALIDATION
    // =========================================================

    private static void validateEdge(
            Map<?, ?> edge
    ) {

        Set<String> allowedEdgeFields = Set.of(
                "source",
                "target"
        );

        for (Object keyObject : edge.keySet()) {

            if (!(keyObject instanceof String key)) {

                throw new IllegalArgumentException(
                        "Invalid edge field"
                );
            }

            if (!allowedEdgeFields.contains(key)) {

                throw new IllegalArgumentException(
                        "Unexpected edge field: " + key
                );
            }
        }

        validateRequiredString(
                "source",
                edge.get("source"),
                MAX_NODE_ID_LENGTH
        );

        validateRequiredString(
                "target",
                edge.get("target"),
                MAX_NODE_ID_LENGTH
        );
    }

    // =========================================================
    // REQUIRED STRING
    // =========================================================

    private static void validateRequiredString(
            String fieldName,
            Object value,
            int maxLength
    ) {

        if (value == null) {

            throw new IllegalArgumentException(
                    fieldName + " cannot be null"
            );
        }

        if (!(value instanceof String input)) {

            throw new IllegalArgumentException(
                    fieldName + " must be a string"
            );
        }

        if (input.isBlank()) {

            throw new IllegalArgumentException(
                    fieldName + " cannot be empty"
            );
        }

        if (input.length() > maxLength) {

            throw new IllegalArgumentException(
                    fieldName + " exceeds maximum length"
            );
        }

        validateText(
                fieldName,
                input
        );
    }

    // =========================================================
    // TEXT VALIDATION
    // =========================================================

    private static void validateText(
            String fieldName,
            String input
    ) {

        // Original input
        checkControlCharacters(
                fieldName,
                input
        );

        checkSuspiciousInput(
                fieldName,
                input
        );

        /*
         * Decode up to 3 times.
         *
         * This catches:
         *
         * <script>
         *
         * %3Cscript%3E
         *
         * %253Cscript%253E
         *
         * etc.
         */

        String current = input;

        for (int i = 0; i < 3; i++) {

            String decoded = decode(current);

            if (decoded.equals(current)) {
                break;
            }

            checkControlCharacters(
                    fieldName,
                    decoded
            );

            checkSuspiciousInput(
                    fieldName,
                    decoded
            );

            current = decoded;
        }
    }

    // =========================================================
    // CONTROL CHARACTER CHECK
    // =========================================================

    private static void checkControlCharacters(
            String fieldName,
            String input
    ) {

        if (CONTROL_PATTERN.matcher(input).find()) {

            throw new IllegalArgumentException(
                    "Invalid control characters in "
                            + fieldName
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

        // HTML
        if (HTML_TAG_PATTERN.matcher(input).find()) {

            throw new IllegalArgumentException(
                    "HTML is not allowed in "
                            + fieldName
            );
        }

        // XSS
        if (XSS_PATTERN.matcher(input).find()) {

            throw new IllegalArgumentException(
                    "Suspicious XSS input detected in "
                            + fieldName
            );
        }

        // SQL injection
        if (SQL_INJECTION_PATTERN.matcher(input).find()) {

            throw new IllegalArgumentException(
                    "Suspicious SQL injection input detected in "
                            + fieldName
            );
        }

        // Command injection
        if (COMMAND_INJECTION_PATTERN.matcher(input).find()) {

            throw new IllegalArgumentException(
                    "Suspicious command injection input detected in "
                            + fieldName
            );
        }

        // Path traversal
        if (PATH_TRAVERSAL_PATTERN.matcher(input).find()) {

            throw new IllegalArgumentException(
                    "Suspicious path traversal input detected in "
                            + fieldName
            );
        }
    }

    // =========================================================
    // DIFFICULTY
    // =========================================================

    private static void validateDifficulty(
            Object value
    ) {

        if (!(value instanceof String difficulty)) {

            throw new IllegalArgumentException(
                    "Difficulty must be a string"
            );
        }

        if (!ALLOWED_DIFFICULTIES.contains(difficulty)) {

            throw new IllegalArgumentException(
                    "Invalid difficulty"
            );
        }
    }

    // =========================================================
    // SHAPE
    // =========================================================

    private static void validateShape(
            Object value
    ) {

        if (!(value instanceof String shape)) {

            throw new IllegalArgumentException(
                    "Shape must be a string"
            );
        }

        if (!ALLOWED_SHAPES.contains(shape)) {

            throw new IllegalArgumentException(
                    "Invalid shape"
            );
        }
    }

    // =========================================================
    // COLOR
    // =========================================================

    private static void validateColor(
            Object value
    ) {

        if (!(value instanceof String color)) {

            throw new IllegalArgumentException(
                    "Color must be a string"
            );
        }

        if (!HEX_COLOR_PATTERN.matcher(color).matches()) {

            throw new IllegalArgumentException(
                    "Invalid color format"
            );
        }
    }

    // =========================================================
    // REFERENCE LINK
    // =========================================================

    private static void validateReferenceLink(
            Object value
    ) {

        if (!(value instanceof String link)) {

            throw new IllegalArgumentException(
                    "Reference link must be a string"
            );
        }

        if (link.isBlank()) {

            throw new IllegalArgumentException(
                    "Reference link cannot be empty"
            );
        }

        if (link.length() > MAX_REFERENCE_LINK_LENGTH) {

            throw new IllegalArgumentException(
                    "Reference link is too long"
            );
        }

        validateText(
                "reference_link",
                link
        );

        try {

            URI uri = new URI(link);

            String scheme = uri.getScheme();

            if (scheme == null) {

                throw new IllegalArgumentException(
                        "Reference link must have a scheme"
                );
            }

            if (!scheme.equalsIgnoreCase("http")
                    && !scheme.equalsIgnoreCase("https")) {

                throw new IllegalArgumentException(
                        "Only HTTP and HTTPS links are allowed"
                );
            }

            if (uri.getHost() == null
                    || uri.getHost().isBlank()) {

                throw new IllegalArgumentException(
                        "Invalid reference link"
                );
            }

        } catch (URISyntaxException e) {

            throw new IllegalArgumentException(
                    "Invalid reference link"
            );
        }
    }

    // =========================================================
    // POSITION
    // =========================================================

    private static void validatePosition(
            Map<?, ?> position
    ) {

        if (!position.containsKey("x")
                || !position.containsKey("y")) {

            throw new IllegalArgumentException(
                    "Position must contain x and y"
            );
        }

        for (Object keyObject : position.keySet()) {

            if (!(keyObject instanceof String key)
                    || (!key.equals("x")
                    && !key.equals("y"))) {

                throw new IllegalArgumentException(
                        "Unexpected position field"
                );
            }
        }

        validateCoordinate(
                "position.x",
                position.get("x")
        );

        validateCoordinate(
                "position.y",
                position.get("y")
        );
    }

    // =========================================================
    // COORDINATE
    // =========================================================

    private static void validateCoordinate(
            String fieldName,
            Object value
    ) {

        if (!(value instanceof Number number)) {

            throw new IllegalArgumentException(
                    fieldName + " must be numeric"
            );
        }

        double coordinate = number.doubleValue();

        if (!Double.isFinite(coordinate)) {

            throw new IllegalArgumentException(
                    fieldName + " must be finite"
            );
        }

        if (coordinate < MIN_POSITION
                || coordinate > MAX_POSITION) {

            throw new IllegalArgumentException(
                    fieldName + " is outside allowed range"
            );
        }
    }

    // =========================================================
    // EMAIL
    // =========================================================

    private static void validateEmail(
            String fieldName,
            Object value
    ) {

        if (!(value instanceof String email)) {

            throw new IllegalArgumentException(
                    fieldName + " must be a string"
            );
        }

        if (email.length() > 254) {

            throw new IllegalArgumentException(
                    "Invalid email"
            );
        }

        validateText(
                fieldName,
                email
        );

        if (!EMAIL_PATTERN.matcher(email).matches()) {

            throw new IllegalArgumentException(
                    "Invalid email format"
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