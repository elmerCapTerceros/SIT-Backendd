package com.gobernacionSIT.sit_backend.dto.response;

import java.time.LocalDateTime;

public record ReporteTrabajoResponse(
        Long id,
        Long solicitudId,
        Long asignacionTecnicoId,
        Long tecnicoId,
        String tipoEquipo,
        String diagnostico,
        String trabajoRealizado,
        String estadoFinal,
        String observacion,
        LocalDateTime createdAt
) {
}
