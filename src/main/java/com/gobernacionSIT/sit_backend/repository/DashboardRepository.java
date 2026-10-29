package com.gobernacionSIT.sit_backend.repository;


import com.gobernacionSIT.sit_backend.dto.response.ConteoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class DashboardRepository {

    private final JdbcTemplate jdbc;

    private List<ConteoDto> consultar(String sql) {
        return jdbc.query(sql, (rs, i) ->
                new ConteoDto(rs.getString("etiqueta"), rs.getLong("total")));
    }

    public List<ConteoDto> solicitudesPorEstado() {
        return consultar("""
            SELECT es.estado_solicitud AS etiqueta, COUNT(*) AS total
            FROM solicitud s
            INNER JOIN estado_solicitud es ON s.estado_id = es.id
            GROUP BY es.id, es.estado_solicitud
            ORDER BY es.id
        """);
    }

    public List<ConteoDto> trabajosPorTecnico() {
        return consultar("""
            SELECT tecnico_nombre AS etiqueta, COUNT(*) AS total
            FROM reporte_trabajo
            GROUP BY tecnico_nombre
            ORDER BY total DESC
        """);
    }

    public List<ConteoDto> reportesPorEstadoFinal() {
        return consultar("""
            SELECT estado_final AS etiqueta, COUNT(*) AS total
            FROM reporte_trabajo
            GROUP BY estado_final
            ORDER BY total DESC
        """);
    }

    public List<ConteoDto> solicitudesPorCategoria() {
        return consultar("""
            SELECT categoria AS etiqueta, COUNT(*) AS total
            FROM solicitud
            GROUP BY categoria
            ORDER BY total DESC
        """);
    }

    public List<ConteoDto> solicitudesPorArea() {
        return consultar("""
            SELECT area AS etiqueta, COUNT(*) AS total
            FROM solicitud
            GROUP BY area
            ORDER BY total DESC
        """);
    }

    public List<ConteoDto> verificacionUsuarios() {
        return consultar("""
            SELECT CASE verificado_por_usuario
                       WHEN TRUE  THEN 'Verificado'
                       WHEN FALSE THEN 'No verificado'
                       ELSE 'Sin respuesta'
                   END AS etiqueta,
                   COUNT(*) AS total
            FROM solicitud
            GROUP BY verificado_por_usuario
            ORDER BY total DESC
        """);
    }
}