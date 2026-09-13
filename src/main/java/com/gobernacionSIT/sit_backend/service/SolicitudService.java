package com.gobernacionSIT.sit_backend.service;

import com.gobernacionSIT.sit_backend.dto.request.AsignarSolicitudRequest;
import com.gobernacionSIT.sit_backend.dto.request.CrearSolicitudRequest;
import com.gobernacionSIT.sit_backend.dto.response.SolicitudResponse;

import java.util.List;

public interface SolicitudService {
    SolicitudResponse crear(String userLogin, CrearSolicitudRequest request);

    SolicitudResponse asignar(Long solicitudId, AsignarSolicitudRequest request);

    List<SolicitudResponse> listarPropias(String userLogin);
}