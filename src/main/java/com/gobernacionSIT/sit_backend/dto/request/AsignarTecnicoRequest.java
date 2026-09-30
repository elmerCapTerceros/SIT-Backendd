package com.gobernacionSIT.sit_backend.dto.request;

import jakarta.validation.constraints.NotNull;

public class AsignarTecnicoRequest {

    @NotNull(message = "La solicitud es obligatoria")
    private Long solicitudId;


    @NotNull(message = "El técnico es obligatorio")
    private Long tecnicoId;

    public Long getSolicitudId() {
        return solicitudId;
    }

    public void setSolicitudId(Long solicitudId) {
        this.solicitudId = solicitudId;
    }

    public Long getTecnicoId() {
        return tecnicoId;
    }

    public void setTecnicoId(Long tecnicoId) {
        this.tecnicoId = tecnicoId;
    }
}
