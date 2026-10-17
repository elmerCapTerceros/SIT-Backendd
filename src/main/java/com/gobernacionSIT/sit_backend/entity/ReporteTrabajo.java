package com.gobernacionSIT.sit_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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

    @Column(name = "tipo_equipo_otro", length = 150)
    private String tipoEquipoOtro;

    @Column(name = "numero_formulario", length = 50)
    private String numeroFormulario;

    @Column(name = "fecha_atencion")
    private LocalDate fechaAtencion;

    @Column(name = "hora_atencion")
    private LocalTime horaAtencion;

    @Column(length = 150)
    private String unidad;

    @Column(length = 200)
    private String solicitante;

    @Column(name = "numero_inventario", length = 100)
    private String numeroInventario;

    @Column(columnDefinition = "TEXT")
    private String problema;

    @Column(columnDefinition = "TEXT")
    private String diagnostico;

    @Column(name = "trabajo_realizado", columnDefinition = "TEXT")
    private String trabajoRealizado;

    @Column(name = "estado_final", length = 30)
    private String estadoFinal;

    @Column(columnDefinition = "TEXT")
    private String observacion;

    @Column(name = "tecnico_nombre", length = 200)
    private String tecnicoNombre;

    @Column(name = "funcionario_nombre", length = 200)
    private String funcionarioNombre;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
