package com.gobernacionSIT.sit_backend.service;

import com.gobernacionSIT.sit_backend.dto.request.AsignarSolicitudRequest;
import com.gobernacionSIT.sit_backend.dto.request.CrearSolicitudRequest;
import com.gobernacionSIT.sit_backend.dto.request.RechazarSolicitudRequest;
import com.gobernacionSIT.sit_backend.dto.response.SolicitudResponse;

import java.util.List;

public interface SolicitudService {
    SolicitudResponse crear(String userLogin, CrearSolicitudRequest request);

    SolicitudResponse asignar(Long solicitudId, AsignarSolicitudRequest request);

    SolicitudResponse atender(Long solicitudId, String userLogin);

    SolicitudResponse rechazar(Long solicitudId, String userLogin, RechazarSolicitudRequest request);

    SolicitudResponse concluir(Long solicitudId, String userLogin);

    List<SolicitudResponse> listarPropias(String userLogin);

    //Listar todas mis solictudes
    List<SolicitudResponse> listarTodaas();

    void actualizarEstadoSolicitud(Long solicitudId, Long estadoId);
}