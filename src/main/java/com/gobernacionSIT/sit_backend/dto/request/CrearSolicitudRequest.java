package com.gobernacionSIT.sit_backend.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CrearSolicitudRequest {

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 200)
    private String titulo;

    @NotBlank(message = "El tipo es obligatorio")
    @Size(max = 100)
    @JsonAlias("categoria")
    private String tipo;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;
}