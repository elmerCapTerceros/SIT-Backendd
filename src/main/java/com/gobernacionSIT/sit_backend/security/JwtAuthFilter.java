package com.gobernacionSIT.sit_backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (jwtUtils.isTokenValid(token)) {
                String userLogin = jwtUtils.getUserLoginFromToken(token);
                SitUserDetails userDetails = userDetailsService.loadUserByUsername(userLogin);

                var authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
                boolean primerIngreso = userDetails.isPrimerIngreso();
                if (primerIngreso && !esActualizacionInicialPermitida(request)) {
                    SecurityContextHolder.clearContext();
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write(
                            "{\"status\":403,\"error\":\"FIRST_LOGIN_REQUIRED\","
                                    + "\"message\":\"Debe completar su registro inicial antes de continuar\"}");
                    return;
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean esActualizacionInicialPermitida(HttpServletRequest request) {
        return "PUT".equals(request.getMethod())
                && "/api/usuarios/me".equals(request.getServletPath());
    }
}