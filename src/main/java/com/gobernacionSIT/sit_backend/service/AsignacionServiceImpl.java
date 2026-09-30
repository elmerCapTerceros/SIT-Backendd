package com.gobernacionSIT.sit_backend.service;
import com.gobernacionSIT.sit_backend.entity.AsignacionTecnico;
import com.gobernacionSIT.sit_backend.entity.Solicitud;
import com.gobernacionSIT.sit_backend.entity.Usuario;
import com.gobernacionSIT.sit_backend.repository.AsignacionTecnicoRepository;
import com.gobernacionSIT.sit_backend.repository.SolicitudRepository;
import com.gobernacionSIT.sit_backend.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AsignacionServiceImpl implements AsignacionService {

    private final AsignacionTecnicoRepository asignacionTecnicoRepository;
    private final SolicitudRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;

    public AsignacionServiceImpl(AsignacionTecnicoRepository asignacionTecnicoRepository,
                                 SolicitudRepository solicitudRepository,
                                 UsuarioRepository usuarioRepository) {
        this.asignacionTecnicoRepository = asignacionTecnicoRepository;
        this.solicitudRepository = solicitudRepository;
        this.usuarioRepository = usuarioRepository;
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
        AsignacionTecnico asignacion = new AsignacionTecnico();
        asignacion.setSolicitud(solicitud);
        asignacion.setTecnico(tecnico);
        asignacionTecnicoRepository.save(asignacion);
    }

    @Override
    public List<Long> obtenerTecnicosAsignados(Long solicitudId) {
        return asignacionTecnicoRepository.findBySolicitud_Id(solicitudId)
                .stream()
                .map(asignacion -> asignacion.getTecnico().getId())
                .toList();
    }

    @Override
    public List<Long> obtenerSolicitudesAsignadas(Long tecnicoId) {
        return asignacionTecnicoRepository.findByTecnicoId(tecnicoId)
                .stream()
                .map(asignacion -> asignacion.getSolicitud().getId())
                .toList();
    }

    @Override
    public List<Long> obtenerSolicitudesAsignadasPorEstado(Long tecnicoId, String estadoSolicitud) {
        return asignacionTecnicoRepository
                .findByTecnico_IdAndSolicitud_Estado_EstadoSolicitud(tecnicoId, estadoSolicitud)
                .stream()
                .map(asignacion -> asignacion.getSolicitud().getId())
                .toList();
    }

    @Override
    public List<Long> listarTecnicos() {
        return usuarioRepository.findByRol_NombreRol("TECNICO").stream()
            .map(Usuario::getId)
                .toList();
    }
    
}
