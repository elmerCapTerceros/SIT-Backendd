package com.gobernacionSIT.sit_backend.service;

import com.gobernacionSIT.sit_backend.dto.request.AsignarSolicitudRequest;
import com.gobernacionSIT.sit_backend.dto.request.CrearSolicitudRequest;
import com.gobernacionSIT.sit_backend.dto.request.RechazarSolicitudRequest;
import com.gobernacionSIT.sit_backend.dto.response.SolicitudResponse;
import com.gobernacionSIT.sit_backend.entity.AsignacionTecnico;
import com.gobernacionSIT.sit_backend.entity.EstadoSolicitud;
import com.gobernacionSIT.sit_backend.entity.Solicitud;
import com.gobernacionSIT.sit_backend.entity.Usuario;
import com.gobernacionSIT.sit_backend.exception.BusinessException;
import com.gobernacionSIT.sit_backend.exception.ResourceNotFoundException;
import com.gobernacionSIT.sit_backend.repository.AsignacionTecnicoRepository;
import com.gobernacionSIT.sit_backend.repository.EstadoSolicitudRepository;
import com.gobernacionSIT.sit_backend.repository.SolicitudRepository;
import com.gobernacionSIT.sit_backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SolicitudServiceImpl implements SolicitudService {

    private static final String PENDIENTE = "PENDIENTE";
    private static final String EN_PROCESO = "EN_PROCESO";
    private static final String CONCLUIDA = "CONCLUIDA";
    private static final String RECHAZADA = "RECHAZADA";

    private final SolicitudRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;
    private final EstadoSolicitudRepository estadoSolicitudRepository;
    private final AsignacionTecnicoRepository asignacionTecnicoRepository;

    @Override
    @Transactional
    public SolicitudResponse crear(String userLogin, CrearSolicitudRequest request) {
        Usuario solicitante = buscarUsuario(userLogin);

        Solicitud solicitud = new Solicitud();
        solicitud.setCodigo(String.format(Locale.ROOT, "UGE-%04d", solicitudRepository.siguienteNumeroCodigo()));
        solicitud.setTitulo(request.getTitulo());
        solicitud.setCategoria(request.getTipo());
        solicitud.setDescripcion(request.getDescripcion());
        solicitud.setArea(solicitante.getArea());
        solicitud.setUbicacion(solicitante.getUbicacionOficina());
        solicitud.setSolicitante(solicitante);
        solicitud.setEstado(buscarEstado(PENDIENTE));

        return SolicitudResponseMapper.toResponse(solicitudRepository.save(solicitud), null);
    }

    @Override
    @Transactional
    public SolicitudResponse asignar(Long solicitudId, AsignarSolicitudRequest request) {
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada"));
        Usuario tecnico = usuarioRepository.findById(request.getTecnicoId())
                .orElseThrow(() -> new ResourceNotFoundException("Técnico no encontrado"));

        if (!"TECNICO".equals(tecnico.getRol().getNombreRol())) {
            throw new BusinessException("Las solicitudes solo se pueden asignar a usuarios con rol TECNICO");
        }

        if (!PENDIENTE.equals(solicitud.getEstado().getEstadoSolicitud())) {
            throw new BusinessException("Solo se pueden asignar solicitudes pendientes");
        }

        solicitud.setPrioridad(request.getPrioridad());
        AsignacionTecnico asignacion = asignacionTecnicoRepository
                .findBySolicitud_IdOrderByAsignadoAtDesc(solicitudId)
                .stream()
                .findFirst()
                .orElseGet(AsignacionTecnico::new);
        asignacion.setSolicitud(solicitud);
        asignacion.setTecnico(tecnico);
        AsignacionTecnico asignacionGuardada = asignacionTecnicoRepository.save(asignacion);

        return SolicitudResponseMapper.toResponse(solicitudRepository.save(solicitud), asignacionGuardada);
    }

    // ---------------------------------------------------------------
    // Acciones del técnico sobre una solicitud asignada
    // ---------------------------------------------------------------

    /** PENDIENTE -> EN_PROCESO */
    @Override
    @Transactional
    public SolicitudResponse atender(Long solicitudId, String userLogin) {
        AsignacionTecnico asignacion = obtenerAsignacionSolicitud(solicitudId, userLogin);
        Solicitud solicitud = asignacion.getSolicitud();
        validarEstadoActual(solicitud, PENDIENTE, "atender");

        solicitud.setEstado(buscarEstado(EN_PROCESO));
        return SolicitudResponseMapper.toResponse(solicitudRepository.save(solicitud), asignacion);
    }

    /** PENDIENTE -> RECHAZADA (el motivo es obligatorio) */
    @Override
    @Transactional
    public SolicitudResponse rechazar(Long solicitudId, String userLogin, RechazarSolicitudRequest request) {
        AsignacionTecnico asignacion = obtenerAsignacionSolicitud(solicitudId, userLogin);
        Solicitud solicitud = asignacion.getSolicitud();
        validarEstadoActual(solicitud, PENDIENTE, "rechazar");

        String motivo = request.getMotivo() == null ? "" : request.getMotivo().trim();
        if (motivo.length() < 10 || motivo.length() > 500) {
            throw new BusinessException("El motivo debe tener entre 10 y 500 caracteres");
        }

        solicitud.setMotivoRechazo(motivo);
        solicitud.setEstado(buscarEstado(RECHAZADA));
        return SolicitudResponseMapper.toResponse(solicitudRepository.save(solicitud), asignacion);
    }

    /** EN_PROCESO -> CONCLUIDA */
    @Override
    @Transactional
    public SolicitudResponse concluir(Long solicitudId, String userLogin) {
        AsignacionTecnico asignacion = obtenerAsignacionSolicitud(solicitudId, userLogin);
        Solicitud solicitud = asignacion.getSolicitud();
        validarEstadoActual(solicitud, EN_PROCESO, "concluir");

        solicitud.setEstado(buscarEstado(CONCLUIDA));
        return SolicitudResponseMapper.toResponse(solicitudRepository.save(solicitud), asignacion);
    }

    @Override
    @Transactional
    public SolicitudResponse verificar(Long solicitudId, String userLogin) {
        Usuario solicitante = buscarUsuario(userLogin);
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada"));
        if (!solicitud.getSolicitante().getId().equals(solicitante.getId())) {
            throw new AccessDeniedException("Solo puede verificar sus propias solicitudes");
        }
        validarEstadoActual(solicitud, CONCLUIDA, "verificar");
        if (!Boolean.TRUE.equals(solicitud.getVerificadoPorUsuario())) {
            solicitud.setVerificadoPorUsuario(true);
            solicitudRepository.save(solicitud);
        }
        AsignacionTecnico asignacion = asignacionTecnicoRepository
                .findBySolicitud_IdOrderByAsignadoAtDesc(solicitudId)
                .stream()
                .findFirst()
                .orElse(null);
        return SolicitudResponseMapper.toResponse(solicitud, asignacion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SolicitudResponse> listarPropias(String userLogin) {
        Usuario usuario = buscarUsuario(userLogin);
        return toResponses(solicitudRepository.findBySolicitante_Id(usuario.getId()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SolicitudResponse> listarTodas() {
        return toResponses(solicitudRepository.findAll());
    }

    // ---------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------

    /** Busca la solicitud y verifica que esté asignada al técnico que hace la petición. */
    private AsignacionTecnico obtenerAsignacionSolicitud(Long solicitudId, String userLogin) {
        Usuario tecnico = buscarUsuario(userLogin);
        return asignacionTecnicoRepository.findBySolicitud_IdOrderByAsignadoAtDesc(solicitudId).stream()
                .filter(asignacion -> asignacion.getTecnico().getId().equals(tecnico.getId()))
                .findFirst()
                .orElseThrow(() -> new BusinessException("Esta solicitud no está asignada a tu usuario"));
    }

    private void validarEstadoActual(Solicitud solicitud, String estadoEsperado, String accion) {
        String actual = solicitud.getEstado().getEstadoSolicitud();
        if (!estadoEsperado.equals(actual)) {
            throw new BusinessException(
                    "Solo se puede " + accion + " una solicitud en estado " + estadoEsperado + " (actual: " + actual + ")");
        }
    }

    private List<SolicitudResponse> toResponses(List<Solicitud> solicitudes) {
        if (solicitudes.isEmpty()) {
            return List.of();
        }
        List<Long> solicitudIds = solicitudes.stream().map(solicitud -> solicitud.getId()).toList();
        Map<Long, AsignacionTecnico> asignacionesPorSolicitud = new HashMap<>();
        for (AsignacionTecnico asignacion : asignacionTecnicoRepository
                .findBySolicitud_IdInOrderByAsignadoAtDesc(solicitudIds)) {
            asignacionesPorSolicitud.putIfAbsent(asignacion.getSolicitud().getId(), asignacion);
        }
        return solicitudes.stream()
                .map(solicitud -> SolicitudResponseMapper.toResponse(
                        solicitud, asignacionesPorSolicitud.get(solicitud.getId())))
                .toList();
    }

    private Usuario buscarUsuario(String userLogin) {
        return usuarioRepository.findByUserLogin(userLogin)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

    private EstadoSolicitud buscarEstado(String nombre) {
        return estadoSolicitudRepository.findByEstadoSolicitud(nombre)
                .orElseThrow(() -> new IllegalStateException("Estado no configurado: " + nombre));
    }

}
