package com.gobernacionSIT.sit_backend.dto.response;
import java.util.List;  
public record DashboardResponse(
        List<ConteoDto> solicitudesPorEstado,
        List<ConteoDto> trabajosPorTecnico,
        List<ConteoDto> reportesPorEstadoFinal,
        List<ConteoDto> solicitudesPorCategoria,
        List<ConteoDto> solicitudesPorArea,
        List<ConteoDto> verificacionUsuarios) {}
