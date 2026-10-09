package com.roadmapapp.roadmapapp.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class config {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // ✅ ENABLE CORS FIRST
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // ✅ DISABLE CSRF FOR API
                .csrf(csrf -> csrf.disable())

                // ✅ AUTH RULES
                .authorizeHttpRequests(auth -> auth

                        // allow preflight requests
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // your public endpoints
                        .requestMatchers(
                                "/Register-User",
                                "/Login-User",
                                "/Static_Roadmaps/Category",
                                "/Static_Roadmaps/**",
                                "/All_RoadMap/**",
                                "/Profile/**",
                                "/Custom_Roadmap",
                                "/tracker",
                                "/all_trackers",
                                "/Custom_Tracker",
                                "/delete_trackers",
                                "/save_tracker",
                                "/payment/**",
                                "/plan/**",
                                "/send",
                                "/verify",
                                "/editPassword",
                                "/deleteAccount",
                                "/LogOut-User",
                                "/payment/failed",
                                "/health"
                        ).permitAll()
                        // everything else protected
                        .anyRequest().authenticated()
                )

                // ✅ SECURITY HEADERS
                .headers(headers -> headers
                        .contentSecurityPolicy(csp -> csp
                                .policyDirectives(
                                        "default-src 'self'; " +
                                                "script-src 'self' 'unsafe-inline'; " +
                                                "style-src 'self' 'unsafe-inline' https://fonts.googleapis.com; " +
                                                "font-src 'self' https://fonts.gstatic.com; " +
                                                "img-src 'self' data: https:; " +
                                                "connect-src 'self' " +


                                                "http://localhost:8080 " +
                                                "http://127.0.0.1:5501 " +
                                                "http://localhost:4321 " +
                                                "http://192.168.1.105:5501"+
                                                "http://192.168.0.178:5501"+
                                                "http://192.168.29.171:8081;"
                                )
                        )
                )

                // ✅ STATELESS SESSION
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }


    // ✅ GLOBAL CORS CONFIG (CRITICAL FIX)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        // allow your frontend domains
        config.setAllowedOrigins(List.of(
                "http://127.0.0.1:5501",
                "http://192.168.0.178:5501",
                "http://localhost:5501",
                "http://192.168.29.171:5501",
                "https://frontend-rust-iota-qby7aguy8j.vercel.app"
        ));

        // allow ALL methods
        config.setAllowedMethods(List.of(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "OPTIONS",
                "PATCH"
        ));

        // allow ALL headers (fixes most CORS errors)
        config.setAllowedHeaders(List.of("*"));

        // allow credentials (cookies, auth headers)
        config.setAllowCredentials(true);

        // cache preflight response
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
