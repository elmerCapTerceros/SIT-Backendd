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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

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

        return toResponse(solicitudRepository.save(solicitud), null);
    }

    @Override
    @Transactional
    public SolicitudResponse asignar(Long solicitudId, AsignarSolicitudRequest request) {
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));
        Usuario tecnico = usuarioRepository.findById(request.getTecnicoId())
                .orElseThrow(() -> new IllegalArgumentException("Técnico no encontrado"));

        if (!"TECNICO".equals(tecnico.getRol().getNombreRol())) {
            throw new BusinessException("Las solicitudes solo se pueden asignar a usuarios con rol TECNICO");
        }

        solicitud.setPrioridad(request.getPrioridad());
        // CAMBIO: al asignar, la solicitud sigue PENDIENTE.
        // Pasa a EN_PROCESO recién cuando el técnico pulsa "Atender".
        solicitud.setEstado(buscarEstado(PENDIENTE));
        AsignacionTecnico asignacion = asignacionTecnicoRepository
                .findBySolicitud_Id(solicitudId)
                .stream()
                .findFirst()
                .orElseGet(AsignacionTecnico::new);
        asignacion.setSolicitud(solicitud);
        asignacion.setTecnico(tecnico);
        asignacionTecnicoRepository.save(asignacion);

        return toResponse(solicitudRepository.save(solicitud), tecnico.getId());
    }

    // ---------------------------------------------------------------
    // Acciones del técnico sobre una solicitud asignada
    // ---------------------------------------------------------------

    /** PENDIENTE -> EN_PROCESO */
    @Override
    @Transactional
    public SolicitudResponse atender(Long solicitudId, String userLogin) {
        Solicitud solicitud = obtenerSolicitudAsignada(solicitudId, userLogin);
        validarEstadoActual(solicitud, PENDIENTE, "atender");

        solicitud.setEstado(buscarEstado(EN_PROCESO));
        return toResponse(solicitudRepository.save(solicitud), tecnicoId(solicitudId));
    }

    /** PENDIENTE -> RECHAZADA (el motivo es obligatorio) */
    @Override
    @Transactional
    public SolicitudResponse rechazar(Long solicitudId, String userLogin, RechazarSolicitudRequest request) {
        Solicitud solicitud = obtenerSolicitudAsignada(solicitudId, userLogin);
        validarEstadoActual(solicitud, PENDIENTE, "rechazar");

        String motivo = request.getMotivo() == null ? "" : request.getMotivo().trim();
        if (motivo.length() < 10 || motivo.length() > 500) {
            throw new BusinessException("El motivo debe tener entre 10 y 500 caracteres");
        }

        solicitud.setMotivoRechazo(motivo);
        solicitud.setEstado(buscarEstado(RECHAZADA));
        return toResponse(solicitudRepository.save(solicitud), tecnicoId(solicitudId));
    }

    /** EN_PROCESO -> CONCLUIDA */
    @Override
    @Transactional
    public SolicitudResponse concluir(Long solicitudId, String userLogin) {
        Solicitud solicitud = obtenerSolicitudAsignada(solicitudId, userLogin);
        validarEstadoActual(solicitud, EN_PROCESO, "concluir");

        solicitud.setEstado(buscarEstado(CONCLUIDA));
        return toResponse(solicitudRepository.save(solicitud), tecnicoId(solicitudId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SolicitudResponse> listarPropias(String userLogin) {
        Usuario usuario = buscarUsuario(userLogin);
        return solicitudRepository.findBySolicitante_Id(usuario.getId()).stream()
                .map(solicitud -> toResponse(solicitud, tecnicoId(solicitud.getId())))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SolicitudResponse> listarTodaas() {
        return solicitudRepository.findAll().stream()
                .map(solicitud -> toResponse(solicitud, tecnicoId(solicitud.getId())))
                .toList();
    }

    @Override
    @Transactional
    public void actualizarEstadoSolicitud(Long solicitudId, Long estadoId) {
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada"));
        EstadoSolicitud estado = estadoSolicitudRepository.findById(estadoId)
                .orElseThrow(() -> new ResourceNotFoundException("Estado de solicitud no encontrado"));

        solicitud.setEstado(estado);
    }

    // ---------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------

    /** Busca la solicitud y verifica que esté asignada al técnico que hace la petición. */
    private Solicitud obtenerSolicitudAsignada(Long solicitudId, String userLogin) {
        Usuario tecnico = buscarUsuario(userLogin);
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada"));

        boolean asignadaAlTecnico = asignacionTecnicoRepository.findBySolicitud_Id(solicitudId).stream()
                .anyMatch(asignacion -> asignacion.getTecnico().getId().equals(tecnico.getId()));
        if (!asignadaAlTecnico) {
            throw new BusinessException("Esta solicitud no está asignada a tu usuario");
        }
        return solicitud;
    }

    private void validarEstadoActual(Solicitud solicitud, String estadoEsperado, String accion) {
        String actual = solicitud.getEstado().getEstadoSolicitud();
        if (!estadoEsperado.equals(actual)) {
            throw new BusinessException(
                    "Solo se puede " + accion + " una solicitud en estado " + estadoEsperado + " (actual: " + actual + ")");
        }
    }

    private Usuario buscarUsuario(String userLogin) {
        return usuarioRepository.findByUserLogin(userLogin)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }

    private EstadoSolicitud buscarEstado(String nombre) {
        return estadoSolicitudRepository.findByEstadoSolicitud(nombre)
                .orElseThrow(() -> new IllegalStateException("Estado no configurado: " + nombre));
    }

    private Long tecnicoId(Long solicitudId) {
        return asignacionTecnicoRepository.findBySolicitud_Id(solicitudId).stream()
                .findFirst()
                .map(asignacion -> asignacion.getTecnico().getId())
                .orElse(null);
    }

    private String nombreCompleto(Usuario usuario) {
        String nombre = usuario.getNombre();
        String apellido = usuario.getApellido();
        if (nombre == null || nombre.isBlank()) return apellido;
        if (apellido == null || apellido.isBlank()) return nombre;
        return nombre + " " + apellido;
    }

    private SolicitudResponse toResponse(Solicitud solicitud, Long tecnicoId) {
        return new SolicitudResponse(
                solicitud.getId(), solicitud.getCodigo(), solicitud.getTitulo(), solicitud.getArea(),
                solicitud.getUbicacion(), solicitud.getCategoria(),
                solicitud.getDescripcion(), solicitud.getPrioridad(), solicitud.getEstado().getEstadoSolicitud(),
                solicitud.getSolicitante().getId(), nombreCompleto(solicitud.getSolicitante()),
                tecnicoId, asignacionTecnicoId(solicitud.getId()), solicitud.getVerificadoPorUsuario(),
                solicitud.getCreatedAt(), solicitud.getUpdatedAt(),
                solicitud.getSolicitante().getCargo(), solicitud.getSolicitante().getTelefono(),
                solicitud.getMotivoRechazo()
        );
    }

    private Long asignacionTecnicoId(Long solicitudId) {
        return asignacionTecnicoRepository.findBySolicitud_Id(solicitudId).stream()
                .findFirst()
                .map(asignacion -> asignacion.getId()) 
                .orElse(null);
    }
}