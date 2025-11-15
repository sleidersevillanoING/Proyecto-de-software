package com.proyectodesoftware.Sistema_Textil.Repository;

import com.proyectodesoftware.Sistema_Textil.entities.Modulo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ModuloRepository extends JpaRepository<Modulo, Long> {
    
    // ✅ Verificar si existe por número de módulo
    boolean existsByNumeroModulo(Integer numeroModulo);
    
    // ✅ Buscar por número de módulo
    Optional<Modulo> findByNumeroModulo(Integer numeroModulo);
    
}