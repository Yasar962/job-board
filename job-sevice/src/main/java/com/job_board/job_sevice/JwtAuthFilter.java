package com.job_board.job_sevice;
import com.job_board.job_sevice.util.jwtUtil;
import io.jsonwebtoken.Claims;
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

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    // only JwtUtil is needed — no UserDetailsService
    private final jwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // step 1: read the Authorization header
        final String authHeader = request.getHeader("Authorization");

        // step 2: if no header or wrong format → skip this filter
        // the request will hit SecurityConfig rules and get rejected if auth is required
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // step 3: remove "Bearer " prefix to get the raw token
        final String token = authHeader.substring(7);

        try {
            // step 4: extract ALL claims from the token at once
            // this also verifies the signature using the secret key
            // if the token was tampered with → this line throws an exception
            // if the token is expired → this line throws an exception
            Claims claims = jwtUtil.extractAllClaims(token);

            // step 5: read what we need directly from the token
            String email  = claims.getSubject();              // "yasar@gmail.com"
            String role   = claims.get("role", String.class); // "CANDIDATE" or "EMPLOYER"
            Long   userId = claims.get("userId", Long.class);

            System.out.println("=============================");
            System.out.println("EMAIL  : " + email);
            System.out.println("ROLE   : " + role);
            System.out.println("USERID : " + userId);
            System.out.println("AUTH   : ROLE_" + role);
            System.out.println("=============================");

            // step 6: only set authentication if not already set
            if (email != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                // step 7: build authentication object using data FROM the token
                // no database call needed — everything is in the token
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                email,     // principal → who is this person (email)
                                userId,    // credentials → we store userId here for the controller
                                List.of(new SimpleGrantedAuthority("ROLE_" + role))
                                // authorities → what role they have
                                // "ROLE_CANDIDATE" or "ROLE_EMPLOYER"
                                // Spring needs "ROLE_" prefix for hasRole() to work
                        );

                // step 8: tell Spring Security this request is authenticated
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        } catch (Exception e) {
            // token is invalid, expired, or tampered
            // clear any partial authentication
            SecurityContextHolder.clearContext();

            // return 401 immediately — don't let request proceed
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(
                    "{\"error\": \"Invalid or expired token\", \"status\": 401}"
            );
            return;
        }

        // step 9: token was valid — continue to the controller
        filterChain.doFilter(request, response);
    }
}
