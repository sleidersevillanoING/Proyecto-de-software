package com.proyectodesoftware.Sistema_Textil.Controller;

import com.proyectodesoftware.Sistema_Textil.Service.UsuarioService;
import com.proyectodesoftware.Sistema_Textil.entities.Usuario;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.listarUsuarios());
    }

    @PostMapping
    public ResponseEntity<?> crearUsuario(@RequestBody Usuario usuario) {
        try {
            Usuario usuarioGuardado = usuarioService.guardar(usuario);
            return ResponseEntity.ok(usuarioGuardado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error creando usuario");
        }
    }

    @GetMapping("/{username}")
    public ResponseEntity<Usuario> buscarPorUsername(@PathVariable String username) {
        Optional<Usuario> usuario = usuarioService.buscarPorUsername(username);
        return usuario.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String username, @RequestParam String password) {
        boolean valido = usuarioService.validarCredenciales(username, password);
        return valido
                ? ResponseEntity.ok("✅ Login exitoso")
                : ResponseEntity.status(401).body("❌ Credenciales incorrectas");
    }
}