package com.campushub.event_service.security;

import com.campushub.event_service.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        System.out.println("\n===== 🛡️ SECURITY INTERCEPTION START =====");
        System.out.println("Raw Authorization Header: " + authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            boolean isValid = jwtUtil.isTokenValid(token);
            System.out.println("Is Token Cryptographically Valid? " + isValid);

            if (isValid) {
                String username = jwtUtil.extractUsername(token);
                List<String> roles = jwtUtil.extractRoles(token);
                
                System.out.println("Extracted Username from Token: " + username);
                System.out.println("Extracted Roles List from Token: " + roles);

                if (roles == null || roles.isEmpty()) {
                    System.out.println("⚠️ WARNING: Roles list extracted from JWT is completely EMPTY or NULL!");
                }

                List<SimpleGrantedAuthority> authorities = roles != null ? roles.stream()
                        .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList()) : List.of();

                System.out.println("Final Transformed Granted Authorities: " + authorities);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(username, null, authorities);

                SecurityContextHolder.getContext().setAuthentication(authentication);
                System.out.println("Status: User locked into Spring Security Context successfully.");
            }
        } else {
            System.out.println("⚠️ WARNING: Missing or incorrectly formatted Bearer token header!");
        }
        System.out.println("===== 🛡️ SECURITY INTERCEPTION END =====\n");

        filterChain.doFilter(request, response);
    }
}