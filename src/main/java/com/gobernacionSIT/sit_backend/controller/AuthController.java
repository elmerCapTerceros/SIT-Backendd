package com.gobernacionSIT.sit_backend.controller;

import com.gobernacionSIT.sit_backend.dto.request.LoginRequest;
import com.gobernacionSIT.sit_backend.dto.response.LoginResponse;
import com.gobernacionSIT.sit_backend.security.JwtUtils;
import com.gobernacionSIT.sit_backend.service.UsuarioService;
import com.gobernacionSIT.sit_backend.dto.response.UsuarioResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UsuarioService usuarioService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUserLogin(), request.getPassword())
        );

        String token = jwtUtils.generateToken(authentication.getName());
        String rol = authentication.getAuthorities().stream()
                .findFirst()
                .map(authority -> authority.getAuthority().replaceFirst("^ROLE_", ""))
                .orElse(null);
            UsuarioResponse usuario = usuarioService.buscarPorUserLogin(authentication.getName());

        return ResponseEntity.ok(new LoginResponse(
            token,
            authentication.getName(),
            rol,
                usuarioService.esPrimerIngreso(authentication.getName()),
                usuario.getNombre(),
                usuario.getApellido()
        ));
    }
}