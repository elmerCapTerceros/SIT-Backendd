package com.gobernacionSIT.sit_backend.service;

import com.gobernacionSIT.sit_backend.dto.request.AsignarSolicitudRequest;
import com.gobernacionSIT.sit_backend.dto.request.CrearSolicitudRequest;
import com.gobernacionSIT.sit_backend.entity.EstadoSolicitud;
import com.gobernacionSIT.sit_backend.entity.Rol;
import com.gobernacionSIT.sit_backend.entity.Solicitud;
import com.gobernacionSIT.sit_backend.entity.Usuario;
import com.gobernacionSIT.sit_backend.exception.BusinessException;
import com.gobernacionSIT.sit_backend.exception.ResourceNotFoundException;
import com.gobernacionSIT.sit_backend.repository.AsignacionTecnicoRepository;
import com.gobernacionSIT.sit_backend.repository.EstadoSolicitudRepository;
import com.gobernacionSIT.sit_backend.repository.SolicitudRepository;
import com.gobernacionSIT.sit_backend.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SolicitudServiceImplTest {

    private final SolicitudRepository solicitudRepository = mock(SolicitudRepository.class);
    private final AsignacionTecnicoRepository asignacionRepository = mock(AsignacionTecnicoRepository.class);
    private final UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
    private final EstadoSolicitudRepository estadoRepository = mock(EstadoSolicitudRepository.class);
    private final SolicitudServiceImpl service = new SolicitudServiceImpl(
            solicitudRepository,
            usuarioRepository,
            estadoRepository,
            asignacionRepository);

    @Test
    void creaSolicitudConUbicacionDeOficinaDelSolicitante() {
        Usuario solicitante = usuario(1L, "FUNCIONARIO");
        solicitante.setUbicacionOficina("Edificio Central");
        EstadoSolicitud estado = new EstadoSolicitud();
        estado.setEstadoSolicitud("PENDIENTE");
        CrearSolicitudRequest request = new CrearSolicitudRequest();
        request.setTitulo("Reparar computadora");
        request.setTipo("PC");
        when(usuarioRepository.findByUserLogin("funcionario")).thenReturn(Optional.of(solicitante));
        when(solicitudRepository.siguienteNumeroCodigo()).thenReturn(1L);
        when(estadoRepository.findByEstadoSolicitud("PENDIENTE")).thenReturn(Optional.of(estado));
        when(solicitudRepository.save(any(Solicitud.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.crear("funcionario", request);

        assertEquals("Edificio Central", response.ubicacion());
        verify(solicitudRepository).save(org.mockito.ArgumentMatchers.argThat(
                solicitud -> "Edificio Central".equals(solicitud.getUbicacion())));
    }

    @Test
    void rechazaAsignacionCuandoUsuarioNoEsTecnico() {
        AsignarSolicitudRequest request = new AsignarSolicitudRequest();
        request.setTecnicoId(2L);
        request.setPrioridad("MEDIA");
        when(solicitudRepository.findById(5L)).thenReturn(Optional.of(new Solicitud()));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuario(2L, "FUNCIONARIO")));

        assertThrows(BusinessException.class, () -> service.asignar(5L, request));

        verify(solicitudRepository, never()).save(any(Solicitud.class));
        verify(asignacionRepository, never()).save(any());
    }

    @Test
    void actualizaUnicamenteEstadoDeSolicitud() {
        Solicitud solicitud = new Solicitud();
        EstadoSolicitud estado = new EstadoSolicitud();
        estado.setId(3L);
        estado.setEstadoSolicitud("CONCLUIDA");
        when(solicitudRepository.findById(5L)).thenReturn(Optional.of(solicitud));
        when(estadoRepository.findById(3L)).thenReturn(Optional.of(estado));

        service.actualizarEstadoSolicitud(5L, 3L);

        assertSame(estado, solicitud.getEstado());
        verify(solicitudRepository, never()).save(any(Solicitud.class));
    }

    @Test
    void informaSiLaSolicitudNoExisteAlActualizarEstado() {
        when(solicitudRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.actualizarEstadoSolicitud(5L, 3L));
    }

    private Usuario usuario(Long id, String nombreRol) {
        Rol rol = new Rol();
        rol.setNombreRol(nombreRol);
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setRol(rol);
        return usuario;
    }
}
