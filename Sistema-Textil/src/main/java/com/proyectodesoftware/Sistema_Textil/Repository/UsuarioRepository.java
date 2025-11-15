package com.proyectodesoftware.Sistema_Textil.Repository;

import com.proyectodesoftware.Sistema_Textil.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    // ✅ BUSCAR POR USERNAME - QUITAR "static"
    Optional<Usuario> findByUsername(String username);

    // ✅ BUSCAR POR ROL
    List<Usuario> findByRol(String rol);

    // ✅ BUSCAR POR NOMBRE COMPLETO
    List<Usuario> findByNombreCompletoContainingIgnoreCase(String nombre);

    // ✅ VERIFICAR SI EXISTE POR USERNAME
    boolean existsByUsername(String username);

    // ✅ BUSCAR USUARIOS ACTIVOS
    List<Usuario> findByActivoTrue();

    // ✅ BUSCAR POR ROL Y ESTADO ACTIVO
    List<Usuario> findByRolAndActivoTrue(String rol);

    // ✅ QUERY PERSONALIZADA - Login con username y password
    @Query("SELECT u FROM Usuario u WHERE u.username = :username AND u.password = :password AND u.activo = true")
    Optional<Usuario> findByUsernameAndPassword(@Param("username") String username, 
                                               @Param("password") String password);

    // ✅ QUERY PERSONALIZADA - Buscar usuarios por parte del nombre
    @Query("SELECT u FROM Usuario u WHERE LOWER(u.nombreCompleto) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Usuario> buscarPorNombre(@Param("nombre") String nombre);

    // ✅ QUERY PERSONALIZADA - Buscar por rol y que estén activos
    @Query("SELECT u FROM Usuario u WHERE u.rol = :rol AND u.activo = true")
    List<Usuario> findActivosByRol(@Param("rol") String rol);
}