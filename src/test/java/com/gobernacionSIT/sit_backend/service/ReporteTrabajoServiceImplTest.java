package com.gobernacionSIT.sit_backend.service;

import com.gobernacionSIT.sit_backend.dto.request.CrearReporteTrabajoRequest;
import com.gobernacionSIT.sit_backend.entity.AsignacionTecnico;
import com.gobernacionSIT.sit_backend.entity.ReporteTrabajo;
import com.gobernacionSIT.sit_backend.entity.Solicitud;
import com.gobernacionSIT.sit_backend.entity.Usuario;
import com.gobernacionSIT.sit_backend.exception.BusinessException;
import com.gobernacionSIT.sit_backend.exception.ResourceNotFoundException;
import com.gobernacionSIT.sit_backend.repository.AsignacionTecnicoRepository;
import com.gobernacionSIT.sit_backend.repository.ReporteTrabajoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReporteTrabajoServiceImplTest {

    private final AsignacionTecnicoRepository asignacionRepository = mock(AsignacionTecnicoRepository.class);
    private final ReporteTrabajoRepository reporteRepository = mock(ReporteTrabajoRepository.class);
    private final ReporteTrabajoServiceImpl service = new ReporteTrabajoServiceImpl(
            asignacionRepository, reporteRepository);

    @Test
    void creaReporteParaTecnicoAsignado() {
        AsignacionTecnico asignacion = asignacion(10L, 20L, "tecnico1");
        CrearReporteTrabajoRequest request = request();
        when(asignacionRepository.findById(10L)).thenReturn(Optional.of(asignacion));
        when(reporteRepository.existsBySolicitud_Id(20L)).thenReturn(false);
        when(reporteRepository.save(any(ReporteTrabajo.class))).thenAnswer(invocation -> {
            ReporteTrabajo reporte = invocation.getArgument(0);
            reporte.setId(30L);
            return reporte;
        });

        var response = service.crear("tecnico1", request);

        assertEquals(30L, response.id());
        assertEquals(20L, response.solicitudId());
        assertEquals(10L, response.asignacionTecnicoId());
        assertEquals(99L, response.tecnicoId());
        assertEquals("SOLUCIONADO", response.estadoFinal());
        assertEquals("2026-10-17", response.fechaAtencion().toString());
        assertEquals("Edificio Central", response.unidad());
        assertEquals("STI-0042", response.numeroFormulario());
        verify(reporteRepository).save(any(ReporteTrabajo.class));
    }

    @Test
    void creaReporteBuscandoLaAsignacionPorSolicitud() {
        AsignacionTecnico asignacion = asignacion(10L, 20L, "tecnico1");
        CrearReporteTrabajoRequest request = request();
        request.setAsignacionTecnicoId(null);
        request.setSolicitudId(20L);
        when(asignacionRepository.findBySolicitud_Id(20L)).thenReturn(List.of(asignacion));
        when(reporteRepository.existsBySolicitud_Id(20L)).thenReturn(false);
        when(reporteRepository.save(any(ReporteTrabajo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.crear("tecnico1", request);

        assertEquals(20L, response.solicitudId());
        assertEquals(10L, response.asignacionTecnicoId());
        verify(reporteRepository).save(any(ReporteTrabajo.class));
    }

    @Test
    void rechazaReporteDeOtroTecnico() {
        when(asignacionRepository.findById(10L)).thenReturn(Optional.of(asignacion(10L, 20L, "tecnico1")));

        assertThrows(AccessDeniedException.class, () -> service.crear("tecnico2", request()));

        verify(reporteRepository, never()).save(any(ReporteTrabajo.class));
    }

    @Test
    void rechazaSegundoReporteParaLaSolicitud() {
        when(asignacionRepository.findById(10L)).thenReturn(Optional.of(asignacion(10L, 20L, "tecnico1")));
        when(reporteRepository.existsBySolicitud_Id(20L)).thenReturn(true);

        assertThrows(BusinessException.class, () -> service.crear("tecnico1", request()));

        verify(reporteRepository, never()).save(any(ReporteTrabajo.class));
    }

    @Test
    void rechazaAsignacionInexistente() {
        when(asignacionRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.crear("tecnico1", request()));

        verify(reporteRepository, never()).save(any(ReporteTrabajo.class));
    }

    private CrearReporteTrabajoRequest request() {
        CrearReporteTrabajoRequest request = new CrearReporteTrabajoRequest();
        request.setAsignacionTecnicoId(10L);
        request.setTipoEquipo("Impresora");
        request.setDiagnostico("Atasco de papel");
        request.setTrabajoRealizado("Limpieza y prueba");
        request.setEstadoFinal("SOLUCIONADO");
        request.setObservacion("Equipo operativo");
        request.setNumeroFormulario("STI-0042");
        request.setFechaAtencion(java.time.LocalDate.of(2026, 10, 17));
        request.setUnidad("Edificio Central");
        return request;
    }

    private AsignacionTecnico asignacion(Long id, Long solicitudId, String userLogin) {
        Usuario tecnico = new Usuario();
        tecnico.setId(99L);
        tecnico.setUserLogin(userLogin);

        Solicitud solicitud = new Solicitud();
        solicitud.setId(solicitudId);

        AsignacionTecnico asignacion = new AsignacionTecnico();
        asignacion.setId(id);
        asignacion.setTecnico(tecnico);
        asignacion.setSolicitud(solicitud);
        return asignacion;
    }
}
