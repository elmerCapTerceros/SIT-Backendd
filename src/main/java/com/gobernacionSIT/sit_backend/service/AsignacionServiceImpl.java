package com.gobernacionSIT.sit_backend.service;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AsignacionServiceImpl implements AsignacionService {

    private static final String PENDIENTE = "PENDIENTE";
    private static final String RECHAZADA = "RECHAZADA";
    private static final String CONCLUIDA = "CONCLUIDA";

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
    @Transactional
    public void asignar(Long solicitudId, Long tecnicoId) {
         Solicitud solicitud = solicitudRepository.findById(solicitudId)
             .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada"));
        Usuario tecnico = usuarioRepository.findById(tecnicoId)
             .orElseThrow(() -> new ResourceNotFoundException("Técnico no encontrado"));
            validarRolTecnico(tecnico);

        String estadoActual = solicitud.getEstado().getEstadoSolicitud();
        if (CONCLUIDA.equals(estadoActual)) {
        throw new BusinessException("No se puede asignar una solicitud concluida");
        }

        List<AsignacionTecnico> actuales = asignacionTecnicoRepository
            .findBySolicitud_IdOrderByAsignadoAtDesc(solicitudId);

        boolean mismoTecnico = !actuales.isEmpty()
            && actuales.get(0).getTecnico().getId().equals(tecnicoId);
        if (mismoTecnico && !RECHAZADA.equals(estadoActual)) {
        throw new BusinessException("El técnico ya está asignado a esta solicitud");
         }

        if (!PENDIENTE.equals(estadoActual)) {
             EstadoSolicitud pendiente = estadoSolicitudRepository.findByEstadoSolicitud(PENDIENTE)
                .orElseThrow(() -> new ResourceNotFoundException("Estado PENDIENTE no encontrado"));
                solicitud.setEstado(pendiente);
                solicitud.setMotivoRechazo(null);
            solicitudRepository.save(solicitud);
        }


        if (!actuales.isEmpty()) {
             asignacionTecnicoRepository.deleteAll(actuales);
            asignacionTecnicoRepository.flush();   
        }

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
        return asignacionTecnicoRepository.findBySolicitud_IdOrderByAsignadoAtDesc(solicitudId)
                .stream()
                .findFirst()
                .map(asignacion -> List.of(asignacion.getTecnico().getId()))
                .orElseGet(List::of);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SolicitudResponse> obtenerSolicitudesAsignadas(Long tecnicoId, String userLogin) {
        validarTecnicoSolicitudes(tecnicoId, userLogin);
        return respuestasDeAsignacionesVigentes(asignacionTecnicoRepository.findByTecnicoId(tecnicoId), tecnicoId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SolicitudResponse> obtenerSolicitudesAsignadasPorEstado(
            Long tecnicoId, String estadoSolicitud, String userLogin) {
        validarTecnicoSolicitudes(tecnicoId, userLogin);
        return respuestasDeAsignacionesVigentes(
                asignacionTecnicoRepository
                        .findByTecnico_IdAndSolicitud_Estado_EstadoSolicitud(tecnicoId, estadoSolicitud),
                tecnicoId);
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
        return SolicitudResponseMapper.toResponse(asignacion.getSolicitud(), asignacion);
    }

    private List<SolicitudResponse> respuestasDeAsignacionesVigentes(
            List<AsignacionTecnico> asignaciones, Long tecnicoId) {
        if (asignaciones.isEmpty()) {
            return List.of();
        }
        List<Long> solicitudIds = asignaciones.stream()
                .map(asignacion -> asignacion.getSolicitud().getId())
                .distinct()
                .toList();
        Map<Long, AsignacionTecnico> asignacionVigentePorSolicitud = new HashMap<>();
        for (AsignacionTecnico asignacion : asignacionTecnicoRepository
                .findBySolicitud_IdInOrderByAsignadoAtDesc(solicitudIds)) {
            asignacionVigentePorSolicitud.putIfAbsent(asignacion.getSolicitud().getId(), asignacion);
        }
        return asignaciones.stream()
                .map(asignacion -> asignacionVigentePorSolicitud.get(asignacion.getSolicitud().getId()))
                .filter(asignacion -> asignacion != null
                        && asignacion.getTecnico().getId().equals(tecnicoId))
                .map(this::toSolicitudResponse)
                .toList();
    }

    @Override
    public List<Long> listarTecnicos() {
        return usuarioRepository.findByRol_NombreRol("TECNICO").stream()
            .map(usuario -> usuario.getId())
                .toList();
    }

    @Override 
    @Transactional
    public void rechazarAsignacionTwo(Long idAsignacion, String userLogin) {
        Usuario tecnico = usuarioRepository.findByUserLogin(userLogin)
                .orElseThrow(() -> new AccessDeniedException("Usuario autenticado no encontrado"));
        AsignacionTecnico asignacion = asignacionTecnicoRepository.findById(idAsignacion)
                .orElseThrow(() -> new IllegalArgumentException("Asignación no encontrada"));
        if (!asignacion.getTecnico().getId().equals(tecnico.getId())) {
            throw new AccessDeniedException("Solo el técnico asignado puede rechazar esta asignación");
        }
        asignacionTecnicoRepository.delete(asignacion);
    }
    
}
