package com.proyectodesoftware.Sistema_Textil.Repository;

import com.proyectodesoftware.Sistema_Textil.entities.Produccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProduccionRepository extends JpaRepository<Produccion, Long> {
    
    // ✅ Buscar producción por módulo y fecha
    @Query("SELECT p FROM Produccion p WHERE p.modulo.id = :moduloId AND p.fecha = :fecha")
    Optional<Produccion> findByModuloIdAndFecha(@Param("moduloId") Long moduloId, @Param("fecha") LocalDate fecha);
    
    // ✅ Buscar producciones por fecha
    List<Produccion> findByFecha(LocalDate fecha);
    
    // ✅ Buscar producciones por módulo
    List<Produccion> findByModuloId(Long moduloId);
    
    // ✅ ESTADÍSTICAS POR MÓDULO Y RANGO DE FECHAS
    @Query("SELECT " +
           "SUM(p.produccionTotal) as totalProducido, " +
           "SUM(p.defectuososTotal) as totalDefectuosos, " +
           "AVG(p.produccionTotal) as promedioProduccion, " +
           "COUNT(p) as totalDiasProduccion, " +
           "(SUM(p.produccionTotal) - SUM(p.defectuososTotal)) * 100.0 / SUM(p.produccionTotal) as eficienciaGeneral " +
           "FROM Produccion p " +
           "WHERE p.fecha BETWEEN :fechaInicio AND :fechaFin AND p.modulo.id = :moduloId")
    Object[] obtenerEstadisticasProduccionPorModulo(@Param("fechaInicio") LocalDate fechaInicio, 
                                                   @Param("fechaFin") LocalDate fechaFin, 
                                                   @Param("moduloId") Long moduloId);
    
    // ✅ ESTADÍSTICAS GENERALES (TODOS LOS MÓDULOS)
    @Query("SELECT " +
           "SUM(p.produccionTotal) as totalProducido, " +
           "SUM(p.defectuososTotal) as totalDefectuosos, " +
           "AVG(p.produccionTotal) as promedioProduccion, " +
           "COUNT(p) as totalDiasProduccion, " +
           "COUNT(DISTINCT p.modulo.id) as totalModulos, " +
           "(SUM(p.produccionTotal) - SUM(p.defectuososTotal)) * 100.0 / SUM(p.produccionTotal) as eficienciaGeneral " +
           "FROM Produccion p " +
           "WHERE p.fecha BETWEEN :fechaInicio AND :fechaFin")
    Object[] obtenerEstadisticasProduccion(@Param("fechaInicio") LocalDate fechaInicio, 
                                          @Param("fechaFin") LocalDate fechaFin);
    
    // ✅ ESTADÍSTICAS POR DÍA
    @Query("SELECT p.fecha, " +
           "SUM(p.produccionTotal) as produccionDia, " +
           "SUM(p.defectuososTotal) as defectuososDia, " +
           "COUNT(DISTINCT p.modulo.id) as modulosActivos " +
           "FROM Produccion p " +
           "WHERE p.fecha BETWEEN :fechaInicio AND :fechaFin " +
           "GROUP BY p.fecha " +
           "ORDER BY p.fecha")
    List<Object[]> obtenerEstadisticasPorDia(@Param("fechaInicio") LocalDate fechaInicio, 
                                            @Param("fechaFin") LocalDate fechaFin);
    
    // ✅ ESTADÍSTICAS POR MÓDULO (RESUMEN)
    @Query("SELECT m.id, m.nombreModulo, " +
           "SUM(p.produccionTotal) as totalProducido, " +
           "SUM(p.defectuososTotal) as totalDefectuosos, " +
           "AVG(p.produccionTotal) as promedioDiario, " +
           "(SUM(p.produccionTotal) - SUM(p.defectuososTotal)) * 100.0 / SUM(p.produccionTotal) as eficiencia " +
           "FROM Produccion p JOIN p.modulo m " +
           "WHERE p.fecha BETWEEN :fechaInicio AND :fechaFin " +
           "GROUP BY m.id, m.nombreModulo " +
           "ORDER BY totalProducido DESC")
    List<Object[]> obtenerEstadisticasPorModulo(@Param("fechaInicio") LocalDate fechaInicio, 
                                               @Param("fechaFin") LocalDate fechaFin);
                                               // En ProduccionHoraRepository - AGREGAR ESTOS MÉTODOS
@Query("SELECT SUM(ph.cantidad) FROM ProduccionHora ph WHERE ph.produccion.id = :produccionId")
Integer sumCantidadByProduccionId(@Param("produccionId") Long produccionId);

@Query("SELECT SUM(ph.defectuosos) FROM ProduccionHora ph WHERE ph.produccion.id = :produccionId")
Integer sumDefectuososByProduccionId(@Param("produccionId") Long produccionId);

@Query("SELECT COUNT(ph) FROM ProduccionHora ph WHERE ph.produccion.id = :produccionId")
Integer countByProduccionId(@Param("produccionId") Long produccionId);
}