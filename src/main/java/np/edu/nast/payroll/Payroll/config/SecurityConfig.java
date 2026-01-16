package np.edu.nast.payroll.Payroll.config;

import lombok.RequiredArgsConstructor;
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
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final CustomUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        // ⚠️ Reminder: Use BCryptPasswordEncoder for production environments
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())

                .authorizeHttpRequests(auth -> auth

                        /* ============================================================
                           1. PUBLIC ENDPOINTS
                           ============================================================ */
                        .requestMatchers("/api/auth/**", "/error").permitAll()
                        .requestMatchers("/api/users/forgot-password/**",
                                "/api/users/reset-password/**").permitAll()
                        .requestMatchers("/api/dashboard/**").permitAll()
                        // allow employee to see salary
                        .requestMatchers("/api/salary-analytics/**").authenticated()

                        /* ============================================================
                           2. LEAVE MANAGEMENT (FIXED: Added Employee Access)
                           ============================================================ */
                        // Allow employees to fetch types and their own balance
                        .requestMatchers(HttpMethod.GET, "/api/leave-types/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_EMPLOYEE")
                        .requestMatchers(HttpMethod.GET, "/api/leave-balance/employee/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_EMPLOYEE")

                        // Allow employees to view their history and submit requests
                        .requestMatchers("/api/employee-leaves/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_EMPLOYEE")


                        /* ============================================================
                           3. SHARED MODULES (ADMIN, ACCOUNTANT, EMPLOYEE)
                           ============================================================ */
                        .requestMatchers("/api/attendance/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_ACCOUNTANT", "ROLE_EMPLOYEE")

                        /* ============================================================
                           4. ACCOUNTING & MANAGEMENT (ADMIN, ACCOUNTANT)
                           ============================================================ */
                        .requestMatchers("/api/payrolls/**", "/api/reports/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_ACCOUNTANT")

                        // Read-only access for Accountants on Employees, Depts, and Designations
                        .requestMatchers(HttpMethod.GET,
                                "/api/employees/**",
                                "/api/departments/**",
                                "/api/designations/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_ACCOUNTANT")

                        // Read-only access for Accountants on Salary Components
                        .requestMatchers(HttpMethod.GET,
                                "/api/salary-components/**",
                                "/api/grade-salary-components/**",
                                "/api/employee-salary-components/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_ACCOUNTANT")

                        /* ============================================================
                           5. ROLE-SPECIFIC PANELS
                           ============================================================ */
                        .requestMatchers("/api/employee/**").hasAuthority("ROLE_EMPLOYEE")
                        .requestMatchers("/api/accountant/**").hasAuthority("ROLE_ACCOUNTANT")
                        .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")

                        /* ============================================================
                           6. ADMIN ONLY (WRITE ACCESS & FALLBACK)
                           ============================================================ */
                        // Any remaining POST/PUT/DELETE on core modules defaults to ADMIN
                        .requestMatchers("/api/employees/**",
                                "/api/departments/**",
                                "/api/designations/**",
                                "/api/salary-components/**").hasAuthority("ROLE_ADMIN")

                        // Global catch-all for /api/** (Admins)
                        .requestMatchers("/api/**").hasAuthority("ROLE_ADMIN")

                        /* ============================================================
                           7. FINAL FALLBACK
                           ============================================================ */
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://localhost:3000"
        ));
        config.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}