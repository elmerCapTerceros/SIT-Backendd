package com.gobernacionSIT.sit_backend.repository;

import com.gobernacionSIT.sit_backend.entity.AsignacionTecnico;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AsignacionTecnicoRepository extends JpaRepository<AsignacionTecnico, Long> {

    List<AsignacionTecnico> findBySolicitud_IdOrderByAsignadoAtDesc(Long solicitudId);

    List<AsignacionTecnico> findBySolicitud_IdIn(List<Long> solicitudIds);

    List<AsignacionTecnico> findBySolicitud_IdInOrderByAsignadoAtDesc(List<Long> solicitudIds);

    List<AsignacionTecnico> findByTecnicoId(Long tecnicoId);

    List<AsignacionTecnico> findByTecnico_IdAndSolicitud_Estado_EstadoSolicitud(
            Long tecnicoId, String estadoSolicitud);

}
