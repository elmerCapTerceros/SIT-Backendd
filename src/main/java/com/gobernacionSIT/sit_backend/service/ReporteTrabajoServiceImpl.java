package com.gobernacionSIT.sit_backend.service;

import com.gobernacionSIT.sit_backend.dto.request.CrearReporteTrabajoRequest;
import com.gobernacionSIT.sit_backend.dto.response.ReporteTrabajoResponse;
import com.gobernacionSIT.sit_backend.entity.AsignacionTecnico;
import com.gobernacionSIT.sit_backend.entity.ReporteTrabajo;
import com.gobernacionSIT.sit_backend.entity.Usuario;
import com.gobernacionSIT.sit_backend.exception.BusinessException;
import com.gobernacionSIT.sit_backend.exception.ResourceNotFoundException;
import com.gobernacionSIT.sit_backend.repository.AsignacionTecnicoRepository;
import com.gobernacionSIT.sit_backend.repository.ReporteTrabajoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReporteTrabajoServiceImpl implements ReporteTrabajoService {

    private final AsignacionTecnicoRepository asignacionTecnicoRepository;
    private final ReporteTrabajoRepository reporteTrabajoRepository;

    @Override
    @Transactional
    public ReporteTrabajoResponse crear(String userLogin, CrearReporteTrabajoRequest request) {
        if (request.getAsignacionTecnicoId() == null && request.getSolicitudId() == null) {
            throw new BusinessException("La solicitud es obligatoria para guardar el reporte");
        }
        AsignacionTecnico asignacion;
        if (request.getAsignacionTecnicoId() != null) {
            asignacion = asignacionTecnicoRepository.findById(request.getAsignacionTecnicoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Asignación de técnico no encontrada"));
        } else {
            asignacion = asignacionTecnicoRepository
                    .findBySolicitud_IdOrderByAsignadoAtDesc(request.getSolicitudId()).stream()
                    .filter(asignacionEncontrada ->
                            asignacionEncontrada.getTecnico().getUserLogin().equals(userLogin))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "No se encontró la asignación de esta solicitud para el técnico"));
        }

        if (!asignacion.getTecnico().getUserLogin().equals(userLogin)) {
            throw new AccessDeniedException("Solo el técnico asignado puede crear el reporte");
        }

        Long solicitudId = asignacion.getSolicitud().getId();
        if (reporteTrabajoRepository.existsBySolicitud_Id(solicitudId)) {
            throw new BusinessException("La solicitud ya tiene un reporte de trabajo");
        }

        ReporteTrabajo reporte = new ReporteTrabajo();
        reporte.setSolicitud(asignacion.getSolicitud());
        reporte.setAsignacionTecnico(asignacion);
        reporte.setNumeroFormulario(request.getNumeroFormulario());
        reporte.setFechaAtencion(request.getFechaAtencion());
        reporte.setHoraAtencion(request.getHoraAtencion());
        reporte.setUnidad(request.getUnidad());
        reporte.setSolicitante(request.getSolicitante());
        reporte.setNumeroInventario(request.getNumeroInventario());
        reporte.setTipoEquipo(request.getTipoEquipo());
        reporte.setTipoEquipoOtro(request.getTipoEquipoOtro());
        reporte.setProblema(request.getProblema());
        reporte.setDiagnostico(request.getDiagnostico());
        reporte.setTrabajoRealizado(request.getTrabajoRealizado());
        reporte.setEstadoFinal(request.getEstadoFinal());
        reporte.setObservacion(request.getObservacion());
        reporte.setTecnicoNombre(nombreCompleto(asignacion.getTecnico()));
        reporte.setFuncionarioNombre(request.getFuncionarioNombre());
        return toResponse(reporteTrabajoRepository.save(reporte));
    }

    private ReporteTrabajoResponse toResponse(ReporteTrabajo reporte) {
        return new ReporteTrabajoResponse(
                reporte.getId(),
                reporte.getSolicitud().getId(),
                reporte.getAsignacionTecnico().getId(),
                reporte.getAsignacionTecnico().getTecnico().getId(),
                reporte.getSolicitud().getCodigo(),
                reporte.getNumeroFormulario(),
                reporte.getFechaAtencion(),
                reporte.getHoraAtencion(),
                reporte.getUnidad(),
                reporte.getSolicitante(),
                reporte.getNumeroInventario(),
                reporte.getTipoEquipo(),
                reporte.getTipoEquipoOtro(),
                reporte.getProblema(),
                reporte.getDiagnostico(),
                reporte.getTrabajoRealizado(),
                reporte.getEstadoFinal(),
                reporte.getObservacion(),
                nombreCompleto(reporte.getAsignacionTecnico().getTecnico()),
                reporte.getFuncionarioNombre(),
                Boolean.TRUE.equals(reporte.getSolicitud().getVerificadoPorUsuario()),
                reporte.getCreatedAt()
        );
    }

    private String nombreCompleto(Usuario usuario) {
        String nombre = usuario.getNombre();
        String apellido = usuario.getApellido();
        if (nombre == null || nombre.isBlank()) {
            return apellido;
        }
        if (apellido == null || apellido.isBlank()) {
            return nombre;
        }
        return nombre + " " + apellido;
    }

    @Transactional(readOnly = true)
    public List<ReporteTrabajoResponse> listarTodosReportes(){
        List<ReporteTrabajo> reportes = reporteTrabajoRepository.findAll();
        return reportes.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<ReporteTrabajoResponse> listarReportePorTecnico(String userLogin){
        List<ReporteTrabajo> reportes = reporteTrabajoRepository.findByAsignacionTecnico_Tecnico_UserLogin(userLogin);
        return reportes.stream().map(this::toResponse).toList();
    }

    
}
