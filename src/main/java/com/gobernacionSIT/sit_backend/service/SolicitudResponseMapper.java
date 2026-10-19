package com.gobernacionSIT.sit_backend.service;

import com.gobernacionSIT.sit_backend.dto.response.SolicitudResponse;
import com.gobernacionSIT.sit_backend.entity.AsignacionTecnico;
import com.gobernacionSIT.sit_backend.entity.Solicitud;
import com.gobernacionSIT.sit_backend.entity.Usuario;

final class SolicitudResponseMapper {

    private SolicitudResponseMapper() {
    }

    static SolicitudResponse toResponse(Solicitud solicitud, AsignacionTecnico asignacion) {
        Usuario solicitante = solicitud.getSolicitante();
        return new SolicitudResponse(
                solicitud.getId(),
                solicitud.getCodigo(),
                solicitud.getTitulo(),
                solicitud.getArea(),
                solicitud.getUbicacion(),
                solicitud.getCategoria(),
                solicitud.getDescripcion(),
                solicitud.getPrioridad(),
                solicitud.getEstado().getEstadoSolicitud(),
                solicitante.getId(),
                nombreCompleto(solicitante),
                asignacion == null ? null : asignacion.getTecnico().getId(),
                asignacion == null ? null : asignacion.getId(),
                solicitud.getVerificadoPorUsuario(),
                solicitud.getCreatedAt(),
                solicitud.getUpdatedAt(),
                solicitante.getCargo(),
                solicitante.getTelefono(),
                solicitud.getMotivoRechazo()
        );
    }

    private static String nombreCompleto(Usuario usuario) {
        String nombre = usuario.getNombre();
        String apellido = usuario.getApellido();
        if (nombre == null || nombre.isBlank()) return apellido;
        if (apellido == null || apellido.isBlank()) return nombre;
        return nombre + " " + apellido;
    }
}
