package com.job_board.user_sevice.filter;

import com.job_board.user_sevice.service.UserDetailsServiceImpl;
import com.job_board.user_sevice.util.jwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.CodePointLength;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final jwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        final String token = authHeader.substring(7);

        try {
            // 4. extract email from the token
            final String email = jwtUtil.extractEmail(token);

            // 5. only proceed if we got an email AND user is not already authenticated
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // 6. load user from database
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                // 7. validate the token against the loaded user
                if (jwtUtil.isTokenValid(token, userDetails.getUsername())) {

                    // 8. create an authentication object
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,                        // credentials null — we use JWT, not password
                                    userDetails.getAuthorities() // roles: [ROLE_CANDIDATE]
                            );

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    // 9. tell Spring Security "this user is authenticated"
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }
        catch (Exception e) {
            // token is invalid or expired — don't set authentication
            // Spring Security will reject the request automatically
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid or expired token");
            return;
        }
        filterChain.doFilter(request, response);
    }
}
