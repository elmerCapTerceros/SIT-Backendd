package com.gobernacionSIT.sit_backend.security;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JwtAuthFilterTest {

    private static final String SECRET = "test-secret-which-is-long-enough-for-hmac-sha256";
    private final JwtUtils jwtUtils = new JwtUtils(SECRET);
    private final UserDetailsServiceImpl userDetailsService = mock(UserDetailsServiceImpl.class);
    private final JwtAuthFilter filter = new JwtAuthFilter(jwtUtils, userDetailsService);

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void blocksFirstLoginFromUsingOtherApiRoutes() throws Exception {
        mockFirstLoginUser();
        MockHttpServletRequest request = request("GET", "/api/solicitudes/misSolicitudes");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        assertEquals(403, response.getStatus());
        assertTrue(response.getContentAsString().contains("FIRST_LOGIN_REQUIRED"));
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    void allowsFirstLoginToCompleteProfile() throws Exception {
        mockFirstLoginUser();
        MockHttpServletRequest request = request("PUT", "/api/usuarios/me");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
    }

    private void mockFirstLoginUser() {
        SitUserDetails userDetails = new SitUserDetails(
                "funcionario",
                "encoded-password",
                List.of(new SimpleGrantedAuthority("ROLE_FUNCIONARIO")),
                true
        );
        when(userDetailsService.loadUserByUsername("funcionario")).thenReturn(userDetails);
    }

    private MockHttpServletRequest request(String method, String path) {
        MockHttpServletRequest request = new MockHttpServletRequest(method, path);
        request.setServletPath(path);
        request.addHeader("Authorization", "Bearer " + jwtUtils.generateToken("funcionario"));
        return request;
    }
}
