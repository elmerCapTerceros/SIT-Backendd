package com.gobernacionSIT.sit_backend.dto.request;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RequestValidationTests {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void rejectsTitleShorterThanFiveCharacters() {
        CrearSolicitudRequest request = new CrearSolicitudRequest();
        request.setTitulo("Wifi");
        request.setTipo("PC");
        request.setDescripcion("Prueba límite QA");

        assertTrue(validator.validate(request).stream()
                .anyMatch(error -> error.getPropertyPath().toString().equals("titulo")));
    }

    @Test
    void rejectsInvalidRegistrationPhoneAndPasswordLength() {
        RegistrarUsuarioRequest request = new RegistrarUsuarioRequest();
        request.setNombre("Nombre");
        request.setApellido("Apellido");
        request.setUserLogin("qa_validacion");
        request.setPassword("123456789");
        request.setCargo("QA");
        request.setTelefono("1234567");
        request.setArea("Sistemas");
        request.setUbicacionOficina("Oficina");

        var fields = validator.validate(request).stream()
                .map(error -> error.getPropertyPath().toString())
                .toList();

        assertTrue(fields.contains("telefono"));
        assertTrue(fields.contains("password"));
    }

    @Test
    void rejectsRejectionReasonOutsideAllowedLength() {
        RechazarSolicitudRequest request = new RechazarSolicitudRequest();
        request.setMotivo("Muy corto");

        assertTrue(validator.validate(request).stream()
                .anyMatch(error -> error.getPropertyPath().toString().equals("motivo")));
    }

    @Test
    void acceptsRejectionReasonWithAllowedLength() {
        RechazarSolicitudRequest request = new RechazarSolicitudRequest();
        request.setMotivo("Motivo valido para rechazo");

        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void allowsReportToResolveAssignmentFromSolicitudId() {
        CrearReporteTrabajoRequest request = new CrearReporteTrabajoRequest();
        request.setSolicitudId(20L);
        request.setDiagnostico("Diagnóstico registrado");
        request.setTrabajoRealizado("Trabajo completado");
        request.setEstadoFinal("SOLUCIONADO");

        assertTrue(validator.validate(request).stream()
                .noneMatch(error -> error.getPropertyPath().toString().equals("asignacionTecnicoId")));
    }
}