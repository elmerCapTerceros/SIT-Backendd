package com.gobernacionSIT.sit_backend.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActualizarPerfilRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 20, message = "El nombre no puede superar los 20 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 20, message = "El apellido no puede superar los 20 caracteres")
    private String apellido;

    @NotBlank(message = "El cargo es obligatorio")
    @Size(max = 20)
    private String cargo;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, max = 8, message = "La contraseña debe tener exactamente 8 caracteres")
    private String password;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "\\d{8}", message = "El teléfono debe tener exactamente 8 dígitos")
    private String telefono;

    @NotBlank(message = "El área es obligatoria")
    @Size(max = 20, message = "El área no puede superar los 20 caracteres")
    private String area;

    @NotBlank(message = "La ubicación de la oficina es obligatoria")
    @Size(max = 20, message = "La ubicación de oficina no puede superar los 20 caracteres")
    private String ubicacionOficina;
}
