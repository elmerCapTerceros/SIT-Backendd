package com.gobernacionSIT.sit_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

public class CrearReporteTrabajoRequest {

    private Long asignacionTecnicoId;

    private Long solicitudId;

    @Size(max = 50, message = "El número de formulario no puede exceder 50 caracteres")
    private String numeroFormulario;

    private LocalDate fechaAtencion;

    private LocalTime horaAtencion;

    @Size(max = 150, message = "La unidad no puede exceder 150 caracteres")
    private String unidad;

    @Size(max = 200, message = "El solicitante no puede exceder 200 caracteres")
    private String solicitante;

    @Size(max = 100, message = "El número de inventario no puede exceder 100 caracteres")
    private String numeroInventario;

    @Size(max = 150, message = "El tipo de equipo no puede exceder 150 caracteres")
    private String tipoEquipo;

    @Size(max = 150, message = "El tipo de equipo no puede exceder 150 caracteres")
    private String tipoEquipoOtro;

    private String problema;

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

    @Size(max = 200, message = "El nombre del técnico no puede exceder 200 caracteres")
    private String tecnicoNombre;

    @Size(max = 200, message = "El nombre del funcionario no puede exceder 200 caracteres")
    private String funcionarioNombre;

    public Long getAsignacionTecnicoId() {
        return asignacionTecnicoId;
    }

    public void setAsignacionTecnicoId(Long asignacionTecnicoId) {
        this.asignacionTecnicoId = asignacionTecnicoId;
    }

    public Long getSolicitudId() {
        return solicitudId;
    }

    public void setSolicitudId(Long solicitudId) {
        this.solicitudId = solicitudId;
    }

    public String getNumeroFormulario() {
        return numeroFormulario;
    }

    public void setNumeroFormulario(String numeroFormulario) {
        this.numeroFormulario = numeroFormulario;
    }

    public LocalDate getFechaAtencion() {
        return fechaAtencion;
    }

    public void setFechaAtencion(LocalDate fechaAtencion) {
        this.fechaAtencion = fechaAtencion;
    }

    public LocalTime getHoraAtencion() {
        return horaAtencion;
    }

    public void setHoraAtencion(LocalTime horaAtencion) {
        this.horaAtencion = horaAtencion;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public String getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(String solicitante) {
        this.solicitante = solicitante;
    }

    public String getNumeroInventario() {
        return numeroInventario;
    }

    public void setNumeroInventario(String numeroInventario) {
        this.numeroInventario = numeroInventario;
    }

    public String getTipoEquipo() {
        return tipoEquipo;
    }

    public void setTipoEquipo(String tipoEquipo) {
        this.tipoEquipo = tipoEquipo;
    }

    public String getTipoEquipoOtro() {
        return tipoEquipoOtro;
    }

    public void setTipoEquipoOtro(String tipoEquipoOtro) {
        this.tipoEquipoOtro = tipoEquipoOtro;
    }

    public String getProblema() {
        return problema;
    }

    public void setProblema(String problema) {
        this.problema = problema;
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

    public String getTecnicoNombre() {
        return tecnicoNombre;
    }

    public void setTecnicoNombre(String tecnicoNombre) {
        this.tecnicoNombre = tecnicoNombre;
    }

    public String getFuncionarioNombre() {
        return funcionarioNombre;
    }

    public void setFuncionarioNombre(String funcionarioNombre) {
        this.funcionarioNombre = funcionarioNombre;
    }
}
