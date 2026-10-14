package com.gobernacionSIT.sit_backend.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ActualizarEstadoSolicitudRequest {

    @NotNull(message = "La solicitud es obligatoria")
    @Positive(message = "El ID de la solicitud debe ser mayor que cero")
    private Long solicitud;

    @NotNull(message = "El estado es obligatorio")
    @Positive(message = "El ID del estado debe ser mayor que cero")
    private Long estadoId;

    public Long getSolicitud() {
        return solicitud;
    }

    public void setSolicitud(Long solicitud) {
        this.solicitud = solicitud;
    }

    public Long getEstadoId() {
        return estadoId;
    }

    public void setEstadoId(Long estadoId) {
        this.estadoId = estadoId;
    }
}
