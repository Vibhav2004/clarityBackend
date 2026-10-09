//package com.roadmapapp.roadmapapp.configurations;
//
//
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//@Component
//public class RateLimitFilter extends OncePerRequestFilter {
//
//
//    /*
//     * ============================================================
//     * REQUEST COUNTER
//     * ============================================================
//     */
//
//    private static class RequestCounter {
//
//        private int count;
//
//        private long windowStart;
//
//
//        public RequestCounter(long now) {
//
//            this.count = 1;
//
//            this.windowStart = now;
//        }
//    }
//
//
//    /*
//     * ============================================================
//     * GLOBAL REQUESTS
//     * ============================================================
//     *
//     * Key:
//     *
//     * IP
//     *
//     * Example:
//     *
//     * 192.168.1.10
//     */
//
//    private final Map<String, RequestCounter> globalCounters =
//            new ConcurrentHashMap<>();
//
//
//    /*
//     * ============================================================
//     * ENDPOINT REQUESTS
//     * ============================================================
//     *
//     * Key:
//     *
//     * IP + endpoint
//     *
//     * Example:
//     *
//     * 192.168.1.10:/all_trackers
//     */
//
//    private final Map<String, RequestCounter> endpointCounters =
//            new ConcurrentHashMap<>();
//
//
//    /*
//     * ============================================================
//     * CLEANUP
//     * ============================================================
//     *
//     * Prevents the maps from growing forever.
//     */
//
//    private volatile long lastCleanup =
//            System.currentTimeMillis();
//
//
//    private static final long CLEANUP_INTERVAL =
//            5 * 60 * 1000L;
//
//
//    /*
//     * ============================================================
//     * MAIN FILTER
//     * ============================================================
//     */
//
//    @Override
//    protected void doFilterInternal(
//            HttpServletRequest request,
//            HttpServletResponse response,
//            FilterChain filterChain)
//            throws ServletException, IOException {
//
//
//        /*
//         * --------------------------------------------------------
//         * OPTIONS REQUEST
//         * --------------------------------------------------------
//         *
//         * Allow CORS preflight requests.
//         */
//
//        if ("OPTIONS".equalsIgnoreCase(
//                request.getMethod())) {
//
//            filterChain.doFilter(request, response);
//
//            return;
//        }
//
//
//        /*
//         * --------------------------------------------------------
//         * GET CLIENT IP
//         * --------------------------------------------------------
//         */
//
//        String ip = getClientIp(request);
//
//
//        /*
//         * --------------------------------------------------------
//         * REQUEST PATH
//         * --------------------------------------------------------
//         */
//
//        String path =
//                request.getRequestURI();
//
//
//        /*
//         * --------------------------------------------------------
//         * GLOBAL LIMIT
//         * --------------------------------------------------------
//         *
//         * 100 requests / minute / IP
//         */
//
//        RateLimitConfig.RateLimitRule globalLimit =
//                RateLimitConfig.GLOBAL_LIMIT;
//
//
//        String globalKey =
//                "GLOBAL:" + ip;
//
//
//        RateCheckResult globalResult =
//                checkLimit(
//                        globalCounters,
//                        globalKey,
//                        globalLimit
//                );
//
//
//        if (!globalResult.allowed()) {
//
//            sendRateLimitResponse(
//                    response,
//                    globalResult
//            );
//
//            return;
//        }
//
//
//        /*
//         * --------------------------------------------------------
//         * ENDPOINT LIMIT
//         * --------------------------------------------------------
//         */
//
//        RateLimitConfig.RateLimitRule endpointLimit =
//                RateLimitConfig.getLimit(path);
//
//
//        /*
//         * If this endpoint doesn't have a specific rule,
//         * only the global limit is applied.
//         */
//
//        if (endpointLimit != null) {
//
//            String endpointKey =
//                    "ENDPOINT:"
//                            + ip
//                            + ":"
//                            + path;
//
//
//            RateCheckResult endpointResult =
//                    checkLimit(
//                            endpointCounters,
//                            endpointKey,
//                            endpointLimit
//                    );
//
//
//            if (!endpointResult.allowed()) {
//
//                sendRateLimitResponse(
//                        response,
//                        endpointResult
//                );
//
//                return;
//            }
//        }
//
//
//        /*
//         * --------------------------------------------------------
//         * CLEAN OLD ENTRIES
//         * --------------------------------------------------------
//         */
//
//        cleanupIfNeeded();
//
//
//        /*
//         * --------------------------------------------------------
//         * ALLOW REQUEST
//         * --------------------------------------------------------
//         */
//
//        filterChain.doFilter(
//                request,
//                response
//        );
//    }
//
//
//    /*
//     * ============================================================
//     * RATE CHECK
//     * ============================================================
//     */
//
//    private RateCheckResult checkLimit(
//            Map<String, RequestCounter> counters,
//            String key,
//            RateLimitConfig.RateLimitRule limit) {
//
//
//        long now =
//                System.currentTimeMillis();
//
//
//        long windowMilliseconds =
//                limit.windowSeconds() * 1000L;
//
//
//        RequestCounter counter =
//                counters.get(key);
//
//
//        /*
//         * --------------------------------------------------------
//         * FIRST REQUEST
//         * --------------------------------------------------------
//         */
//
//        if (counter == null) {
//
//            RequestCounter newCounter =
//                    new RequestCounter(now);
//
//
//            counters.put(
//                    key,
//                    newCounter
//            );
//
//
//            return new RateCheckResult(
//                    true,
//                    1,
//                    limit.maxRequests(),
//                    now + windowMilliseconds
//            );
//        }
//
//
//        /*
//         * --------------------------------------------------------
//         * WINDOW EXPIRED
//         * --------------------------------------------------------
//         */
//
//        synchronized (counter) {
//
//            if (now - counter.windowStart
//                    >= windowMilliseconds) {
//
//                counter.count = 1;
//
//                counter.windowStart = now;
//
//
//                return new RateCheckResult(
//                        true,
//                        1,
//                        limit.maxRequests(),
//                        now + windowMilliseconds
//                );
//            }
//
//
//            /*
//             * ----------------------------------------------------
//             * LIMIT EXCEEDED
//             * ----------------------------------------------------
//             */
//
//            if (counter.count
//                    >= limit.maxRequests()) {
//
//                long resetTime =
//                        counter.windowStart
//                                + windowMilliseconds;
//
//
//                return new RateCheckResult(
//                        false,
//                        counter.count,
//                        limit.maxRequests(),
//                        resetTime
//                );
//            }
//
//
//            /*
//             * ----------------------------------------------------
//             * INCREMENT COUNTER
//             * ----------------------------------------------------
//             */
//
//            counter.count++;
//
//
//            long resetTime =
//                    counter.windowStart
//                            + windowMilliseconds;
//
//
//            return new RateCheckResult(
//                    true,
//                    counter.count,
//                    limit.maxRequests(),
//                    resetTime
//            );
//        }
//    }
//
//
//    /*
//     * ============================================================
//     * CLIENT IP
//     * ============================================================
//     */
//
//    private String getClientIp(
//            HttpServletRequest request) {
//
//
//        /*
//         * Render / reverse proxy generally forwards
//         * the client address using X-Forwarded-For.
//         *
//         * The first IP is the original client.
//         */
//
//        String forwardedFor =
//                request.getHeader(
//                        "X-Forwarded-For"
//                );
//
//
//        if (forwardedFor != null
//                && !forwardedFor.isBlank()) {
//
//            return forwardedFor
//                    .split(",")[0]
//                    .trim();
//        }
//
//
//        /*
//         * Fallback.
//         */
//
//        return request.getRemoteAddr();
//    }
//
//
//    /*
//     * ============================================================
//     * 429 RESPONSE
//     * ============================================================
//     */
//
//    private void sendRateLimitResponse(
//            HttpServletResponse response,
//            RateCheckResult result)
//            throws IOException {
//
//
//        long now =
//                System.currentTimeMillis();
//
//
//        long remainingMilliseconds =
//                Math.max(
//                        0,
//                        result.resetTime()
//                                - now
//                );
//
//
//        long retryAfterSeconds =
//                Math.max(
//                        1,
//                        (remainingMilliseconds + 999) / 1000
//                );
//
//
////        response.setStatus(
////                HttpServletResponse.SC_TOO_MANY_REQUESTS
////        );
//        response.setStatus(429);
//
//
//        response.setContentType(
//                "application/json"
//        );
//
//
//        response.setCharacterEncoding(
//                "UTF-8"
//        );
//
//
//        response.setHeader(
//                "Retry-After",
//                String.valueOf(
//                        retryAfterSeconds
//                )
//        );
//
//
//        response.getWriter().write(
//                """
//                {
//                  "error": "RATE_LIMIT_EXCEEDED",
//                  "message": "Too many requests. Please try again later.",
//                  "retryAfterSeconds": %d
//                }
//                """.formatted(
//                        retryAfterSeconds
//                )
//        );
//    }
//
//
//    /*
//     * ============================================================
//     * CLEANUP
//     * ============================================================
//     */
//
//    private void cleanupIfNeeded() {
//
//
//        long now =
//                System.currentTimeMillis();
//
//
//        if (now - lastCleanup
//                < CLEANUP_INTERVAL) {
//
//            return;
//        }
//
//
//        synchronized (this) {
//
//            if (now - lastCleanup
//                    < CLEANUP_INTERVAL) {
//
//                return;
//            }
//
//
//            removeExpiredEntries(
//                    globalCounters,
//                    now
//            );
//
//
//            removeExpiredEntries(
//                    endpointCounters,
//                    now
//            );
//
//
//            lastCleanup = now;
//        }
//    }
//
//
//    /*
//     * ============================================================
//     * REMOVE EXPIRED ENTRIES
//     * ============================================================
//     */
//
//    private void removeExpiredEntries(
//            Map<String, RequestCounter> counters,
//            long now) {
//
//
//        counters.entrySet().removeIf(
//                entry -> {
//
//                    RequestCounter counter =
//                            entry.getValue();
//
//
//                    /*
//                     * Keep entries for a reasonable amount of
//                     * time. The exact endpoint window is not
//                     * available from the counter itself, so
//                     * five minutes is used as a cleanup threshold.
//                     *
//                     * Long-window entries are also safely removed
//                     * once inactive long enough.
//                     */
//
//                    return now
//                            - counter.windowStart
//                            > 2 * 60 * 60 * 1000L;
//                }
//        );
//    }
//
//
//    /*
//     * ============================================================
//     * RESULT
//     * ============================================================
//     */
//
//    private record RateCheckResult(
//            boolean allowed,
//            int currentCount,
//            int maxRequests,
//            long resetTime
//    ) {
//    }
//}
package com.roadmapapp.roadmapapp.configurations;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final ConcurrentMap<String, WindowState> windows =
            new ConcurrentHashMap<>();

    private final AtomicLong requestCounter = new AtomicLong();

    private static final long CLEANUP_IDLE_MS =
            TimeUnit.HOURS.toMillis(2);

    private static final class WindowState {
        private final Deque<Long> timestamps = new ArrayDeque<>();
        private long lastAccessMillis;
    }

    private record Decision(
            boolean allowed,
            int retryAfterSeconds
    ) {}

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // CORS preflight requests should not consume rate limits.
        return "OPTIONS".equalsIgnoreCase(request.getMethod());
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        long now = System.currentTimeMillis();
        cleanupPeriodically(now);

        // Do not trust a client-supplied X-Forwarded-For header here.
        String clientIp = request.getRemoteAddr();

        if (clientIp == null || clientIp.isBlank()) {
            clientIp = "unknown";
        }

        // 1. Global emergency limit, applied to every endpoint.
        Decision globalDecision = tryAcquire(
                "GLOBAL|" + clientIp,
                RateLimitConfig.GLOBAL_LIMIT,
                now
        );

        if (!globalDecision.allowed()) {
            rejectRequest(response, globalDecision);
            return;
        }

        // getServletPath() excludes the query string.
        String path = request.getServletPath();
        String method = request.getMethod();

        // 2. Apply a matching endpoint-specific limit.
        RateLimitConfig.EndpointRule rule =
                RateLimitConfig.findRule(method, path);

        if (rule != null) {

            /*
             * Use the rule pattern, not the full URL, in the key.
             * This keeps /Profile/user1 and /Profile/user2 in the
             * same per-IP endpoint bucket instead of allowing a
             * new bucket for every path variable.
             */
            String endpointKey =
                    "ENDPOINT|"
                            + clientIp + "|"
                            + rule.method() + "|"
                            + rule.pattern();

            Decision endpointDecision = tryAcquire(
                    endpointKey,
                    rule.limit(),
                    now
            );

            if (!endpointDecision.allowed()) {
                rejectRequest(response, endpointDecision);
                return;
            }
        }

        // No limit exceeded: continue to Spring Security/controllers.
        filterChain.doFilter(request, response);
    }

    private Decision tryAcquire(
            String key,
            RateLimitConfig.RateLimitRule rule,
            long now
    ) {
        long windowMillis =
                TimeUnit.SECONDS.toMillis(rule.windowSeconds());

        AtomicReference<Decision> result =
                new AtomicReference<>();

        /*
         * ConcurrentHashMap.compute() makes updates for each key
         * atomic, so concurrent requests cannot bypass the counter.
         */
        windows.compute(key, (ignored, existing) -> {

            WindowState state =
                    existing == null ? new WindowState() : existing;

            long cutoff = now - windowMillis;

            // Remove requests outside the sliding time window.
            while (!state.timestamps.isEmpty()
                    && state.timestamps.peekFirst() <= cutoff) {
                state.timestamps.removeFirst();
            }

            state.lastAccessMillis = now;

            if (state.timestamps.size() >= rule.maxRequests()) {

                long oldestRequest = state.timestamps.peekFirst();

                long retryMillis =
                        oldestRequest + windowMillis - now;

                int retrySeconds = (int) Math.max(
                        1,
                        (retryMillis + 999) / 1000
                );

                result.set(new Decision(false, retrySeconds));

            } else {

                state.timestamps.addLast(now);
                result.set(new Decision(true, 0));
            }

            return state;
        });

        return result.get();
    }

    private void rejectRequest(
            HttpServletResponse response,
            Decision decision
    ) throws IOException {

        response.setStatus(
                HttpStatus.TOO_MANY_REQUESTS.value()
        );

        response.setHeader(
                "Retry-After",
                Integer.toString(decision.retryAfterSeconds())
        );

        response.setHeader("Cache-Control", "no-store");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write(
                "{\"error\":\"RATE_LIMITED\","
                        + "\"message\":\"Too many requests. Please try again later.\","
                        + "\"retryAfterSeconds\":"
                        + decision.retryAfterSeconds()
                        + "}"
        );
    }

    private void cleanupPeriodically(long now) {

        // Avoid scanning the map on every request.
        if (requestCounter.incrementAndGet() % 1000 != 0) {
            return;
        }

        long idleCutoff = now - CLEANUP_IDLE_MS;

        for (String key : windows.keySet()) {
            windows.computeIfPresent(key, (ignored, state) ->
                    state.lastAccessMillis < idleCutoff
                            ? null
                            : state
            );
        }
    }
}