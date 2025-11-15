package com.proyectodesoftware.Sistema_Textil.Controller;

import com.proyectodesoftware.Sistema_Textil.Service.ModuloService;
import com.proyectodesoftware.Sistema_Textil.entities.Modulo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/modulos")
@CrossOrigin(origins = "*")
public class ModuloController {

    private final ModuloService moduloService;

    public ModuloController(ModuloService moduloService) {
        this.moduloService = moduloService;
    }

    // ✅ Listar todos los módulos
    @GetMapping
    public ResponseEntity<List<Modulo>> listar() {
        List<Modulo> modulos = moduloService.listar();
        return ResponseEntity.ok(modulos);
    }

    // ✅ Obtener un módulo por su ID
    @GetMapping("/{id}")
    public ResponseEntity<Modulo> obtenerPorId(@PathVariable Long id) {
        Optional<Modulo> modulo = moduloService.buscarPorId(id);
        return modulo.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    // ✅ CREAR UN NUEVO MÓDULO - CORREGIDO
    @PostMapping
    public ResponseEntity<?> crearModulo(@RequestBody Modulo modulo) {
        try {
            System.out.println("📦 Recibiendo solicitud para crear módulo: " + modulo.getNombreModulo());
            
            // Validaciones adicionales
            if (modulo.getNumeroModulo() == null) {
                return ResponseEntity.badRequest().body("❌ El número de módulo es obligatorio");
            }
            
            if (modulo.getNombreModulo() == null || modulo.getNombreModulo().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("❌ El nombre del módulo es obligatorio");
            }
            
            // Guardar el módulo
            Modulo nuevoModulo = moduloService.guardar(modulo);
            
            System.out.println("✅ Módulo creado exitosamente - ID: " + nuevoModulo.getId());
            return ResponseEntity.ok(nuevoModulo);
            
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Error de validación: " + e.getMessage());
            return ResponseEntity.badRequest().body("❌ " + e.getMessage());
        } catch (Exception e) {
            System.out.println("💥 Error interno creando módulo: " + e.getMessage());
            return ResponseEntity.internalServerError().body("💥 Error interno del servidor");
        }
    }

    // ✅ ACTUALIZAR MÓDULO EXISTENTE
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarModulo(@PathVariable Long id, @RequestBody Modulo modulo) {
        try {
            // Verificar que el módulo existe
            if (!moduloService.existePorId(id)) {
                return ResponseEntity.notFound().build();
            }
            
            // Establecer el ID del módulo a actualizar
            modulo.setId(id);
            
            // Guardar la actualización
            Modulo moduloActualizado = moduloService.guardar(modulo);
            
            System.out.println("✅ Módulo actualizado exitosamente - ID: " + id);
            return ResponseEntity.ok(moduloActualizado);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("❌ " + e.getMessage());
        } catch (Exception e) {
            System.out.println("💥 Error actualizando módulo: " + e.getMessage());
            return ResponseEntity.internalServerError().body("💥 Error interno del servidor");
        }
    }

    // ✅ Eliminar módulo por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            if (moduloService.buscarPorId(id).isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            moduloService.eliminar(id);
            return ResponseEntity.ok().body("✅ Módulo eliminado exitosamente");
            
        } catch (Exception e) {
            System.out.println("💥 Error eliminando módulo: " + e.getMessage());
            return ResponseEntity.internalServerError().body("💥 Error eliminando módulo");
        }
    }

    // ✅ ENDPOINT ADICIONAL: Buscar módulo por número
    @GetMapping("/numero/{numeroModulo}")
    public ResponseEntity<Modulo> buscarPorNumero(@PathVariable Integer numeroModulo) {
        Optional<Modulo> modulo = moduloService.buscarPorNumeroModulo(numeroModulo);
        return modulo.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    // ✅ ENDPOINT ADICIONAL: Contar módulos
    @GetMapping("/contar")
    public ResponseEntity<Long> contarModulos() {
        long total = moduloService.contarModulos();
        return ResponseEntity.ok(total);
    }
}