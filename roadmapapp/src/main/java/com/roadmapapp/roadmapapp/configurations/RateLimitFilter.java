package com.roadmapapp.roadmapapp.configurations;



import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {


    /*
     * ============================================================
     * REQUEST COUNTER
     * ============================================================
     */

    private static class RequestCounter {

        private int count;

        private long windowStart;


        public RequestCounter(long now) {

            this.count = 1;

            this.windowStart = now;
        }
    }


    /*
     * ============================================================
     * GLOBAL REQUESTS
     * ============================================================
     *
     * Key:
     *
     * IP
     *
     * Example:
     *
     * 192.168.1.10
     */

    private final Map<String, RequestCounter> globalCounters =
            new ConcurrentHashMap<>();


    /*
     * ============================================================
     * ENDPOINT REQUESTS
     * ============================================================
     *
     * Key:
     *
     * IP + endpoint
     *
     * Example:
     *
     * 192.168.1.10:/all_trackers
     */

    private final Map<String, RequestCounter> endpointCounters =
            new ConcurrentHashMap<>();


    /*
     * ============================================================
     * CLEANUP
     * ============================================================
     *
     * Prevents the maps from growing forever.
     */

    private volatile long lastCleanup =
            System.currentTimeMillis();


    private static final long CLEANUP_INTERVAL =
            5 * 60 * 1000L;


    /*
     * ============================================================
     * MAIN FILTER
     * ============================================================
     */

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {


        /*
         * --------------------------------------------------------
         * OPTIONS REQUEST
         * --------------------------------------------------------
         *
         * Allow CORS preflight requests.
         */

        if ("OPTIONS".equalsIgnoreCase(
                request.getMethod())) {

            filterChain.doFilter(request, response);

            return;
        }


        /*
         * --------------------------------------------------------
         * GET CLIENT IP
         * --------------------------------------------------------
         */

        String ip = getClientIp(request);


        /*
         * --------------------------------------------------------
         * REQUEST PATH
         * --------------------------------------------------------
         */

        String path =
                request.getRequestURI();


        /*
         * --------------------------------------------------------
         * GLOBAL LIMIT
         * --------------------------------------------------------
         *
         * 100 requests / minute / IP
         */

        RateLimitConfig.RateLimitRule globalLimit =
                RateLimitConfig.GLOBAL_LIMIT;


        String globalKey =
                "GLOBAL:" + ip;


        RateCheckResult globalResult =
                checkLimit(
                        globalCounters,
                        globalKey,
                        globalLimit
                );


        if (!globalResult.allowed()) {

            sendRateLimitResponse(
                    response,
                    globalResult
            );

            return;
        }


        /*
         * --------------------------------------------------------
         * ENDPOINT LIMIT
         * --------------------------------------------------------
         */

        RateLimitConfig.RateLimitRule endpointLimit =
                RateLimitConfig.getLimit(path);


        /*
         * If this endpoint doesn't have a specific rule,
         * only the global limit is applied.
         */

        if (endpointLimit != null) {

            String endpointKey =
                    "ENDPOINT:"
                            + ip
                            + ":"
                            + path;


            RateCheckResult endpointResult =
                    checkLimit(
                            endpointCounters,
                            endpointKey,
                            endpointLimit
                    );


            if (!endpointResult.allowed()) {

                sendRateLimitResponse(
                        response,
                        endpointResult
                );

                return;
            }
        }


        /*
         * --------------------------------------------------------
         * CLEAN OLD ENTRIES
         * --------------------------------------------------------
         */

        cleanupIfNeeded();


        /*
         * --------------------------------------------------------
         * ALLOW REQUEST
         * --------------------------------------------------------
         */

        filterChain.doFilter(
                request,
                response
        );
    }


    /*
     * ============================================================
     * RATE CHECK
     * ============================================================
     */

    private RateCheckResult checkLimit(
            Map<String, RequestCounter> counters,
            String key,
            RateLimitConfig.RateLimitRule limit) {


        long now =
                System.currentTimeMillis();


        long windowMilliseconds =
                limit.windowSeconds() * 1000L;


        RequestCounter counter =
                counters.get(key);


        /*
         * --------------------------------------------------------
         * FIRST REQUEST
         * --------------------------------------------------------
         */

        if (counter == null) {

            RequestCounter newCounter =
                    new RequestCounter(now);


            counters.put(
                    key,
                    newCounter
            );


            return new RateCheckResult(
                    true,
                    1,
                    limit.maxRequests(),
                    now + windowMilliseconds
            );
        }


        /*
         * --------------------------------------------------------
         * WINDOW EXPIRED
         * --------------------------------------------------------
         */

        synchronized (counter) {

            if (now - counter.windowStart
                    >= windowMilliseconds) {

                counter.count = 1;

                counter.windowStart = now;


                return new RateCheckResult(
                        true,
                        1,
                        limit.maxRequests(),
                        now + windowMilliseconds
                );
            }


            /*
             * ----------------------------------------------------
             * LIMIT EXCEEDED
             * ----------------------------------------------------
             */

            if (counter.count
                    >= limit.maxRequests()) {

                long resetTime =
                        counter.windowStart
                                + windowMilliseconds;


                return new RateCheckResult(
                        false,
                        counter.count,
                        limit.maxRequests(),
                        resetTime
                );
            }


            /*
             * ----------------------------------------------------
             * INCREMENT COUNTER
             * ----------------------------------------------------
             */

            counter.count++;


            long resetTime =
                    counter.windowStart
                            + windowMilliseconds;


            return new RateCheckResult(
                    true,
                    counter.count,
                    limit.maxRequests(),
                    resetTime
            );
        }
    }


    /*
     * ============================================================
     * CLIENT IP
     * ============================================================
     */

    private String getClientIp(
            HttpServletRequest request) {


        /*
         * Render / reverse proxy generally forwards
         * the client address using X-Forwarded-For.
         *
         * The first IP is the original client.
         */

        String forwardedFor =
                request.getHeader(
                        "X-Forwarded-For"
                );


        if (forwardedFor != null
                && !forwardedFor.isBlank()) {

            return forwardedFor
                    .split(",")[0]
                    .trim();
        }


        /*
         * Fallback.
         */

        return request.getRemoteAddr();
    }


    /*
     * ============================================================
     * 429 RESPONSE
     * ============================================================
     */

    private void sendRateLimitResponse(
            HttpServletResponse response,
            RateCheckResult result)
            throws IOException {


        long now =
                System.currentTimeMillis();


        long remainingMilliseconds =
                Math.max(
                        0,
                        result.resetTime()
                                - now
                );


        long retryAfterSeconds =
                Math.max(
                        1,
                        (remainingMilliseconds + 999) / 1000
                );


//        response.setStatus(
//                HttpServletResponse.SC_TOO_MANY_REQUESTS
//        );
        response.setStatus(429);


        response.setContentType(
                "application/json"
        );


        response.setCharacterEncoding(
                "UTF-8"
        );


        response.setHeader(
                "Retry-After",
                String.valueOf(
                        retryAfterSeconds
                )
        );


        response.getWriter().write(
                """
                {
                  "error": "RATE_LIMIT_EXCEEDED",
                  "message": "Too many requests. Please try again later.",
                  "retryAfterSeconds": %d
                }
                """.formatted(
                        retryAfterSeconds
                )
        );
    }


    /*
     * ============================================================
     * CLEANUP
     * ============================================================
     */

    private void cleanupIfNeeded() {


        long now =
                System.currentTimeMillis();


        if (now - lastCleanup
                < CLEANUP_INTERVAL) {

            return;
        }


        synchronized (this) {

            if (now - lastCleanup
                    < CLEANUP_INTERVAL) {

                return;
            }


            removeExpiredEntries(
                    globalCounters,
                    now
            );


            removeExpiredEntries(
                    endpointCounters,
                    now
            );


            lastCleanup = now;
        }
    }


    /*
     * ============================================================
     * REMOVE EXPIRED ENTRIES
     * ============================================================
     */

    private void removeExpiredEntries(
            Map<String, RequestCounter> counters,
            long now) {


        counters.entrySet().removeIf(
                entry -> {

                    RequestCounter counter =
                            entry.getValue();


                    /*
                     * Keep entries for a reasonable amount of
                     * time. The exact endpoint window is not
                     * available from the counter itself, so
                     * five minutes is used as a cleanup threshold.
                     *
                     * Long-window entries are also safely removed
                     * once inactive long enough.
                     */

                    return now
                            - counter.windowStart
                            > 2 * 60 * 60 * 1000L;
                }
        );
    }


    /*
     * ============================================================
     * RESULT
     * ============================================================
     */

    private record RateCheckResult(
            boolean allowed,
            int currentCount,
            int maxRequests,
            long resetTime
    ) {
    }
}
