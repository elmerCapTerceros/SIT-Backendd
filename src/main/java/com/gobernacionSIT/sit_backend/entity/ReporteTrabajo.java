package com.gobernacionSIT.sit_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "reporte_trabajo")
@Getter
@Setter
public class ReporteTrabajo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitud_id", nullable = false)
    private Solicitud solicitud;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asignacion_tecnico_id", nullable = false)
    private AsignacionTecnico asignacionTecnico;

    @Column(name = "tipo_equipo", length = 150)
    private String tipoEquipo;

    @Column(columnDefinition = "TEXT")
    private String diagnostico;

    @Column(name = "trabajo_realizado", columnDefinition = "TEXT")
    private String trabajoRealizado;

    @Column(name = "estado_final", length = 30)
    private String estadoFinal;

    @Column(columnDefinition = "TEXT")
    private String observacion;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
