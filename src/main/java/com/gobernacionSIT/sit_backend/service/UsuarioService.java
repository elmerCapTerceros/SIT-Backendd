package com.gobernacionSIT.sit_backend.service;

import com.gobernacionSIT.sit_backend.dto.request.RegistrarUsuarioRequest;
import com.gobernacionSIT.sit_backend.dto.request.ActualizarPerfilRequest;
import com.gobernacionSIT.sit_backend.dto.response.UsuarioResponse;

import java.util.List;

public interface UsuarioService {

    UsuarioResponse registrar(RegistrarUsuarioRequest request);

    UsuarioResponse actualizarPerfil(String userLogin, ActualizarPerfilRequest request);

    UsuarioResponse buscarPorId(Long id);

    List<UsuarioResponse> listarTecnicos();

    List<UsuarioResponse> listarPorArea(String area);
}