package com.gobernacionSIT.sit_backend.service;
import com.gobernacionSIT.sit_backend.dto.response.DashboardResponse;
import com.gobernacionSIT.sit_backend.repository.DashboardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor

public class DashboardService {

    private final DashboardRepository repo;

    @Transactional(readOnly = true)
    public DashboardResponse obtenerResumen() {
        return new DashboardResponse(
                repo.solicitudesPorEstado(),
                repo.trabajosPorTecnico(),
                repo.reportesPorEstadoFinal(),
                repo.solicitudesPorCategoria(),
                repo.solicitudesPorArea(),
                repo.verificacionUsuarios());
    }
}
