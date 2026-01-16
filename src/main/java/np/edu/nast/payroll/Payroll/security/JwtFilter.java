package np.edu.nast.payroll.Payroll.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

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

        // 1. Skip filter if header is missing or incorrect
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);

        // 2. Handle invalid frontend strings
        if (token.isBlank() || "null".equalsIgnoreCase(token) || "undefined".equalsIgnoreCase(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String username = jwtUtils.getUsernameFromToken(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtUtils.validateToken(token)) {
                    // Pass authorities directly from UserDetails to maintain the ROLE_ prefix
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    // Optional: Debug log to verify authorities in console
                    // System.out.println("Authenticated: " + username + " with " + userDetails.getAuthorities());
                }
            }
        } catch (ExpiredJwtException ex) {
            sendUnauthorized(response, "TOKEN_EXPIRED", "Session expired. Please login again.");
            return; // Stop the chain
        } catch (JwtException | IllegalArgumentException ex) {
            sendUnauthorized(response, "INVALID_TOKEN", "Invalid security token.");
            return; // Stop the chain
        } catch (Exception ex) {
            sendUnauthorized(response, "AUTH_ERROR", "Authentication failed.");
            return; // Stop the chain
        }

        filterChain.doFilter(request, response);
    }

    private void sendUnauthorized(HttpServletResponse response, String error, String message) throws IOException {
        SecurityContextHolder.clearContext();
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        String jsonResponse = String.format("{\"error\": \"%s\", \"message\": \"%s\"}", error, message);
        response.getWriter().write(jsonResponse);
    }
}