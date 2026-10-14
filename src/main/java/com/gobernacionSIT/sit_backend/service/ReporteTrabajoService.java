package com.gobernacionSIT.sit_backend.service;

import com.gobernacionSIT.sit_backend.dto.request.CrearReporteTrabajoRequest;
import com.gobernacionSIT.sit_backend.dto.response.ReporteTrabajoResponse;
import java.util.List;

public interface ReporteTrabajoService {
    ReporteTrabajoResponse crear(String userLogin, CrearReporteTrabajoRequest request);

    public List<ReporteTrabajoResponse> listarTodosReportes();

    public List<ReporteTrabajoResponse> listarReportePorTecnico(String userLogin);
}
