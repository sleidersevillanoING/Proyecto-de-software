package com.proyectodesoftware.Sistema_Textil.Repository;

import com.proyectodesoftware.Sistema_Textil.entities.Reporte;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ReporteRepository extends JpaRepository<Reporte, Long> {
    
    // ✅ Buscar reporte por rango de fechas
    Optional<Reporte> findByFechaInicioAndFechaFin(LocalDate fechaInicio, LocalDate fechaFin);
  
}