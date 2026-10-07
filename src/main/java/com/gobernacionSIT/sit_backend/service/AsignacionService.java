package com.gobernacionSIT.sit_backend.service;
import com.gobernacionSIT.sit_backend.dto.response.SolicitudResponse;
import java.util.List;

public interface AsignacionService{
    
    void asignar(Long solicitudId, Long tecnicoId);
    
    List<Long> obtenerTecnicosAsignados(Long solicitudId);

    List<SolicitudResponse> obtenerSolicitudesAsignadas(Long tecnicoId, String userLogin);

    List<SolicitudResponse> obtenerSolicitudesAsignadasPorEstado(
            Long tecnicoId, String estadoSolicitud, String userLogin);

    List<Long> listarTecnicos ();

}