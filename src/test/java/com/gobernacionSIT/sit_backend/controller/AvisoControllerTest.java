package com.gobernacionSIT.sit_backend.controller;

import com.gobernacionSIT.sit_backend.exception.GlobalExceptionHandler;
import com.gobernacionSIT.sit_backend.repository.AvisoRepository;
import com.gobernacionSIT.sit_backend.repository.UsuarioRepository;
import com.gobernacionSIT.sit_backend.service.AvisoService;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class AvisoControllerTest {

    private final AvisoRepository avisoRepository = mock(AvisoRepository.class);
    private final AvisoService avisoService = new AvisoService(
            avisoRepository,
            mock(UsuarioRepository.class));
    private final MockMvc mockMvc = standaloneSetup(new AvisoController(avisoService))
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();

    @Test
    void nonexistentZeroAvisoReturnsNotFound() throws Exception {
        assertNotFound(0L);
    }

    @Test
    void nonexistentNegativeAvisoReturnsNotFound() throws Exception {
        assertNotFound(-1L);
    }

    @Test
    void nonNumericAvisoIdReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/avisos/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("INVALID_PARAMETER"));

        verifyNoInteractions(avisoRepository);
    }

    private void assertNotFound(Long id) throws Exception {
        when(avisoRepository.findById(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/avisos/{id}", id).principal(supervisorAuthentication()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"));

        verify(avisoRepository).findById(id);
    }

    private Authentication supervisorAuthentication() {
        return UsernamePasswordAuthenticationToken.authenticated(
                "qa-supervisor",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_SUPERVISOR")));
    }
}
