package com.gobernacionSIT.sit_backend.service;

import com.gobernacionSIT.sit_backend.entity.Rol;
import com.gobernacionSIT.sit_backend.entity.AsignacionTecnico;
import com.gobernacionSIT.sit_backend.entity.Solicitud;
import com.gobernacionSIT.sit_backend.entity.Usuario;
import com.gobernacionSIT.sit_backend.exception.BusinessException;
import com.gobernacionSIT.sit_backend.repository.AsignacionTecnicoRepository;
import com.gobernacionSIT.sit_backend.repository.SolicitudRepository;
import com.gobernacionSIT.sit_backend.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AsignacionServiceImplTest {

    private final AsignacionTecnicoRepository asignacionRepository = mock(AsignacionTecnicoRepository.class);
    private final SolicitudRepository solicitudRepository = mock(SolicitudRepository.class);
    private final UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
    private final AsignacionServiceImpl service = new AsignacionServiceImpl(
            asignacionRepository, solicitudRepository, usuarioRepository);

    @Test
    void noAsignaSolicitudesAUsuariosConOtroRol() {
        when(solicitudRepository.findById(5L)).thenReturn(Optional.of(new Solicitud()));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuario(2L, "FUNCIONARIO")));

        assertThrows(BusinessException.class, () -> service.asignar(5L, 2L));

        verify(asignacionRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void noPermiteCrearUnaSegundaAsignacionParaLaMismaSolicitud() {
        Solicitud solicitud = new Solicitud();
        AsignacionTecnico existente = new AsignacionTecnico();
        existente.setTecnico(usuario(4L, "TECNICO"));
        when(solicitudRepository.findById(5L)).thenReturn(Optional.of(solicitud));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuario(2L, "TECNICO")));
        when(asignacionRepository.findBySolicitud_IdOrderByAsignadoAtDesc(5L))
                .thenReturn(List.of(existente));

        assertThrows(BusinessException.class, () -> service.asignar(5L, 2L));

        verify(asignacionRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void tecnicoNoPuedeConsultarSolicitudesDeOtroTecnico() {
        when(usuarioRepository.findByUserLogin("tecnico-1")).thenReturn(Optional.of(usuario(1L, "TECNICO")));

        assertThrows(AccessDeniedException.class,
                () -> service.obtenerSolicitudesAsignadas(2L, "tecnico-1"));
        assertThrows(AccessDeniedException.class,
                () -> service.obtenerSolicitudesAsignadasPorEstado(2L, "PENDIENTE", "tecnico-1"));

        verify(asignacionRepository, never()).findByTecnicoId(2L);
        verify(asignacionRepository, never())
                .findByTecnico_IdAndSolicitud_Estado_EstadoSolicitud(2L, "PENDIENTE");
    }

    @Test
    void tecnicoPuedeConsultarSusPropiasSolicitudes() {
        when(usuarioRepository.findByUserLogin("tecnico-1")).thenReturn(Optional.of(usuario(1L, "TECNICO")));
        when(asignacionRepository.findByTecnicoId(1L)).thenReturn(List.of());

        assertTrue(service.obtenerSolicitudesAsignadas(1L, "tecnico-1").isEmpty());

        verify(asignacionRepository).findByTecnicoId(1L);
    }

    @Test
    void tecnicoNoVeAsignacionesHistoricasDeUnaSolicitudReasignada() {
        Usuario tecnicoAnterior = usuario(1L, "TECNICO");
        Usuario tecnicoActual = usuario(2L, "TECNICO");
        Solicitud solicitud = new Solicitud();
        solicitud.setId(5L);
        AsignacionTecnico anterior = new AsignacionTecnico();
        anterior.setSolicitud(solicitud);
        anterior.setTecnico(tecnicoAnterior);
        AsignacionTecnico actual = new AsignacionTecnico();
        actual.setSolicitud(solicitud);
        actual.setTecnico(tecnicoActual);
        when(usuarioRepository.findByUserLogin("tecnico-1")).thenReturn(Optional.of(tecnicoAnterior));
        when(asignacionRepository.findByTecnicoId(1L)).thenReturn(List.of(anterior));
        when(asignacionRepository.findBySolicitud_IdInOrderByAsignadoAtDesc(List.of(5L)))
                .thenReturn(List.of(actual, anterior));

        assertTrue(service.obtenerSolicitudesAsignadas(1L, "tecnico-1").isEmpty());
    }

    @Test
    void supervisorPuedeConsultarSolicitudesDeCualquierTecnico() {
        when(usuarioRepository.findByUserLogin("supervisor")).thenReturn(Optional.of(usuario(3L, "SUPERVISOR")));
        when(asignacionRepository.findByTecnicoId(1L)).thenReturn(List.of());

        assertTrue(service.obtenerSolicitudesAsignadas(1L, "supervisor").isEmpty());

        verify(asignacionRepository).findByTecnicoId(1L);
    }

    @Test
    void tecnicoPuedeRechazarSuPropiaAsignacion() {
        Usuario tecnico = usuario(1L, "TECNICO");
        AsignacionTecnico asignacion = new AsignacionTecnico();
        asignacion.setTecnico(tecnico);
        when(usuarioRepository.findByUserLogin("tecnico-1")).thenReturn(Optional.of(tecnico));
        when(asignacionRepository.findById(10L)).thenReturn(Optional.of(asignacion));

        service.rechazarAsignacionTwo(10L, "tecnico-1");

        verify(asignacionRepository).delete(asignacion);
    }

    @Test
    void tecnicoNoPuedeRechazarAsignacionDeOtroTecnico() {
        Usuario otroTecnico = usuario(2L, "TECNICO");
        AsignacionTecnico asignacion = new AsignacionTecnico();
        asignacion.setTecnico(otroTecnico);
        when(usuarioRepository.findByUserLogin("tecnico-1"))
                .thenReturn(Optional.of(usuario(1L, "TECNICO")));
        when(asignacionRepository.findById(10L)).thenReturn(Optional.of(asignacion));

        assertThrows(AccessDeniedException.class,
                () -> service.rechazarAsignacionTwo(10L, "tecnico-1"));

        verify(asignacionRepository, never()).delete(asignacion);
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
