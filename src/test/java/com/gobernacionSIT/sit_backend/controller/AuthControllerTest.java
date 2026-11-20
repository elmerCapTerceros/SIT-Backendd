package com.gobernacionSIT.sit_backend.controller;

import com.gobernacionSIT.sit_backend.exception.GlobalExceptionHandler;
import com.gobernacionSIT.sit_backend.security.JwtUtils;
import com.gobernacionSIT.sit_backend.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class AuthControllerTest {

    @Test
    void invalidPasswordReturnsUnauthorizedWithoutAuthenticationDetails() throws Exception {
        assertUnauthorized(new BadCredentialsException("Sensitive authentication details"));
    }

    @Test
    void unknownUserReturnsSameUnauthorizedResponseAsInvalidPassword() throws Exception {
        assertUnauthorized(new UsernameNotFoundException("unregistered-test-user"));
    }

    private void assertUnauthorized(AuthenticationException authenticationFailure) throws Exception {
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenThrow(authenticationFailure);
        AuthController controller = new AuthController(
                authenticationManager,
                mock(JwtUtils.class),
                mock(UsuarioService.class));
        MockMvc mockMvc = standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userLogin": "qa-user",
                                  "password": "wrong-password"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.message").value("Usuario o contraseña incorrectos"))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assertFalse(responseBody.contains(authenticationFailure.getMessage()));
    }
}
