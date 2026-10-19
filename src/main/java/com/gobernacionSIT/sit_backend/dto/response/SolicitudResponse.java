package com.gobernacionSIT.sit_backend.dto.response;

import java.time.LocalDateTime;

public record SolicitudResponse(
        Long id,
        String codigo,
        String titulo,
        String area,
        String ubicacion,
        String categoria,
        String descripcion,
        String prioridad,
        String estado,
        Long solicitanteId,
        String solicitanteNombre,
        Long tecnicoId,
        Long asignacionTecnicoId,
        Boolean verificadoPorUsuario,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String solicitanteCargo,
        String solicitanteTelefono,
        String motivoRechazo
) {
}