package com.gobernacionSIT.sit_backend.dto.response;

import java.time.LocalDateTime;

public record AvisoResponse(
        Long id,
        Long idSupervisor,
        String mensaje,
        String destinatario,
        boolean activo,
        LocalDateTime createdAt
) {}