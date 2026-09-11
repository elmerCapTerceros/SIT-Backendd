package com.gobernacionSIT.sit_backend.repository;

import com.gobernacionSIT.sit_backend.entity.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {

    // Ítem #2: funcionario lista sus propias solicitudes
    List<Solicitud> findBySolicitante_Id(Long solicitanteId);

    // Ítem #9: supervisor ve todas las solicitudes
    // Heredado de JpaRepository.findAll()

    // Filtrar por estado
    List<Solicitud> findByEstado_EstadoSol(String estadoSol);

    // Ítem #4: técnico lista solicitudes que tiene asignadas
    // TODO: implementar cuando esté definida la relación Solicitud-Asignacion
    // List<Solicitud> findByAsignaciones_Tecnico_Id(Long tecnicoId);

    // Filtrar por prioridad
    List<Solicitud> findByPrioridad(String prioridad);
}