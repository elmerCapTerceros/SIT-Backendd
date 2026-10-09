package com.gobernacionSIT.sit_backend.service;

import com.gobernacionSIT.sit_backend.entity.Rol;
import com.gobernacionSIT.sit_backend.entity.AsignacionTecnico;
import com.gobernacionSIT.sit_backend.entity.EstadoSolicitud;
import com.gobernacionSIT.sit_backend.entity.Solicitud;
import com.gobernacionSIT.sit_backend.entity.Usuario;
import com.gobernacionSIT.sit_backend.exception.BusinessException;
import com.gobernacionSIT.sit_backend.repository.AsignacionTecnicoRepository;
import com.gobernacionSIT.sit_backend.repository.EstadoSolicitudRepository;
import com.gobernacionSIT.sit_backend.repository.SolicitudRepository;
import com.gobernacionSIT.sit_backend.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        private final EstadoSolicitudRepository estadoSolicitudRepository = mock(EstadoSolicitudRepository.class);
    private final AsignacionServiceImpl service = new AsignacionServiceImpl(
            asignacionRepository, solicitudRepository, usuarioRepository, estadoSolicitudRepository);

    @Test
    void noAsignaSolicitudesAUsuariosConOtroRol() {
        when(solicitudRepository.findById(5L)).thenReturn(Optional.of(new Solicitud()));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuario(2L, "FUNCIONARIO")));

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
    void supervisorPuedeConsultarSolicitudesDeCualquierTecnico() {
        when(usuarioRepository.findByUserLogin("supervisor")).thenReturn(Optional.of(usuario(3L, "SUPERVISOR")));
        when(asignacionRepository.findByTecnicoId(1L)).thenReturn(List.of());

        assertTrue(service.obtenerSolicitudesAsignadas(1L, "supervisor").isEmpty());

        verify(asignacionRepository).findByTecnicoId(1L);
    }

    @Test
    void respuestaIncluyeDetalleDelProblemaYDatosDelSolicitante() {
        Usuario solicitante = usuario(4L, "FUNCIONARIO");
        solicitante.setNombre("Ana");
        solicitante.setApellido("Pérez");
        solicitante.setCargo("Analista");
        solicitante.setTelefono("76543210");
        solicitante.setArea("Sistemas");
        solicitante.setUbicacionOficina("Edificio central");

        EstadoSolicitud estado = new EstadoSolicitud();
        estado.setEstadoSolicitud("PENDIENTE");
        Solicitud solicitud = new Solicitud();
        solicitud.setId(12L);
        solicitud.setCodigo("SOL-0012");
        solicitud.setTitulo("Equipo sin conexión");
        solicitud.setCategoria("Redes");
        solicitud.setDescripcion("No conecta a la red institucional");
        solicitud.setUbicacion("Oficina 12");
        solicitud.setEquipoDanado("Computadora");
        solicitud.setEstado(estado);
        solicitud.setSolicitante(solicitante);

        AsignacionTecnico asignacion = new AsignacionTecnico();
        asignacion.setSolicitud(solicitud);
        asignacion.setTecnico(usuario(1L, "TECNICO"));
        when(usuarioRepository.findByUserLogin("tecnico-1")).thenReturn(Optional.of(usuario(1L, "TECNICO")));
        when(asignacionRepository.findByTecnicoId(1L)).thenReturn(List.of(asignacion));

        var respuesta = service.obtenerSolicitudesAsignadas(1L, "tecnico-1").getFirst();

        assertEquals("Oficina 12", respuesta.ubicacion());
        assertEquals("Computadora", respuesta.equipoDanado());
        assertEquals("Ana Pérez", respuesta.solicitanteNombre());
        assertEquals("Analista", respuesta.solicitanteCargo());
        assertEquals("76543210", respuesta.solicitanteTelefono());
        assertEquals("Sistemas", respuesta.solicitanteArea());
        assertEquals("Edificio central", respuesta.solicitanteUbicacionOficina());
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
