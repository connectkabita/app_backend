package np.edu.nast.payroll.Payroll.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // 1. Skip filter if no Bearer token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);

        // 2. Handle cases where frontend sends "Bearer null" or "Bearer undefined"
        if (token.isBlank() || "null".equalsIgnoreCase(token) || "undefined".equalsIgnoreCase(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String username = jwtUtils.getUsernameFromToken(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtUtils.validateToken(token)) {
                    // Logic: Map authorities to ensure they have ROLE_ prefix for SecurityConfig matching
                    List<SimpleGrantedAuthority> authorities = userDetails.getAuthorities().stream()
                            .map(grantedAuthority -> {
                                String auth = grantedAuthority.getAuthority();
                                return new SimpleGrantedAuthority(auth.startsWith("ROLE_") ? auth : "ROLE_" + auth);
                            })
                            .collect(Collectors.toList());

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    authorities
                            );

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
            // Continue the chain if authentication is successful
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException ex) {
            sendUnauthorized(response, "TOKEN_EXPIRED", "JWT token has expired. Please login again.");
        } catch (JwtException | IllegalArgumentException ex) {
            sendUnauthorized(response, "INVALID_TOKEN", "JWT token is invalid.");
        } catch (Exception ex) {
            sendUnauthorized(response, "AUTH_ERROR", "An error occurred during authentication.");
        }
    }

    private void sendUnauthorized(
            HttpServletResponse response,
            String error,
            String message
    ) throws IOException {
        SecurityContextHolder.clearContext();
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        String jsonResponse = String.format(
                "{\"error\": \"%s\", \"message\": \"%s\"}",
                error, message
        );
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }
}