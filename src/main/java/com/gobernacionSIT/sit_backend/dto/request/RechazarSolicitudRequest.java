package com.gobernacionSIT.sit_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RechazarSolicitudRequest {

    @NotBlank(message = "El motivo del rechazo es obligatorio")
    @Size(min = 10, max = 500, message = "El motivo debe tener entre 10 y 500 caracteres")
    private String motivo;
}
