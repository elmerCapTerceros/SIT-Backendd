package com.gobernacionSIT.sit_backend.service;
import java.util.List;

public interface AsignacionService{
    
    void asignar(Long solicitudId, Long tecnicoId);
    
    List<Long> obtenerTecnicosAsignados(Long solicitudId);

    List<Long> obtenerSolicitudesAsignadas(Long tecnicoId);

    List<Long> obtenerSolicitudesAsignadasPorEstado(Long tecnicoId, String estadoSolicitud);

    List<Long> listarTecnicos ();

}