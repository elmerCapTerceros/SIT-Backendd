package com.gobernacionSIT.sit_backend.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtUtilsTest {

    @Test
    void rejectsSecretsShorterThanThirtyTwoBytes() {
        assertThrows(IllegalArgumentException.class, () -> new JwtUtils("too-short-secret"));
    }

    @Test
    void validatesTokensWithConfiguredSecret() {
        JwtUtils jwtUtils = new JwtUtils("test-secret-which-is-long-enough-for-hmac-sha256");
        String token = jwtUtils.generateToken("tecnico");

        assertTrue(jwtUtils.isTokenValid(token));
        assertFalse(jwtUtils.isTokenValid("invalid-token"));
    }
}
