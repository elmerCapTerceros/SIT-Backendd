package com.gobernacionSIT.sit_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponse {

    private String token;
    private String userLogin;
    private String rol;
    private boolean primerIngreso;
    private String nombre;
    private String apellido;
}
