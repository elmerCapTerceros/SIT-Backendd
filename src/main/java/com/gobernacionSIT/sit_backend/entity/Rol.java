package com.gobernacionSIT.sit_backend.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "rol")
@Getter
@Setter

public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_rol", nullable = false, unique = true, length = 20)
    private String nombreRol;
}
