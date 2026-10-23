package com.gobernacionSIT.sit_backend.service;

import com.gobernacionSIT.sit_backend.dto.request.AsignarSolicitudRequest;
import com.gobernacionSIT.sit_backend.dto.request.CrearSolicitudRequest;
import com.gobernacionSIT.sit_backend.entity.AsignacionTecnico;
import com.gobernacionSIT.sit_backend.entity.EstadoSolicitud;
import com.gobernacionSIT.sit_backend.entity.Rol;
import com.gobernacionSIT.sit_backend.entity.Solicitud;
import com.gobernacionSIT.sit_backend.entity.Usuario;
import com.gobernacionSIT.sit_backend.exception.BusinessException;
import com.gobernacionSIT.sit_backend.repository.AsignacionTecnicoRepository;
import com.gobernacionSIT.sit_backend.repository.EstadoSolicitudRepository;
import com.gobernacionSIT.sit_backend.repository.SolicitudRepository;
import com.gobernacionSIT.sit_backend.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    void noPermiteReasignarUnaSolicitudQueYaEstaEnProceso() {
        EstadoSolicitud estado = new EstadoSolicitud();
        estado.setEstadoSolicitud("EN_PROCESO");
        Solicitud solicitud = new Solicitud();
        solicitud.setEstado(estado);
        AsignarSolicitudRequest request = new AsignarSolicitudRequest();
        request.setTecnicoId(2L);
        request.setPrioridad("MEDIA");
        when(solicitudRepository.findById(5L)).thenReturn(Optional.of(solicitud));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuario(2L, "TECNICO")));

        assertThrows(BusinessException.class, () -> service.asignar(5L, request));
        verify(asignacionRepository, never()).save(any());
    }

    @Test
    void reasignaSolicitudRechazadaCreandoUnaNuevaAsignacion() {
        Usuario solicitante = usuario(1L, "FUNCIONARIO");
        Solicitud solicitud = solicitud(solicitante, "RECHAZADA");
        solicitud.setMotivoRechazo("No corresponde a mi especialidad");
        Usuario tecnicoAnterior = usuario(3L, "TECNICO");
        AsignacionTecnico asignacionAnterior = new AsignacionTecnico();
        asignacionAnterior.setSolicitud(solicitud);
        asignacionAnterior.setTecnico(tecnicoAnterior);
        EstadoSolicitud pendiente = new EstadoSolicitud();
        pendiente.setEstadoSolicitud("PENDIENTE");
        AsignarSolicitudRequest request = new AsignarSolicitudRequest();
        request.setTecnicoId(2L);
        request.setPrioridad("ALTA");
        when(solicitudRepository.findById(5L)).thenReturn(Optional.of(solicitud));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuario(2L, "TECNICO")));
        when(asignacionRepository.findBySolicitud_IdOrderByAsignadoAtDesc(5L))
                .thenReturn(java.util.List.of(asignacionAnterior));
        when(estadoRepository.findByEstadoSolicitud("PENDIENTE")).thenReturn(Optional.of(pendiente));
        when(asignacionRepository.save(any(AsignacionTecnico.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(solicitudRepository.save(any(Solicitud.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.asignar(5L, request);

        assertEquals("PENDIENTE", solicitud.getEstado().getEstadoSolicitud());
        assertNull(solicitud.getMotivoRechazo());
        assertEquals(2L, response.tecnicoId());
        verify(asignacionRepository).save(org.mockito.ArgumentMatchers.argThat(asignacion ->
                asignacion != asignacionAnterior
                        && asignacion.getSolicitud() == solicitud
                        && asignacion.getTecnico().getId().equals(2L)));
    }

    @Test
    void tecnicoNoPuedeAtenderSolicitudNoAsignada() {
        when(usuarioRepository.findByUserLogin("tecnico")).thenReturn(Optional.of(usuario(2L, "TECNICO")));
        when(asignacionRepository.findBySolicitud_IdOrderByAsignadoAtDesc(5L)).thenReturn(java.util.List.of());

        assertThrows(BusinessException.class, () -> service.atender(5L, "tecnico"));
        verify(solicitudRepository, never()).save(any(Solicitud.class));
    }

    @Test
    void tecnicoAnteriorNoPuedeAtenderSolicitudReasignada() {
        Usuario solicitante = usuario(3L, "FUNCIONARIO");
        Usuario tecnicoAnterior = usuario(1L, "TECNICO");
        Usuario tecnicoActual = usuario(2L, "TECNICO");
        Solicitud solicitud = solicitud(solicitante, "PENDIENTE");
        AsignacionTecnico asignacionActual = asignacion(solicitud, tecnicoActual);
        AsignacionTecnico asignacionAnterior = asignacion(solicitud, tecnicoAnterior);
        EstadoSolicitud estadoEnProceso = new EstadoSolicitud();
        estadoEnProceso.setEstadoSolicitud("EN_PROCESO");

        when(usuarioRepository.findByUserLogin("tecnico-anterior")).thenReturn(Optional.of(tecnicoAnterior));
        when(asignacionRepository.findBySolicitud_IdOrderByAsignadoAtDesc(5L))
                .thenReturn(java.util.List.of(asignacionActual, asignacionAnterior));
        when(estadoRepository.findByEstadoSolicitud("EN_PROCESO")).thenReturn(Optional.of(estadoEnProceso));
        when(solicitudRepository.save(any(Solicitud.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertThrows(BusinessException.class, () -> service.atender(5L, "tecnico-anterior"));

        verify(solicitudRepository, never()).save(any(Solicitud.class));
    }

    @Test
    void tecnicoActualPuedeAtenderSolicitudReasignada() {
        Usuario solicitante = usuario(3L, "FUNCIONARIO");
        Usuario tecnicoAnterior = usuario(1L, "TECNICO");
        Usuario tecnicoActual = usuario(2L, "TECNICO");
        Solicitud solicitud = solicitud(solicitante, "PENDIENTE");
        AsignacionTecnico asignacionActual = asignacion(solicitud, tecnicoActual);
        AsignacionTecnico asignacionAnterior = asignacion(solicitud, tecnicoAnterior);
        EstadoSolicitud estadoEnProceso = new EstadoSolicitud();
        estadoEnProceso.setEstadoSolicitud("EN_PROCESO");

        when(usuarioRepository.findByUserLogin("tecnico-actual")).thenReturn(Optional.of(tecnicoActual));
        when(asignacionRepository.findBySolicitud_IdOrderByAsignadoAtDesc(5L))
                .thenReturn(java.util.List.of(asignacionActual, asignacionAnterior));
        when(estadoRepository.findByEstadoSolicitud("EN_PROCESO")).thenReturn(Optional.of(estadoEnProceso));
        when(solicitudRepository.save(any(Solicitud.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.atender(5L, "tecnico-actual");

        assertEquals("EN_PROCESO", response.estado());
        verify(solicitudRepository).save(solicitud);
    }

    @Test
    void solicitantePuedeVerificarSuSolicitudConcluida() {
        Usuario solicitante = usuario(1L, "FUNCIONARIO");
        Solicitud solicitud = solicitud(solicitante, "CONCLUIDA");
        when(usuarioRepository.findByUserLogin("funcionario")).thenReturn(Optional.of(solicitante));
        when(solicitudRepository.findById(5L)).thenReturn(Optional.of(solicitud));
        when(solicitudRepository.save(any(Solicitud.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(asignacionRepository.findBySolicitud_IdOrderByAsignadoAtDesc(5L)).thenReturn(java.util.List.of());

        var response = service.verificar(5L, "funcionario");

        assertEquals(true, response.verificadoPorUsuario());
        verify(solicitudRepository).save(solicitud);
    }

    @Test
    void funcionarioNoPuedeVerificarSolicitudDeOtraPersona() {
        Usuario solicitante = usuario(1L, "FUNCIONARIO");
        when(usuarioRepository.findByUserLogin("funcionario")).thenReturn(Optional.of(solicitante));
        when(solicitudRepository.findById(5L))
                .thenReturn(Optional.of(solicitud(usuario(2L, "FUNCIONARIO"), "CONCLUIDA")));

        assertThrows(AccessDeniedException.class, () -> service.verificar(5L, "funcionario"));
        verify(solicitudRepository, never()).save(any(Solicitud.class));
    }

    @Test
    void noPermiteVerificarSolicitudAunNoConcluida() {
        Usuario solicitante = usuario(1L, "FUNCIONARIO");
        when(usuarioRepository.findByUserLogin("funcionario")).thenReturn(Optional.of(solicitante));
        when(solicitudRepository.findById(5L))
                .thenReturn(Optional.of(solicitud(solicitante, "EN_PROCESO")));

        assertThrows(BusinessException.class, () -> service.verificar(5L, "funcionario"));
        verify(solicitudRepository, never()).save(any(Solicitud.class));
    }

    private Solicitud solicitud(Usuario solicitante, String estadoNombre) {
        EstadoSolicitud estado = new EstadoSolicitud();
        estado.setEstadoSolicitud(estadoNombre);
        Solicitud solicitud = new Solicitud();
        solicitud.setId(5L);
        solicitud.setEstado(estado);
        solicitud.setSolicitante(solicitante);
        return solicitud;
    }

    private AsignacionTecnico asignacion(Solicitud solicitud, Usuario tecnico) {
        AsignacionTecnico asignacion = new AsignacionTecnico();
        asignacion.setSolicitud(solicitud);
        asignacion.setTecnico(tecnico);
        return asignacion;
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
