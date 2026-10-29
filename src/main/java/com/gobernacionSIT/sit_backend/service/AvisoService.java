package com.gobernacionSIT.sit_backend.service;

import com.gobernacionSIT.sit_backend.dto.request.AvisoRequest;
import com.gobernacionSIT.sit_backend.dto.response.AvisoResponse;
import com.gobernacionSIT.sit_backend.entity.Aviso;
import com.gobernacionSIT.sit_backend.entity.Usuario;
import com.gobernacionSIT.sit_backend.repository.AvisoRepository;
import com.gobernacionSIT.sit_backend.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AvisoService {

    private final AvisoRepository avisoRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public AvisoResponse crear(AvisoRequest request) {
        Usuario supervisor = usuarioRepository.findById(request.idSupervisor())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Supervisor no encontrado con id " + request.idSupervisor()));

        Aviso aviso = new Aviso();
        aviso.setSupervisor(supervisor);
        aviso.setMensaje(request.mensaje());
        aviso.setDestinatario(request.destinatario());
        aviso.setActivo(true);
        aviso.setCreatedAt(LocalDateTime.now());

        return toResponse(avisoRepository.save(aviso));
    }

    @Transactional(readOnly = true)
    public AvisoResponse obtenerPorId(Long id) {
        return toResponse(buscarOLanzar(id));
    }

    @Transactional(readOnly = true)
    public List<AvisoResponse> listarActivos() {
        return avisoRepository.findByActivoOrderByCreatedAtDesc(true)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<AvisoResponse> listarPorDestinatario(String destinatario) {
        return avisoRepository.findByDestinatarioInAndActivoOrderByCreatedAtDesc(
                        List.of(destinatario, "AMBOS"), true)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public AvisoResponse obtenerPorId(Long id, String destinatario) {
        Aviso aviso = buscarOLanzar(id);
        if (!Boolean.TRUE.equals(aviso.getActivo())
                || (!destinatario.equals(aviso.getDestinatario())
                && !"AMBOS".equals(aviso.getDestinatario()))) {
            throw new AccessDeniedException("No tiene acceso a este aviso");
        }
        return toResponse(aviso);
    }

    @Transactional(readOnly = true)
    public List<AvisoResponse> listarPorSupervisor(Long idSupervisor) {
        return avisoRepository.findBySupervisorIdOrderByCreatedAtDesc(idSupervisor)
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public AvisoResponse actualizar(Long id, AvisoRequest request) {
        Aviso aviso = buscarOLanzar(id);
        aviso.setMensaje(request.mensaje());
        aviso.setDestinatario(request.destinatario());
        return toResponse(aviso);
    }

    @Transactional
    public void cambiarEstado(Long id, boolean activo) {
        int filas = avisoRepository.cambiarActivo(id, activo);
        if (filas == 0) {
            throw new EntityNotFoundException("Aviso no encontrado con id " + id);
        }
    }

    private Aviso buscarOLanzar(Long id) {
        return avisoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Aviso no encontrado con id " + id));
    }

    private AvisoResponse toResponse(Aviso a) {
        return new AvisoResponse(
                a.getId(),
                a.getSupervisor().getId(),
                a.getMensaje(),
                a.getDestinatario(),
                a.getActivo(),
                a.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public List<AvisoResponse> listarTodos() {
        return avisoRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(this::toResponse).toList();
    }
    
    @Transactional
    public void eliminarAviso(Long id) {
        Aviso aviso = buscarOLanzar(id);
        avisoRepository.delete(aviso);
    }

}