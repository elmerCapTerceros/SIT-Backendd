package com.gobernacionSIT.sit_backend.dto.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class RegistrarUsuarioRequest {

    @Size(max = 20, message = "El nombre no puede superar los 20 caracteres")
    private String nombre;


    @Size(max = 20, message = "El apellido no puede superar los 20 caracteres")
    private String apellido;

    @NotBlank(message = "El usuario de acceso es obligatorio")
    @Size(max = 30, message = "El usuario de acceso no puede superar los 30 caracteres")
    private String userLogin;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, max = 8, message = "La contraseña debe tener exactamente 8 caracteres")
    private String password;

    @Size(max = 20, message = "El cargo no puede superar los 20 caracteres")
    private String cargo;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "\\d{8}", message = "El teléfono debe tener exactamente 8 dígitos")
    private String telefono;

    @Size(max = 20, message = "El área no puede superar los 20 caracteres")
    private String area;

    @Size(max = 20, message = "La ubicación de oficina no puede superar los 20 caracteres")
    private String ubicacionOficina;

    private String nombreRol;
}
