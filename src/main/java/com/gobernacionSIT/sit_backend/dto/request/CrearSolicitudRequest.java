package com.gobernacionSIT.sit_backend.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CrearSolicitudRequest {

    @NotBlank(message = "El título es obligatorio")
    @Size(min = 5, max = 200, message = "El título debe tener entre 5 y 200 caracteres")
    private String titulo;

    @NotBlank(message = "El tipo es obligatorio")
    @Pattern(regexp = "PC|Laptop|Impresora|Otros", message = "El tipo debe ser PC, Laptop, Impresora u Otros")
    @JsonAlias("categoria")
    private String tipo;

    @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
    private String descripcion;
}