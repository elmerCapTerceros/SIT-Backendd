package com.gobernacionSIT.sit_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuario")
@Getter
@Setter
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String nombre;

    @Column(length = 100)
    private String apellido;

    @Column(name = "user_login", nullable = false, unique = true, length = 40)
    private String userLogin;

    @Column(length = 30)
    private String password;

    @Column(length = 20)
    private String cargo;

    @Column(length = 8)
    private String telefono;

    @Column(length = 40)
    private String area;

    @Column(name = "ubicacion_oficina", length = 40)
    private String ubicacionOficina;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}