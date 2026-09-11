package com.gobernacionSIT.sit_backend.repository;

import com.gobernacionSIT.sit_backend.entity.ReporteTrabajo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ReporteTrabajoRepository extends JpaRepository<ReporteTrabajo, Long> {
    Optional<ReporteTrabajo> findBySolicitud_Id(Long solicitudId);

    // Ítem #7 y #12: técnico/supervisor ven reportes por técnico
    List<ReporteTrabajo> findByAsignacionTecnico_Tecnico_Id(Long tecnicoId);

    List<ReporteTrabajo> findByEstadoFinal(String estadoFinal);
}
