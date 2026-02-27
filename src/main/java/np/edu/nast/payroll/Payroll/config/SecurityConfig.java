package np.edu.nast.payroll.Payroll.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import np.edu.nast.payroll.Payroll.security.CustomUserDetailsService;
import np.edu.nast.payroll.Payroll.security.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final CustomUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Warning: NoOpPasswordEncoder is only for testing. Use BCrypt for production.
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Allow pre-flight OPTIONS requests
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // --- PUBLIC AUTH ENDPOINTS ---
                        // Explicitly permitting all auth endpoints to avoid 401/403 errors
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/auth/login", "/api/auth/signup", "/api/auth/register").permitAll()
                        .requestMatchers("/api/auth/forgot-password", "/api/auth/verify-otp", "/api/auth/reset-password").permitAll()

                        // Internal error path
                        .requestMatchers("/error").permitAll()

                        // --- SECURED ENDPOINTS ---
                        // Employee/User permissions
                        .requestMatchers("/api/leave/my-history/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN", "ROLE_EMPLOYEE")
                        .requestMatchers("/api/leave/apply/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN", "ROLE_EMPLOYEE")
                        .requestMatchers("/api/attendance/**").hasAnyAuthority("ROLE_USER", "ROLE_EMPLOYEE", "ROLE_ADMIN")
                        .requestMatchers("/api/payrolls/command-center/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN", "ROLE_EMPLOYEE")

                        // Admin-only permissions
                        .requestMatchers("/api/leave/all/**", "/api/leave/status/**", "/api/payrolls/**").hasAuthority("ROLE_ADMIN")

                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            log.error("Unauthorized error at {}: {}", request.getRequestURI(), authException.getMessage());
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"success\": false, \"message\": \"Unauthorized: " + authException.getMessage() + "\"}");
                        })
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Allow all origins for mobile development testing
        configuration.setAllowedOriginPatterns(Collections.singletonList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type", "Accept"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(Collections.singletonList("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}