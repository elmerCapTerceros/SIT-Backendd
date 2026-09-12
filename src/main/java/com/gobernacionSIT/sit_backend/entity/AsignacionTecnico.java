package com.gobernacionSIT.sit_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "asignacion_tecnico", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"solicitud_id", "tecnico_id"})
})
@Getter
@Setter
public class AsignacionTecnico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitud_id", nullable = false)
    private Solicitud solicitud;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tecnico_id", nullable = false)
    private Usuario tecnico;

    @Column(name = "asignado_at", nullable = false, updatable = false)
    private LocalDateTime asignadoAt = LocalDateTime.now();
}