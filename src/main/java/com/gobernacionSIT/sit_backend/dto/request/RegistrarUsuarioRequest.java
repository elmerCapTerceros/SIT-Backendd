package com.gobernacionSIT.sit_backend.dto.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class RegistrarUsuarioRequest {

    @Size(max = 100)
    private String nombre;


    @Size(max = 100)
    private String apellido;

    @NotBlank(message = "El usuario de acceso es obligatorio")
    @Size(max = 40)
    private String userLogin;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;

    @Size(max = 20)
    private String cargo;

    @Pattern(regexp = "\\d{7,8}", message = "El teléfono debe tener 7 u 8 dígitos")
    private String telefono;

    @Size(max = 40)
    private String area;

    @Size(max = 40)
    private String ubicacionOficina;

    private String nombreRol;
}
