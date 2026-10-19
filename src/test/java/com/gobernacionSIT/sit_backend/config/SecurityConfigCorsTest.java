package com.gobernacionSIT.sit_backend.config;

import com.gobernacionSIT.sit_backend.security.JwtAuthFilter;
import com.gobernacionSIT.sit_backend.security.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

class SecurityConfigCorsTest {

    @Test
    void allowsPatchPreflightFromLocalFrontend() {
        SecurityConfig securityConfig = new SecurityConfig(
                mock(UserDetailsServiceImpl.class),
                mock(JwtAuthFilter.class));
        MockHttpServletRequest request = new MockHttpServletRequest("OPTIONS", "/api/solicitudes/3/concluir");
        request.addHeader(HttpHeaders.ORIGIN, "http://localhost:5173");
        request.addHeader(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "PATCH");
        request.addHeader(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "authorization,content-type");

        CorsConfiguration configuration = securityConfig.corsConfigurationSource().getCorsConfiguration(request);

        assertNotNull(configuration);
        assertEquals("http://localhost:5173", configuration.checkOrigin("http://localhost:5173"));
        assertNotNull(configuration.checkHttpMethod(HttpMethod.PATCH));
        assertNotNull(configuration.checkHeaders(List.of("authorization", "content-type")));
    }
}
