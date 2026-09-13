package com.gobernacionSIT.sit_backend.dto.response;

import java.time.LocalDateTime;

public record SolicitudResponse(
        Long id,
        String titulo,
        String area,
        String categoria,
        String descripcion,
        String prioridad,
        String estado,
        Long solicitanteId,
        Long tecnicoId,
        Boolean verificadoPorUsuario,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}