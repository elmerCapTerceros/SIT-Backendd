package com.gobernacionSIT.sit_backend.repository;

import com.gobernacionSIT.sit_backend.entity.AsignacionTecnico;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AsignacionTecnicoRepository extends JpaRepository<AsignacionTecnico, Long> {

    boolean existsBySolicitudIdAndTecnicoId(Long solicitudId, Long tecnicoId);

    List<AsignacionTecnico> findBySolicitud_Id(Long solicitudId);

    List<AsignacionTecnico> findByTecnicoId(Long tecnicoId);

        List<AsignacionTecnico> findByTecnico_IdAndSolicitud_Estado_EstadoSolicitud(
            Long tecnicoId, String estadoSolicitud);

    Optional<AsignacionTecnico> findBySolicitudIdAndTecnicoId(Long solicitudId, Long tecnicoId);

}
