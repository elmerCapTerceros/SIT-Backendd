package com.gobernacionSIT.sit_backend.service;

import com.gobernacionSIT.sit_backend.dto.response.SolicitudResponse;
import com.gobernacionSIT.sit_backend.entity.AsignacionTecnico;
import com.gobernacionSIT.sit_backend.entity.Solicitud;
import com.gobernacionSIT.sit_backend.entity.Usuario;
import com.gobernacionSIT.sit_backend.exception.BusinessException;
import com.gobernacionSIT.sit_backend.repository.AsignacionTecnicoRepository;
import com.gobernacionSIT.sit_backend.repository.EstadoSolicitudRepository;
import com.gobernacionSIT.sit_backend.repository.SolicitudRepository;
import com.gobernacionSIT.sit_backend.repository.UsuarioRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AsignacionServiceImpl implements AsignacionService {

    private final AsignacionTecnicoRepository asignacionTecnicoRepository;
    private final SolicitudRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;
    private final EstadoSolicitudRepository estadoSolicitudRepository;

    public AsignacionServiceImpl(AsignacionTecnicoRepository asignacionTecnicoRepository,
                                 SolicitudRepository solicitudRepository,
                                 UsuarioRepository usuarioRepository,
                                 EstadoSolicitudRepository estadoSolicitudRepository) {
        this.asignacionTecnicoRepository = asignacionTecnicoRepository;
        this.solicitudRepository = solicitudRepository;
        this.usuarioRepository = usuarioRepository;
        this.estadoSolicitudRepository = estadoSolicitudRepository;
    }

    @Override
    public void asignar(Long solicitudId, Long tecnicoId) {
        if (asignacionTecnicoRepository.existsBySolicitudIdAndTecnicoId(solicitudId, tecnicoId)) {
            throw new IllegalStateException("El técnico ya está asignado a esta solicitud");
        }
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
            .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));
        Usuario tecnico = usuarioRepository.findById(tecnicoId)
            .orElseThrow(() -> new IllegalArgumentException("Técnico no encontrado"));
        validarRolTecnico(tecnico);
        AsignacionTecnico asignacion = new AsignacionTecnico();
        asignacion.setSolicitud(solicitud);
        asignacion.setTecnico(tecnico);
        asignacionTecnicoRepository.save(asignacion);
    }

    private void validarRolTecnico(Usuario usuario) {
        if (!"TECNICO".equals(usuario.getRol().getNombreRol())) {
            throw new BusinessException("Las solicitudes solo se pueden asignar a usuarios con rol TECNICO");
        }
    }

    @Override
    public List<Long> obtenerTecnicosAsignados(Long solicitudId) {
        return asignacionTecnicoRepository.findBySolicitud_Id(solicitudId)
                .stream()
                .map(asignacion -> asignacion.getTecnico().getId())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SolicitudResponse> obtenerSolicitudesAsignadas(Long tecnicoId, String userLogin) {
        validarTecnicoSolicitudes(tecnicoId, userLogin);
        return asignacionTecnicoRepository.findByTecnicoId(tecnicoId)
                .stream()
                .map(this::toSolicitudResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SolicitudResponse> obtenerSolicitudesAsignadasPorEstado(
            Long tecnicoId, String estadoSolicitud, String userLogin) {
        validarTecnicoSolicitudes(tecnicoId, userLogin);
        return asignacionTecnicoRepository
                .findByTecnico_IdAndSolicitud_Estado_EstadoSolicitud(tecnicoId, estadoSolicitud)
                .stream()
                .map(this::toSolicitudResponse)
                .toList();
    }

    /**
     * El técnico lee el detalle, acepta, y la solicitud pasa a EN_PROCESO.
     */
    @Override
    @Transactional
    public SolicitudResponse aceptarSolicitud(Long solicitudId, String userLogin) {
        Usuario usuario = usuarioRepository.findByUserLogin(userLogin)
                .orElseThrow(() -> new AccessDeniedException("Usuario autenticado no encontrado"));

        AsignacionTecnico asignacion = asignacionTecnicoRepository.findBySolicitud_Id(solicitudId)
                .stream()
                .filter(a -> a.getTecnico().getId().equals(usuario.getId()))
                .findFirst()
                .orElseThrow(() -> new AccessDeniedException("Esta solicitud no está asignada a usted"));

        Solicitud solicitud = asignacion.getSolicitud();
        String actual = solicitud.getEstado().getEstadoSolicitud();
        if (!List.of("ASIGNADA", "PENDIENTE").contains(actual)) {
            throw new BusinessException("La solicitud ya fue aceptada o no admite este cambio");
        }

        solicitud.setEstado(estadoSolicitudRepository.findByEstadoSolicitud("EN_PROCESO")
                .orElseThrow(() -> new IllegalStateException("Estado no configurado: EN_PROCESO")));
        solicitudRepository.save(solicitud);

        return toSolicitudResponse(asignacion);
    }

    private void validarTecnicoSolicitudes(Long tecnicoId, String userLogin) {
        Usuario usuario = usuarioRepository.findByUserLogin(userLogin)
                .orElseThrow(() -> new AccessDeniedException("Usuario autenticado no encontrado"));
        if ("TECNICO".equals(usuario.getRol().getNombreRol())
                && !usuario.getId().equals(tecnicoId)) {
            throw new AccessDeniedException("No puede consultar solicitudes asignadas a otro técnico");
        }
    }

    private SolicitudResponse toSolicitudResponse(AsignacionTecnico asignacion) {
        Solicitud solicitud = asignacion.getSolicitud();
        return new SolicitudResponse(
                solicitud.getId(), solicitud.getCodigo(), solicitud.getTitulo(), solicitud.getArea(),
                solicitud.getCategoria(), solicitud.getDescripcion(), solicitud.getPrioridad(),
                solicitud.getEstado().getEstadoSolicitud(), solicitud.getSolicitante().getId(),
                nombreCompleto(solicitud.getSolicitante()), asignacion.getTecnico().getId(),
                solicitud.getVerificadoPorUsuario(),
                solicitud.getCreatedAt(), solicitud.getUpdatedAt(), solicitud.getUbicacion(),
                solicitud.getEquipoDanado(), solicitud.getSolicitante().getCargo(),
                solicitud.getSolicitante().getTelefono(), solicitud.getSolicitante().getArea(),
                solicitud.getSolicitante().getUbicacionOficina()
        );
    }

    @Override
    public List<Long> listarTecnicos() {
        return usuarioRepository.findByRol_NombreRol("TECNICO").stream()
            .map(usuario -> usuario.getId())
                .toList();
    }

    private String nombreCompleto(Usuario usuario) {
        String nombre = usuario.getNombre();
        String apellido = usuario.getApellido();
        if (nombre == null || nombre.isBlank()) return apellido;
        if (apellido == null || apellido.isBlank()) return nombre;
        return nombre + " " + apellido;
    }

}