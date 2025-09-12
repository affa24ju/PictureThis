package com.PictureThis.PictureThis.JWTsecurity;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JWTRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JWTUtil jwtUtil;
 // filter som körs på varje inkommande HTTP-request.
    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        // Hämta Authorization-headern
        final String authHeader = request.getHeader("Authorization");
         // Kollar om Authorization-header innehåller en giltig JWT-token.
        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            token = authHeader.substring(7);
        }

        // Om token är giltig sätts användaren som inloggad i SecurityContext.
        // Om token är ogiltig returneras 401 Unauthorized.
        if (token != null && jwtUtil.validateToken(token)) {
            String userId = jwtUtil.extractUserId(token);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userId, null,
                    List.of());

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } else if (token != null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // Fortsätt med nästa filter i kedjan
        filterChain.doFilter(request, response);
    }
}
