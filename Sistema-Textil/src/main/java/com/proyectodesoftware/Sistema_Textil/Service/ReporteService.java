 package com.proyectodesoftware.Sistema_Textil.Service;

import com.proyectodesoftware.Sistema_Textil.Repository.ProduccionRepository;
import com.proyectodesoftware.Sistema_Textil.Repository.ProduccionHoraRepository;
import com.proyectodesoftware.Sistema_Textil.Repository.ReporteRepository;
import com.proyectodesoftware.Sistema_Textil.entities.Reporte;
import com.proyectodesoftware.Sistema_Textil.entities.Usuario;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ReporteService {

    private final ReporteRepository reporteRepository;
    private final ProduccionRepository produccionRepository;
    private final ProduccionHoraRepository produccionHoraRepository;

    public ReporteService(ReporteRepository reporteRepository, 
                         ProduccionRepository produccionRepository,
                         ProduccionHoraRepository produccionHoraRepository) {
        this.reporteRepository = reporteRepository;
        this.produccionRepository = produccionRepository;
        this.produccionHoraRepository = produccionHoraRepository;
    }

    // ✅ MÉTODO CORREGIDO - Generar reporte automáticamente desde producciones
    public Reporte generarReporte(LocalDate fechaInicio, LocalDate fechaFin, Usuario usuario) {
        
        // 1. Obtener estadísticas de producciones
        Object[] statsProduccion = produccionRepository.obtenerEstadisticasProduccion(fechaInicio, fechaFin);
        
        Integer totalProduccion = 0;
        Integer totalDefectuosos = 0;
        
        if (statsProduccion != null && statsProduccion.length == 2 && statsProduccion[0] != null) {
            totalProduccion = ((Number) statsProduccion[0]).intValue();
            totalDefectuosos = ((Number) statsProduccion[1]).intValue();
        }

        // 2. Calcular tiempo promedio por hora
        BigDecimal tiempoPromedioHora = calcularTiempoPromedioHora(fechaInicio, fechaFin);

        // 3. Crear y guardar el reporte
        Reporte reporte = new Reporte();
        reporte.setFechaInicio(fechaInicio);
        reporte.setFechaFin(fechaFin);
        reporte.setTotalProduccion(totalProduccion);
        reporte.setTotalDefectuosos(totalDefectuosos);
        reporte.setTiempoPromedioHora(tiempoPromedioHora);
        reporte.setGeneradoPor(usuario);

        return reporteRepository.save(reporte);
    }

    // ✅ MÉTODO CORREGIDO - Calcular tiempo promedio por hora
    private BigDecimal calcularTiempoPromedioHora(LocalDate fechaInicio, LocalDate fechaFin) {
        // Obtener estadísticas de producción por hora
        Object[] statsHora = produccionHoraRepository.obtenerEstadisticasProduccionHora(fechaInicio, fechaFin);
        
        if (statsHora != null && statsHora.length == 2 && statsHora[0] != null && statsHora[1] != null) {
            Long totalHoras = ((Number) statsHora[0]).longValue();
            Long totalProduccionHora = ((Number) statsHora[1]).longValue();
            
            if (totalHoras > 0 && totalProduccionHora > 0) {
                // Calcular promedio: producción total / número de horas registradas
                return BigDecimal.valueOf(totalProduccionHora)
                    .divide(BigDecimal.valueOf(totalHoras), 2, RoundingMode.HALF_UP);
            }
        }
        
        return BigDecimal.ZERO;
    }

    // ✅ NUEVO MÉTODO - Generar reporte por MÓDULO específico
    public Reporte generarReportePorModulo(LocalDate fechaInicio, LocalDate fechaFin, Long moduloId, Usuario usuario) {
        
        // 1. Obtener estadísticas de producciones DEL MÓDULO ESPECÍFICO
        Object[] statsProduccion = produccionRepository.obtenerEstadisticasProduccionPorModulo(fechaInicio, fechaFin, moduloId);
        
        Integer totalProduccion = 0;
        Integer totalDefectuosos = 0;
        
        if (statsProduccion != null && statsProduccion.length == 2 && statsProduccion[0] != null) {
            totalProduccion = ((Number) statsProduccion[0]).intValue();
            totalDefectuosos = ((Number) statsProduccion[1]).intValue();
        }

        // 2. Calcular tiempo promedio por hora DEL MÓDULO ESPECÍFICO
        BigDecimal tiempoPromedioHora = calcularTiempoPromedioHoraPorModulo(fechaInicio, fechaFin, moduloId);

        // 3. Crear y guardar el reporte
        Reporte reporte = new Reporte();
        reporte.setFechaInicio(fechaInicio);
        reporte.setFechaFin(fechaFin);
        reporte.setTotalProduccion(totalProduccion);
        reporte.setTotalDefectuosos(totalDefectuosos);
        reporte.setTiempoPromedioHora(tiempoPromedioHora);
        reporte.setGeneradoPor(usuario);

        return reporteRepository.save(reporte);
    }

    // ✅ NUEVO MÉTODO - Calcular tiempo promedio por hora POR MÓDULO
    private BigDecimal calcularTiempoPromedioHoraPorModulo(LocalDate fechaInicio, LocalDate fechaFin, Long moduloId) {
        Object[] statsHora = produccionHoraRepository.obtenerEstadisticasProduccionHoraPorModulo(fechaInicio, fechaFin, moduloId);
        
        if (statsHora != null && statsHora.length == 2 && statsHora[0] != null && statsHora[1] != null) {
            Long totalHoras = ((Number) statsHora[0]).longValue();
            Long totalProduccionHora = ((Number) statsHora[1]).longValue();
            
            if (totalHoras > 0 && totalProduccionHora > 0) {
                return BigDecimal.valueOf(totalProduccionHora)
                    .divide(BigDecimal.valueOf(totalHoras), 2, RoundingMode.HALF_UP);
            }
        }
        
        return BigDecimal.ZERO;
    }

    // ✅ Método para obtener reporte por rango de fechas (si ya existe)
    public Optional<Reporte> obtenerReporteExistente(LocalDate fechaInicio, LocalDate fechaFin) {
        return reporteRepository.findByFechaInicioAndFechaFin(fechaInicio, fechaFin);
    }

    public Reporte guardar(Reporte reporte) {
        return reporteRepository.save(reporte);
    }

    public List<Reporte> listar() {
        return reporteRepository.findAll();
    }

    public Optional<Reporte> buscarPorId(Long id) {
        return reporteRepository.findById(id);
    }

    public void eliminar(Long id) {
        reporteRepository.deleteById(id);
    }
}