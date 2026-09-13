package com.gobernacionSIT.sit_backend.repository;

import com.gobernacionSIT.sit_backend.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Query("select u from Usuario u join fetch u.rol where u.userLogin = :userLogin")
    Optional<Usuario> findByUserLogin(String userLogin);

    boolean existsByUserLogin(String userLogin);

    List<Usuario> findByRol_NombreRol(String nombreRol);

    List<Usuario> findByArea(String area);

}
