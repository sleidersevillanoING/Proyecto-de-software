package com.proyectodesoftware.Sistema_Textil.Controller;

import com.proyectodesoftware.Sistema_Textil.Service.ProduccionHoraService;
import com.proyectodesoftware.Sistema_Textil.entities.ProduccionHora;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produccion-hora")
@CrossOrigin(origins = "*")
public class ProduccionHoraController {

    private final ProduccionHoraService produccionHoraService;

    public ProduccionHoraController(ProduccionHoraService produccionHoraService) {
        this.produccionHoraService = produccionHoraService;
    }

    @GetMapping("/produccion/{idProduccion}")
    public ResponseEntity<List<ProduccionHora>> listarPorProduccion(@PathVariable Long idProduccion) {
        return ResponseEntity.ok(produccionHoraService.listarPorProduccion(idProduccion));
    }
    @GetMapping
    public ResponseEntity<List<ProduccionHora>> listarTodas() {
        return ResponseEntity.ok(produccionHoraService.listarTodas());
    }
    @PostMapping
    public ResponseEntity<ProduccionHora> crearRegistroHora(@RequestBody ProduccionHora produccionHora) {
        return ResponseEntity.ok(produccionHoraService.guardar(produccionHora));
    }
}
