package com.gobernacionSIT.sit_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "aviso")
@Getter
@Setter
public class Aviso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_supervisor", nullable = false)
    private Usuario supervisor;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String destinatario;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
