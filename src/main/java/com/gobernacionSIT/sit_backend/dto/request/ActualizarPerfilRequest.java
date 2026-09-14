package com.gobernacionSIT.sit_backend.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActualizarPerfilRequest {

    @Size(max = 100)
    private String nombre;

    @Size(max = 100)
    private String apellido;

    @Size(max = 20)
    private String cargo;

    @Size(max = 20)
    private String password;

    @Pattern(regexp = "\\d{7,8}", message = "El teléfono debe tener 7 u 8 dígitos")
    private String telefono;

    @Size(max = 40)
    private String area;

    @Size(max = 40)
    private String ubicacionOficina;
}
