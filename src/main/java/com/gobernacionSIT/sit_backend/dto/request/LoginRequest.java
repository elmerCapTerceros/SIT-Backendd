package com.gobernacionSIT.sit_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @NotBlank(message = "El usuario es obligatorio")
    private String userLogin;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}