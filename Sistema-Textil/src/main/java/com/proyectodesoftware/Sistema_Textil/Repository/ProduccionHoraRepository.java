package com.proyectodesoftware.Sistema_Textil.Repository;

import com.proyectodesoftware.Sistema_Textil.entities.ProduccionHora;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProduccionHoraRepository extends JpaRepository<ProduccionHora, Long> {
    
    List<ProduccionHora> findByProduccionId(Long produccionId);
    
    // ✅ MÉTODOS PARA CALCULAR TOTALES
    @Query("SELECT SUM(ph.cantidad) FROM ProduccionHora ph WHERE ph.produccion.id = :produccionId")
    Integer sumCantidadByProduccionId(@Param("produccionId") Long produccionId);
    
    @Query("SELECT SUM(ph.defectuosos) FROM ProduccionHora ph WHERE ph.produccion.id = :produccionId")
    Integer sumDefectuososByProduccionId(@Param("produccionId") Long produccionId);
    
    @Query("SELECT COUNT(ph) FROM ProduccionHora ph WHERE ph.produccion.id = :produccionId")
    Integer countByProduccionId(@Param("produccionId") Long produccionId);
    
    Optional<ProduccionHora> findByProduccionIdAndHora(Long produccionId, LocalTime hora);

    // ✅ MÉTODOS PARA ESTADÍSTICAS
    @Query("SELECT COUNT(ph), COALESCE(SUM(ph.cantidad), 0) " +
           "FROM ProduccionHora ph " +
           "JOIN ph.produccion p " +
           "WHERE p.fecha BETWEEN :fechaInicio AND :fechaFin")
    Object[] obtenerEstadisticasProduccionHora(@Param("fechaInicio") LocalDate fechaInicio, 
                                              @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT COUNT(ph), COALESCE(SUM(ph.cantidad), 0) " +
           "FROM ProduccionHora ph " +
           "JOIN ph.produccion p " +
           "WHERE p.fecha BETWEEN :fechaInicio AND :fechaFin " +
           "AND p.modulo.id = :moduloId")
    Object[] obtenerEstadisticasProduccionHoraPorModulo(@Param("fechaInicio") LocalDate fechaInicio, 
                                                       @Param("fechaFin") LocalDate fechaFin,
                                                       @Param("moduloId") Long moduloId);
}