package com.gobernacionSIT.sit_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistroInicialRequest {

    @NotBlank(message = "La autorización de registro es obligatoria")
    private String registrationToken;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100)
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100)
    private String apellido;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, max = 100, message = "La contraseña debe tener entre 8 y 100 caracteres")
    private String password;

    @NotBlank(message = "El cargo es obligatorio")
    @Size(max = 20)
    private String cargo;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "\\d{7,8}", message = "El teléfono debe tener 7 u 8 dígitos")
    private String telefono;

    @NotBlank(message = "El área es obligatoria")
    @Size(max = 40)
    private String area;

    @NotBlank(message = "La ubicación de la oficina es obligatoria")
    @Size(max = 40)
    private String ubicacionOficina;
}
