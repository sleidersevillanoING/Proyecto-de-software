package com.proyectodesoftware.Sistema_Textil.Controller;

import com.proyectodesoftware.Sistema_Textil.Service.ProduccionService;
import com.proyectodesoftware.Sistema_Textil.entities.Produccion;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/produccion")
@CrossOrigin(origins = "*")
public class ProduccionController {

    private final ProduccionService produccionService;

    public ProduccionController(ProduccionService produccionService) {
        this.produccionService = produccionService;
    }

    @GetMapping
    public ResponseEntity<List<Produccion>> listar() {
        return ResponseEntity.ok(produccionService.listarTodas());
    }

    @GetMapping("/fecha/{fecha}")
    public ResponseEntity<List<Produccion>> buscarPorFecha(@PathVariable String fecha) {
        LocalDate date = LocalDate.parse(fecha);
        return ResponseEntity.ok(produccionService.buscarPorFecha(date));
    }

    @PostMapping
    public ResponseEntity<Produccion> crearProduccion(@RequestBody Produccion produccion) {
        return ResponseEntity.ok(produccionService.guardar(produccion));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        produccionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
    
    // ✅ NUEVOS ENDPOINTS PARA ESTADÍSTICAS
    
    @GetMapping("/estadisticas")
    public ResponseEntity<Object[]> obtenerEstadisticas(
            @RequestParam LocalDate fechaInicio,
            @RequestParam LocalDate fechaFin) {
        return ResponseEntity.ok(produccionService.obtenerEstadisticasProduccion(fechaInicio, fechaFin));
    }
    
    @GetMapping("/estadisticas/modulo/{moduloId}")
    public ResponseEntity<Object[]> obtenerEstadisticasPorModulo(
            @PathVariable Long moduloId,
            @RequestParam LocalDate fechaInicio,
            @RequestParam LocalDate fechaFin) {
        return ResponseEntity.ok(produccionService.obtenerEstadisticasPorModulo(fechaInicio, fechaFin, moduloId));
    }
    
    @GetMapping("/estadisticas/diarias")
    public ResponseEntity<List<Object[]>> obtenerEstadisticasDiarias(
            @RequestParam LocalDate fechaInicio,
            @RequestParam LocalDate fechaFin) {
        return ResponseEntity.ok(produccionService.obtenerEstadisticasPorDia(fechaInicio, fechaFin));
    }
    
    @GetMapping("/estadisticas/resumen-modulos")
    public ResponseEntity<List<Object[]>> obtenerResumenPorModulos(
            @RequestParam LocalDate fechaInicio,
            @RequestParam LocalDate fechaFin) {
        return ResponseEntity.ok(produccionService.obtenerEstadisticasResumenPorModulo(fechaInicio, fechaFin));
    }
}