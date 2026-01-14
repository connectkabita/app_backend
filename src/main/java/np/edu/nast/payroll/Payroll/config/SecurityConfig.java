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
                        .requestMatchers("/api/users/forgot-password/**", "/api/users/reset-password/**").permitAll()
                        .requestMatchers("/api/dashboard/**").permitAll()

                        /* ============================================================
                           2. ACCOUNTING & REPORTING (UPDATED FOR DASHBOARD & SUMMARY)
                           ============================================================ */
                        .requestMatchers(
                                "/api/payrolls/summary", // Matches your Frontend Salary.jsx fetch
                                "/api/payrolls/**",
                                "/api/reports/**",
                                "/api/tax-slabs/**",
                                "/api/salary-summary/**"
                        ).hasAnyAuthority("ROLE_ADMIN", "ROLE_ACCOUNTANT", "ADMIN", "ACCOUNTANT")

                        /* ============================================================
                           3. SHARED READ-ONLY ACCESS (UPDATED TO INCLUDE LEAVES & ATTENDANCE)
                           ============================================================ */
                        .requestMatchers(HttpMethod.GET,
                                "/api/employees/**",

                                "/api/departments/**",
                                "/api/designations/**",
                                "/api/salary-components/**",
                                "/api/grade-salary-components/**",
                                "/api/employee-salary-components/**",
                                "/api/attendance/**",
                                "/api/leaves/**", // Allow reading general leave data
                                "/api/leave-types/**"
                        ).hasAnyAuthority("ROLE_ADMIN", "ROLE_ACCOUNTANT", "ROLE_EMPLOYEE", "ADMIN", "ACCOUNTANT", "EMPLOYEE")

                        /* ============================================================
                           4. LEAVE & EMPLOYEE SELF-SERVICE
                           ============================================================ */
                        .requestMatchers(HttpMethod.GET, "/api/leave-balance/employee/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_EMPLOYEE", "ADMIN", "EMPLOYEE")
                        .requestMatchers("/api/employee-leaves/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_EMPLOYEE", "ADMIN", "EMPLOYEE")
                        .requestMatchers("/api/employee/**").hasAnyAuthority("ROLE_EMPLOYEE", "ROLE_ADMIN", "EMPLOYEE", "ADMIN")

                        /* ============================================================
                           5. SPECIFIC PANEL AUTHORIZATION
                           ============================================================ */
                        .requestMatchers("/api/accountant/**").hasAnyAuthority("ROLE_ACCOUNTANT", "ROLE_ADMIN", "ACCOUNTANT", "ADMIN")
                        .requestMatchers("/api/admin/**").hasAnyAuthority("ROLE_ADMIN", "ADMIN")

                        /* ============================================================
                           6. GLOBAL FALLBACK & WRITE RESTRICTIONS
                           ============================================================ */
                        // Allow Accountants to process Payrolls and Tax updates
                        .requestMatchers(HttpMethod.POST, "/api/payrolls/**", "/api/salary-summary/**", "/api/tax-slabs/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_ACCOUNTANT", "ADMIN", "ACCOUNTANT")
                        .requestMatchers(HttpMethod.PUT, "/api/payrolls/**", "/api/salary-summary/**", "/api/tax-slabs/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_ACCOUNTANT", "ADMIN", "ACCOUNTANT")

                        // Restrict sensitive deletions and general system config to ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/**").hasAnyAuthority("ROLE_ADMIN", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/**").hasAnyAuthority("ROLE_ADMIN", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/**").hasAnyAuthority("ROLE_ADMIN", "ADMIN")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:3000"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}