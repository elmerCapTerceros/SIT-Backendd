package com.gobernacionSIT.sit_backend.repository;

import com.gobernacionSIT.sit_backend.entity.AsignacionTecnico;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AsignacionTecnicoRepository extends JpaRepository<AsignacionTecnico, Long> {
    List<AsignacionTecnico> findBySolicitud_Id(Long solicitudId);

    List<AsignacionTecnico> findByTecnico_Id(Long tecnicoId);

    Optional<AsignacionTecnico> findBySolicitud_IdAndTecnico_Id(Long solicitudId, Long tecnicoId);

    
}
