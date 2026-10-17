package com.gobernacionSIT.sit_backend.service;

import com.gobernacionSIT.sit_backend.entity.AsignacionTecnico;
import com.gobernacionSIT.sit_backend.entity.EstadoSolicitud;
import com.gobernacionSIT.sit_backend.entity.Solicitud;
import com.gobernacionSIT.sit_backend.entity.Usuario;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SolicitudResponseMapperTest {

    @Test
    void mapsRequestAndAssignmentFieldsFromSingleSource() {
        Usuario solicitante = new Usuario();
        solicitante.setId(1L);
        solicitante.setNombre("Ana");
        solicitante.setApellido("Pérez");
        solicitante.setCargo("Analista");
        solicitante.setTelefono("12345678");

        Usuario tecnico = new Usuario();
        tecnico.setId(2L);

        EstadoSolicitud estado = new EstadoSolicitud();
        estado.setEstadoSolicitud("RECHAZADA");

        Solicitud solicitud = new Solicitud();
        solicitud.setId(3L);
        solicitud.setEstado(estado);
        solicitud.setSolicitante(solicitante);
        solicitud.setMotivoRechazo("El equipo no corresponde al inventario");

        AsignacionTecnico asignacion = new AsignacionTecnico();
        asignacion.setId(4L);
        asignacion.setTecnico(tecnico);

        var response = SolicitudResponseMapper.toResponse(solicitud, asignacion);

        assertEquals(2L, response.tecnicoId());
        assertEquals(4L, response.asignacionTecnicoId());
        assertEquals("Ana Pérez", response.solicitanteNombre());
        assertEquals("Analista", response.solicitanteCargo());
        assertEquals("12345678", response.solicitanteTelefono());
        assertEquals(solicitud.getMotivoRechazo(), response.motivoRechazo());
    }

    @Test
    void mapsUnassignedRequestWithoutAssignmentIds() {
        Usuario solicitante = new Usuario();
        solicitante.setId(1L);
        EstadoSolicitud estado = new EstadoSolicitud();
        estado.setEstadoSolicitud("PENDIENTE");
        Solicitud solicitud = new Solicitud();
        solicitud.setEstado(estado);
        solicitud.setSolicitante(solicitante);

        var response = SolicitudResponseMapper.toResponse(solicitud, null);

        assertNull(response.tecnicoId());
        assertNull(response.asignacionTecnicoId());
    }
}
