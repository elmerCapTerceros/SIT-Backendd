package com.gobernacionSIT.sit_backend.dto.response;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;

public record ReporteTrabajoResponse(
        Long id,
        Long solicitudId,
        Long asignacionTecnicoId,
        Long tecnicoId,
        String codigoSolicitud,
        String numeroFormulario,
        LocalDate fechaAtencion,
        LocalTime horaAtencion,
        String unidad,
        String solicitante,
        String numeroInventario,
        String tipoEquipo,
        String tipoEquipoOtro,
        String problema,
        String diagnostico,
        String trabajoRealizado,
        String estadoFinal,
        String observacion,
        String tecnicoNombre,
        String funcionarioNombre,
        boolean verificacion,
        LocalDateTime createdAt
) {
}
