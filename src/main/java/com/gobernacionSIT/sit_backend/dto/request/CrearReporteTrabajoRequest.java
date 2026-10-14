package com.gobernacionSIT.sit_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CrearReporteTrabajoRequest {

    @NotNull(message = "La asignación del técnico es obligatoria")
    private Long asignacionTecnicoId;

    @Size(max = 150, message = "El tipo de equipo no puede exceder 150 caracteres")
    private String tipoEquipo;

    @NotBlank(message = "El diagnóstico es obligatorio")
    private String diagnostico;

    @NotBlank(message = "El trabajo realizado es obligatorio")
    private String trabajoRealizado;

    @NotBlank(message = "El estado final es obligatorio")
    @Pattern(
            regexp = "SOLUCIONADO|PARCIAL|PENDIENTE|REQUIERE_REPUESTO|SERVICIO_EXTERNO",
            message = "El estado final no es válido"
    )
    private String estadoFinal;

    private String observacion;

    public Long getAsignacionTecnicoId() {
        return asignacionTecnicoId;
    }

    public void setAsignacionTecnicoId(Long asignacionTecnicoId) {
        this.asignacionTecnicoId = asignacionTecnicoId;
    }

    public String getTipoEquipo() {
        return tipoEquipo;
    }

    public void setTipoEquipo(String tipoEquipo) {
        this.tipoEquipo = tipoEquipo;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public String getTrabajoRealizado() {
        return trabajoRealizado;
    }

    public void setTrabajoRealizado(String trabajoRealizado) {
        this.trabajoRealizado = trabajoRealizado;
    }

    public String getEstadoFinal() {
        return estadoFinal;
    }

    public void setEstadoFinal(String estadoFinal) {
        this.estadoFinal = estadoFinal;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
}
