package com.proyectodesoftware.Sistema_Textil.Controller;


import com.proyectodesoftware.Sistema_Textil.Service.ReporteService;
import com.proyectodesoftware.Sistema_Textil.entities.Reporte;
import com.proyectodesoftware.Sistema_Textil.entities.Usuario;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reportes")
@CrossOrigin(origins = "*")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    // ✅ ENDPOINT CORREGIDO - Generar reporte automáticamente
    @PostMapping("/generar")
    public ResponseEntity<Reporte> generarReporte(
            @RequestParam LocalDate fechaInicio,
            @RequestParam LocalDate fechaFin,
            @RequestParam Long usuarioId) {
        
        // Crear usuario temporal
        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        
        // Verificar si ya existe un reporte para este rango
        Optional<Reporte> reporteExistente = reporteService.obtenerReporteExistente(fechaInicio, fechaFin);
        
        if (reporteExistente.isPresent()) {
            return ResponseEntity.ok(reporteExistente.get());
        }
        
        // Generar nuevo reporte
        Reporte reporte = reporteService.generarReporte(fechaInicio, fechaFin, usuario);
        return ResponseEntity.ok(reporte);
    }

    // ✅ NUEVO ENDPOINT - Generar reporte por MÓDULO
    @PostMapping("/generar-por-modulo")
    public ResponseEntity<Reporte> generarReportePorModulo(
            @RequestParam LocalDate fechaInicio,
            @RequestParam LocalDate fechaFin,
            @RequestParam Long moduloId,
            @RequestParam Long usuarioId) {
        
        // Crear usuario temporal
        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        
        // Generar reporte del módulo específico
        Reporte reporte = reporteService.generarReportePorModulo(fechaInicio, fechaFin, moduloId, usuario);
        return ResponseEntity.ok(reporte);
    }

    @GetMapping
    public ResponseEntity<List<Reporte>> listarReportes() {
        return ResponseEntity.ok(reporteService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reporte> obtenerReporte(@PathVariable Long id) {
        Optional<Reporte> reporte = reporteService.buscarPorId(id);
        return reporte.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Reporte> crearReporte(@RequestBody Reporte reporte) {
        return ResponseEntity.ok(reporteService.guardar(reporte));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarReporte(@PathVariable Long id) {
        reporteService.eliminar(id);
        return ResponseEntity.ok().build();
    }
}