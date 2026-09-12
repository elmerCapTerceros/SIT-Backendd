package com.gobernacionSIT.sit_backend.repository;

import com.gobernacionSIT.sit_backend.entity.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {

    List<Solicitud> findBySolicitante_Id(Long solicitanteId);

    List<Solicitud> findByEstado_EstadoSolicitud(String estadoSolicitud);

    List<Solicitud> findByPrioridad(String prioridad);
}
