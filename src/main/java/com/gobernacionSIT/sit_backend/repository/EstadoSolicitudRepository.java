package com.gobernacionSIT.sit_backend.repository;

import com.gobernacionSIT.sit_backend.entity.EstadoSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EstadoSolicitudRepository extends JpaRepository<EstadoSolicitud, Long> {

    Optional<EstadoSolicitud> findByEstadoSol(String estadoSol);
}
