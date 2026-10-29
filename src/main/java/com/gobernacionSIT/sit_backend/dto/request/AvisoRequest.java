package com.gobernacionSIT.sit_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record AvisoRequest(
        @NotNull Long idSupervisor,
        @NotBlank String mensaje,
        @NotBlank @Pattern(regexp = "FUNCIONARIO|TECNICO|AMBOS",
                message = "El destinatario debe ser FUNCIONARIO, TECNICO o AMBOS")
        String destinatario
) {}