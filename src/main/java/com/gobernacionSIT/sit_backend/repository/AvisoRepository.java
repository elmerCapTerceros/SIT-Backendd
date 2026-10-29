package com.gobernacionSIT.sit_backend.repository;

import com.gobernacionSIT.sit_backend.entity.Aviso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AvisoRepository extends JpaRepository<Aviso, Long> {
    List<Aviso> findByActivoOrderByCreatedAtDesc(boolean activo);

    List<Aviso> findByDestinatarioInAndActivoOrderByCreatedAtDesc(
            List<String> destinatarios, boolean activo);

    List<Aviso> findBySupervisorIdOrderByCreatedAtDesc(Long supervisorId);

    @Modifying
    @Query("UPDATE Aviso a SET a.activo = :activo WHERE a.id = :id")
    int cambiarActivo(@Param("id") Long id, @Param("activo") boolean activo);
}
