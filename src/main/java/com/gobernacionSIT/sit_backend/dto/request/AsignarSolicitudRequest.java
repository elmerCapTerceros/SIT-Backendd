package com.gobernacionSIT.sit_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AsignarSolicitudRequest {

    @NotBlank(message = "La prioridad es obligatoria")
    @Pattern(regexp = "ALTA|MEDIA|BAJA", message = "La prioridad debe ser ALTA, MEDIA o BAJA")
    private String prioridad;

    @NotNull(message = "El técnico es obligatorio")
    private Long tecnicoId;
}