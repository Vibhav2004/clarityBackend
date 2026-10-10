//package com.roadmapapp.roadmapapp.configurations;
//
//
//import org.springframework.context.annotation.Configuration;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Configuration
//public class RateLimitConfig {
//
//    /*
//     * ============================================================
//     * RATE LIMIT RULE
//     * ============================================================
//     *
//     * maxRequests     = maximum requests allowed
//     * windowSeconds   = time window in seconds
//     */
//
//    public record RateLimitRule(
//            int maxRequests,
//            long windowSeconds
//    ) {
//    }
//
//
//    /*
//     * ============================================================
//     * GLOBAL EMERGENCY LIMIT
//     * ============================================================
//     *
//     * Maximum number of requests from one IP across the
//     * entire backend.
//     *
//     * 100 requests / minute
//     */
//
//    public static final RateLimitRule GLOBAL_LIMIT =
//            new RateLimitRule(100, 60);
//
//
//    /*
//     * ============================================================
//     * ENDPOINT RULE
//     * ============================================================
//     */
//
//    public record EndpointRule(
//            String pattern,
//            RateLimitRule limit
//    ) {
//    }
//
//
//    /*
//     * ============================================================
//     * ALL ENDPOINT LIMITS
//     * ============================================================
//     */
//
//    public static final List<EndpointRule> ENDPOINT_LIMITS =
//            new ArrayList<>();
//
//
//    static {
//
//        // ========================================================
//        // GENERAL / HEALTH
//        // ========================================================
//
//        ENDPOINT_LIMITS.add(
//                new EndpointRule(
//                        "/health",
//                        new RateLimitRule(60, 60)
//                )
//        );
//
//
//        // ========================================================
//        // STATIC ROADMAP CATEGORIES
//        // ========================================================
//
//        ENDPOINT_LIMITS.add(
//                new EndpointRule(
//                        "/Static_Roadmaps/Category",
//                        new RateLimitRule(60, 60)
//                )
//        );
//
//
//        /*
//         * IMPORTANT:
//         *
//         * These are the category/name/difficulty/email endpoints
//         * that generate a roadmap.
//         *
//         * Example:
//         *
//         * /Static_Roadmaps/Category/Programming/Java/Intermediate/email
//         *
//         * 10 requests / hour
//         */
//
//        ENDPOINT_LIMITS.add(
//                new EndpointRule(
//                        "/Static_Roadmaps/Category/*/*/*/*",
//                        new RateLimitRule(1000, 3600)
//                )
//        );
//
//
//        /*
//         * Category/{category}
//         *
//         * Example:
//         *
//         * /Static_Roadmaps/Category/Programming
//         */
//
//        ENDPOINT_LIMITS.add(
//                new EndpointRule(
//                        "/Static_Roadmaps/Category/*",
//                        new RateLimitRule(600, 60)
//                )
//        );
//
//
//        // ========================================================
//        // USER ROADMAPS
//        // ========================================================
//
//        ENDPOINT_LIMITS.add(
//                new EndpointRule(
//                        "/All_RoadMap/*",
//                        new RateLimitRule(30, 60)
//                )
//        );
//
//
//        // ========================================================
//        // PROFILE
//        // ========================================================
//
//        ENDPOINT_LIMITS.add(
//                new EndpointRule(
//                        "/Profile/*",
//                        new RateLimitRule(30, 60)
//                )
//        );
//
//
//        // ========================================================
//        // PLAN
//        // ========================================================
//
//        ENDPOINT_LIMITS.add(
//                new EndpointRule(
//                        "/plan/*",
//                        new RateLimitRule(30, 60)
//                )
//        );
//
//
//        // ========================================================
//        // TRACKERS
//        // ========================================================
//
//        ENDPOINT_LIMITS.add(
//                new EndpointRule(
//                        "/all_trackers",
//                        new RateLimitRule(30, 60)
//                )
//        );
//
//
//        ENDPOINT_LIMITS.add(
//                new EndpointRule(
//                        "/tracker",
//                        new RateLimitRule(10, 3600)
//                )
//        );
//
//
//        ENDPOINT_LIMITS.add(
//                new EndpointRule(
//                        "/Custom_Tracker",
//                        new RateLimitRule(5, 3600)
//                )
//        );
//
//
//        ENDPOINT_LIMITS.add(
//                new EndpointRule(
//                        "/save_tracker",
//                        new RateLimitRule(6000, 60)
//                )
//        );
//
//
//        ENDPOINT_LIMITS.add(
//                new EndpointRule(
//                        "/delete_trackers",
//                        new RateLimitRule(100, 60)
//                )
//        );
//
//
//        // ========================================================
//        // CUSTOM ROADMAP
//        // ========================================================
//
//        ENDPOINT_LIMITS.add(
//                new EndpointRule(
//                        "/Custom_Roadmap",
//                        new RateLimitRule(500, 3600)
//                )
//        );
//
//
//        // ========================================================
//        // AUTHENTICATION
//        // ========================================================
//
//        ENDPOINT_LIMITS.add(
//                new EndpointRule(
//                        "/Register-User",
//                        new RateLimitRule(300, 3600)
//                )
//        );
//
//
//        ENDPOINT_LIMITS.add(
//                new EndpointRule(
//                        "/Login-User",
//                        new RateLimitRule(10, 60)
//                )
//        );
//
//
//        ENDPOINT_LIMITS.add(
//                new EndpointRule(
//                        "/LogOut-User",
//                        new RateLimitRule(20, 60)
//                )
//        );
//
//
//        // ========================================================
//        // OTP
//        // ========================================================
//
//        ENDPOINT_LIMITS.add(
//                new EndpointRule(
//                        "/send",
//                        new RateLimitRule(3, 600)
//                )
//        );
//
//
//        ENDPOINT_LIMITS.add(
//                new EndpointRule(
//                        "/verify",
//                        new RateLimitRule(5, 600)
//                )
//        );
//
//
//        // ========================================================
//        // PASSWORD
//        // ========================================================
//
//        ENDPOINT_LIMITS.add(
//                new EndpointRule(
//                        "/editPassword",
//                        new RateLimitRule(3, 3600)
//                )
//        );
//
//
//        // ========================================================
//        // ACCOUNT DELETION
//        // ========================================================
//
//        ENDPOINT_LIMITS.add(
//                new EndpointRule(
//                        "/deleteAccount",
//                        new RateLimitRule(2, 3600)
//                )
//        );
//
//
//        // ========================================================
//        // PAYMENT
//        // ========================================================
//
//        ENDPOINT_LIMITS.add(
//                new EndpointRule(
//                        "/payment/create-order",
//                        new RateLimitRule(5, 600)
//                )
//        );
//
//
//        ENDPOINT_LIMITS.add(
//                new EndpointRule(
//                        "/payment/verify",
//                        new RateLimitRule(10, 600)
//                )
//        );
//    }
//
//
//    /*
//     * ============================================================
//     * FIND LIMIT FOR REQUEST PATH
//     * ============================================================
//     */
//
//    public static RateLimitRule getLimit(String path) {
//
//        /*
//         * Exact matches first.
//         */
//        for (EndpointRule rule : ENDPOINT_LIMITS) {
//
//            if (!rule.pattern().contains("*")
//                    && rule.pattern().equals(path)) {
//
//                return rule.limit();
//            }
//        }
//
//
//        /*
//         * Wildcard matches.
//         */
//        for (EndpointRule rule : ENDPOINT_LIMITS) {
//
//            if (matches(rule.pattern(), path)) {
//
//                return rule.limit();
//            }
//        }
//
//
//        /*
//         * No endpoint-specific rule.
//         */
//        return null;
//    }
//
//
//    /*
//     * ============================================================
//     * WILDCARD MATCHING
//     * ============================================================
//     *
//     * Example:
//     *
//     * /Profile/*
//     *
//     * matches:
//     *
//     * /Profile/test@gmail.com
//     */
//
//    private static boolean matches(
//            String pattern,
//            String path) {
//
//        String[] patternParts =
//                pattern.split("/", -1);
//
//        String[] pathParts =
//                path.split("/", -1);
//
//
//        if (patternParts.length != pathParts.length) {
//
//            return false;
//        }
//
//
//        for (int i = 0;
//             i < patternParts.length;
//             i++) {
//
//            String patternPart =
//                    patternParts[i];
//
//
//            String pathPart =
//                    pathParts[i];
//
//
//            if ("*".equals(patternPart)) {
//
//                continue;
//            }
//
//
//            if (!patternPart.equals(pathPart)) {
//
//                return false;
//            }
//        }
//
//
//        return true;
//    }
//}
package com.roadmapapp.roadmapapp.configurations;

import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;

import java.util.List;

@Configuration
public class RateLimitConfig {

    public record RateLimitRule(
            int maxRequests,
            long windowSeconds
    ) {
        public RateLimitRule {
            if (maxRequests < 1 || windowSeconds < 1) {
                throw new IllegalArgumentException(
                        "Rate-limit values must be positive"
                );
            }
        }
    }

    public record EndpointRule(
            String method,
            String pattern,
            RateLimitRule limit
    ) {}

    // Emergency limit across the entire backend, per IP.
    public static final RateLimitRule GLOBAL_LIMIT =
            new RateLimitRule(120, 60);

    private static final AntPathMatcher PATH_MATCHER =
            new AntPathMatcher();

    public static final List<EndpointRule> ENDPOINT_LIMITS = List.of(

            // Health
            new EndpointRule(
                    "GET", "/health",
                    new RateLimitRule(60, 60)
            ),

            // Authentication
            new EndpointRule(
                    "POST", "/Register-User",
                    new RateLimitRule(50, 3600)
            ),
            new EndpointRule(
                    "POST", "/Login-User",
                    new RateLimitRule(10, 300)
            ),
            new EndpointRule(
                    "POST", "/LogOut-User",
                    new RateLimitRule(20, 60)
            ),

            // Static roadmaps
            new EndpointRule(
                    "POST", "/Static_Roadmaps/Category",
                    new RateLimitRule(60, 60)
            ),
            new EndpointRule(
                    "POST", "/Static_Roadmaps/Category/*",
                    new RateLimitRule(60, 60)
            ),
            new EndpointRule(
                    "POST", "/Static_Roadmaps/Category/*/*/*/*",
                    new RateLimitRule(30, 60)
            ),

            // User data
            new EndpointRule(
                    "POST", "/All_RoadMap/*",
                    new RateLimitRule(30, 60)
            ),
            new EndpointRule(
                    "POST", "/Profile/*",
                    new RateLimitRule(30, 60)
            ),
            new EndpointRule(
                    "POST", "/plan",
                    new RateLimitRule(60, 60)
            ),

            // Trackers
            new EndpointRule(
                    "POST", "/tracker",
                    new RateLimitRule(10, 3600)
            ),
            new EndpointRule(
                    "POST", "/all_trackers",
                    new RateLimitRule(30, 60)
            ),
            new EndpointRule(
                    "POST", "/Custom_Tracker",
                    new RateLimitRule(5, 3600)
            ),
            new EndpointRule(
                    "POST", "/save_tracker",
                    new RateLimitRule(60, 60)
            ),
            new EndpointRule(
                    "DELETE", "/delete_trackers",
                    new RateLimitRule(10, 60)
            ),

            // Custom roadmap generation
            new EndpointRule(
                    "POST", "/Custom_Roadmap",
                    new RateLimitRule(10, 3600)
            ),

            // OTP
            new EndpointRule(
                    "POST", "/send",
                    new RateLimitRule(3, 600)
            ),
            new EndpointRule(
                    "POST", "/verify",
                    new RateLimitRule(5, 600)
            ),

            // Password and account
            new EndpointRule(
                    "POST", "/editPassword",
                    new RateLimitRule(3, 3600)
            ),
            new EndpointRule(
                    "DELETE", "/deleteAccount",
                    new RateLimitRule(2, 3600)
            ),

            // Payments
            new EndpointRule(
                    "POST", "/payment/create-order",
                    new RateLimitRule(5, 600)
            ),
            new EndpointRule(
                    "POST", "/payment/verify",
                    new RateLimitRule(10, 600)
            ),
            new EndpointRule(
                    "POST", "/payment/failed",
                    new RateLimitRule(10, 600)
            )
    );

    public static EndpointRule findRule(
            String method,
            String path
    ) {
        String normalizedPath = normalizePath(path);

        // Exact paths take priority.
        for (EndpointRule rule : ENDPOINT_LIMITS) {
            if (!rule.pattern().contains("*")
                    && rule.method().equalsIgnoreCase(method)
                    && rule.pattern().equals(normalizedPath)) {
                return rule;
            }
        }

        // Then check wildcard paths such as /Profile/*.
        for (EndpointRule rule : ENDPOINT_LIMITS) {
            if (rule.method().equalsIgnoreCase(method)
                    && PATH_MATCHER.match(
                    rule.pattern(),
                    normalizedPath
            )) {
                return rule;
            }
        }

        return null;
    }

    private static String normalizePath(String path) {
        if (path == null || path.isBlank()) {
            return "/";
        }

        if (path.length() > 1 && path.endsWith("/")) {
            return path.substring(0, path.length() - 1);
        }

        return path;
    }
}