package com.gobernacionSIT.sit_backend.service;

import com.gobernacionSIT.sit_backend.dto.request.AsignarSolicitudRequest;
import com.gobernacionSIT.sit_backend.dto.request.CrearSolicitudRequest;
import com.gobernacionSIT.sit_backend.dto.response.SolicitudResponse;
import com.gobernacionSIT.sit_backend.entity.AsignacionTecnico;
import com.gobernacionSIT.sit_backend.entity.EstadoSolicitud;
import com.gobernacionSIT.sit_backend.entity.Solicitud;
import com.gobernacionSIT.sit_backend.entity.Usuario;
import com.gobernacionSIT.sit_backend.repository.AsignacionTecnicoRepository;
import com.gobernacionSIT.sit_backend.repository.EstadoSolicitudRepository;
import com.gobernacionSIT.sit_backend.repository.SolicitudRepository;
import com.gobernacionSIT.sit_backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SolicitudServiceImpl implements SolicitudService {

    private final SolicitudRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;
    private final EstadoSolicitudRepository estadoSolicitudRepository;
    private final AsignacionTecnicoRepository asignacionTecnicoRepository;

    @Override
    @Transactional
    public SolicitudResponse crear(String userLogin, CrearSolicitudRequest request) {
        Usuario solicitante = buscarUsuario(userLogin);

        Solicitud solicitud = new Solicitud();
        solicitud.setTitulo(request.getTitulo());
        solicitud.setCategoria(request.getTipo());
        solicitud.setDescripcion(request.getDescripcion());
        solicitud.setArea(solicitante.getArea());
        solicitud.setSolicitante(solicitante);
        solicitud.setEstado(buscarEstado("PENDIENTE"));

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
            throw new IllegalArgumentException("El usuario seleccionado no tiene rol de técnico");
        }

        solicitud.setPrioridad(request.getPrioridad());
        solicitud.setEstado(buscarEstado("EN_PROCESO"));
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

    @Override
    @Transactional(readOnly = true)
    public List<SolicitudResponse> listarPropias(String userLogin) {
        Usuario usuario = buscarUsuario(userLogin);
        return solicitudRepository.findBySolicitante_Id(usuario.getId()).stream()
                .map(solicitud -> toResponse(solicitud, tecnicoId(solicitud.getId())))
                .toList();
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

    private SolicitudResponse toResponse(Solicitud solicitud, Long tecnicoId) {
        return new SolicitudResponse(
                solicitud.getId(), solicitud.getTitulo(), solicitud.getArea(), solicitud.getCategoria(),
                solicitud.getDescripcion(), solicitud.getPrioridad(), solicitud.getEstado().getEstadoSolicitud(),
                solicitud.getSolicitante().getId(), tecnicoId, solicitud.getVerificadoPorUsuario(),
                solicitud.getCreatedAt(), solicitud.getUpdatedAt()
        );
    }
}