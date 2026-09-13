package com.gobernacionSIT.sit_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class UsuarioResponse {

    private Long id;
    private String nombre;
    private String apellido;
    private String userLogin;
    private String cargo;
    private String telefono;
    private String area;
    private String ubicacionOficina;
    private String rol;
    private LocalDateTime createdAt;
}