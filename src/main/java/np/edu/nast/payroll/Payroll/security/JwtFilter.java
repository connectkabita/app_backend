package np.edu.nast.payroll.Payroll.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            final String token = authHeader.substring(7);

            try {
                // Defensive check for common mobile client issues
                if (token.isEmpty() || "null".equalsIgnoreCase(token)) {
                    filterChain.doFilter(request, response);
                    return;
                }

                String username = jwtUtils.getUsernameFromToken(token);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    if (jwtUtils.validateToken(token)) {
                        // Load core user details (password/enabled status) from DB
                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                        // 1. Extract role directly from the JWT (e.g., "ROLE_ADMIN")
                        String rawRole = jwtUtils.extractRole(token);

                        if (rawRole != null) {
                            // 2. Normalize to strict ROLE_ format
                            String processedRole = rawRole.trim().toUpperCase();
                            if (!processedRole.startsWith("ROLE_")) {
                                processedRole = "ROLE_" + processedRole;
                            }

                            // 3. Create authority specifically from the token's claim
                            // This prevents a 403 if the DB role name differs slightly from the JWT role claim
                            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(processedRole);
                            List<SimpleGrantedAuthority> authorities = Collections.singletonList(authority);

                            log.info("JWT Authentication Success: User [{}] authenticated with Authority [{}]", username, processedRole);

                            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()// Use authorities derived from JWT
                            );

                            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authToken);
                        } else {
                            log.warn("Access Denied: Token validated but no 'role' claim found for user [{}]", username);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("JWT Filter validation failed: {}", e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}