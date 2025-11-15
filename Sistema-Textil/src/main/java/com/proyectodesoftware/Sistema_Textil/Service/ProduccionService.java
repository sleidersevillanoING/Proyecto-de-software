package com.proyectodesoftware.Sistema_Textil.Service;

import com.proyectodesoftware.Sistema_Textil.Repository.ProduccionRepository;
import com.proyectodesoftware.Sistema_Textil.Repository.ProduccionHoraRepository;
import com.proyectodesoftware.Sistema_Textil.entities.Produccion;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ProduccionService {

    private final ProduccionRepository produccionRepository;
    private final ProduccionHoraRepository produccionHoraRepository; // ✅ AGREGADO

    // ✅ CONSTRUCTOR CORREGIDO - Agregar ProduccionHoraRepository
    public ProduccionService(ProduccionRepository produccionRepository, 
                           ProduccionHoraRepository produccionHoraRepository) {
        this.produccionRepository = produccionRepository;
        this.produccionHoraRepository = produccionHoraRepository; // ✅ INICIALIZADO
    }

    public List<Produccion> listarTodas() {
        return produccionRepository.findAll();
    }

    public Optional<Produccion> buscarPorId(Long id) {
        return produccionRepository.findById(id);
    }

    public Optional<Produccion> findByModuloAndFecha(Long moduloId, LocalDate fecha) {
        return produccionRepository.findByModuloIdAndFecha(moduloId, fecha);
    }

    public List<Produccion> buscarPorFecha(LocalDate fecha) {
        return produccionRepository.findByFecha(fecha);
    }

    public Produccion guardar(Produccion produccion) {
        return produccionRepository.save(produccion);
    }

    public void eliminar(Long id) {
        produccionRepository.deleteById(id);
    }
    
    // ✅ MÉTODOS DE ESTADÍSTICAS
    
    public Object[] obtenerEstadisticasPorModulo(LocalDate fechaInicio, LocalDate fechaFin, Long moduloId) {
        return produccionRepository.obtenerEstadisticasProduccionPorModulo(fechaInicio, fechaFin, moduloId);
    }
    
    public Object[] obtenerEstadisticasProduccion(LocalDate fechaInicio, LocalDate fechaFin) {
        return produccionRepository.obtenerEstadisticasProduccion(fechaInicio, fechaFin);
    }
    
    public List<Object[]> obtenerEstadisticasPorDia(LocalDate fechaInicio, LocalDate fechaFin) {
        return produccionRepository.obtenerEstadisticasPorDia(fechaInicio, fechaFin);
    }
    
    public List<Object[]> obtenerEstadisticasResumenPorModulo(LocalDate fechaInicio, LocalDate fechaFin) {
        return produccionRepository.obtenerEstadisticasPorModulo(fechaInicio, fechaFin);
    }

    // ✅ MÉTODO PARA ACTUALIZAR TOTALES AUTOMÁTICAMENTE
    public void actualizarTotalesProduccion(Long produccionId) {
        Optional<Produccion> produccionOpt = produccionRepository.findById(produccionId);
        if (produccionOpt.isPresent()) {
            Produccion produccion = produccionOpt.get();
            
            // Calcular totales desde ProduccionHora
            Integer totalCantidad = produccionHoraRepository.sumCantidadByProduccionId(produccionId);
            Integer totalDefectuosos = produccionHoraRepository.sumDefectuososByProduccionId(produccionId);
            
            // Si son null, establecer en 0
            totalCantidad = totalCantidad != null ? totalCantidad : 0;
            totalDefectuosos = totalDefectuosos != null ? totalDefectuosos : 0;
            
            produccion.setProduccionTotal(totalCantidad);
            produccion.setDefectuososTotal(totalDefectuosos);
            
            produccionRepository.save(produccion);
            System.out.println("✅ Totales actualizados - Producción ID: " + produccionId + 
                             ", Total: " + totalCantidad + ", Defectuosos: " + totalDefectuosos);
        }
    }

    // ✅ MÉTODO PARA CALCULAR EFICIENCIA
    public double calcularEficiencia(Integer total, Integer defectuosos) {
        if (total == null || total == 0) return 0.0;
        return ((total - defectuosos) * 100.0) / total;
    }
}