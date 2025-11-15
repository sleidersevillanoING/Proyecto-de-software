package com.proyectodesoftware.Sistema_Textil.Service;

import com.proyectodesoftware.Sistema_Textil.Repository.UsuarioRepository;
import com.proyectodesoftware.Sistema_Textil.entities.Usuario;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // ✅ MÉTODO PRINCIPAL PARA LOGIN
    public Optional<Usuario> validarYBuscarUsuario(String username, String password) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
        
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            // Validar que esté activo y la contraseña coincida
            if (usuario.getActivo() && usuario.getPassword().equals(password)) {
                return usuarioOpt;
            }
        }
        return Optional.empty();
    }

    // ✅ MÉTODO ALTERNATIVO PARA LOGIN (más eficiente)
    public boolean validarCredenciales(String username, String password) {
        return usuarioRepository.findByUsernameAndPassword(username, password).isPresent();
    }

    // ✅ LISTAR TODOS LOS USUARIOS
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    // ✅ LISTAR USUARIOS ACTIVOS
    public List<Usuario> listarUsuariosActivos() {
        return usuarioRepository.findByActivoTrue();
    }

    // ✅ GUARDAR USUARIO - CORREGIDO
    public Usuario guardar(Usuario usuario) {
        try {
            // Validaciones antes de guardar
            if (usuario.getUsername() == null || usuario.getUsername().trim().isEmpty()) {
                throw new IllegalArgumentException("El username no puede estar vacío");
            }
            if (usuario.getPassword() == null || usuario.getPassword().trim().isEmpty()) {
                throw new IllegalArgumentException("La contraseña no puede estar vacía");
            }
            if (usuario.getRol() == null || usuario.getRol().trim().isEmpty()) {
                throw new IllegalArgumentException("El rol no puede estar vacío");
            }
            
            // Verificar duplicados si es nuevo usuario
            if (usuario.getId() == null) {
                if (usuarioRepository.existsByUsername(usuario.getUsername())) {
                    throw new IllegalArgumentException("El usuario ya existe");
                }
            }
            
            // Establecer valores por defecto
            if (usuario.getActivo() == null) {
                usuario.setActivo(true);
            }
            
            return usuarioRepository.save(usuario);
            
        } catch (Exception e) {
            System.out.println("💥 Error guardando usuario: " + e.getMessage());
            throw e;
        }
    }

    // ✅ BUSCAR POR ID
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    // ✅ BUSCAR POR USERNAME - CORREGIDO
    public Optional<Usuario> buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    // ✅ VERIFICAR SI EXISTE USERNAME
    public boolean existeUsername(String username) {
        return usuarioRepository.existsByUsername(username);
    }

    // ✅ BUSCAR POR ROL
    public List<Usuario> buscarPorRol(String rol) {
        return usuarioRepository.findByRol(rol);
    }

    // ✅ BUSCAR POR NOMBRE
    public List<Usuario> buscarPorNombre(String nombre) {
        return usuarioRepository.findByNombreCompletoContainingIgnoreCase(nombre);
    }

    // ✅ BUSCAR SUPERVISORES ACTIVOS
    public List<Usuario> buscarSupervisoresActivos() {
        return usuarioRepository.findByRolAndActivoTrue("SUPERVISOR");
    }

    // ✅ BUSCAR ADMINS ACTIVOS
    public List<Usuario> buscarAdminsActivos() {
        return usuarioRepository.findByRolAndActivoTrue("ADMIN");
    }

    // ✅ DESACTIVAR USUARIO (eliminación lógica)
    public void desactivarUsuario(Long id) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            usuario.setActivo(false);
            usuarioRepository.save(usuario);
        }
    }

    // ✅ ACTIVAR USUARIO
    public void activarUsuario(Long id) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            usuario.setActivo(true);
            usuarioRepository.save(usuario);
        }
    }

    // ✅ ELIMINACIÓN FÍSICA (solo si es necesario)
    public void eliminar(Long id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
        }
    }

    // ✅ MÉTODOS ÚTILES PARA VERIFICACIONES
    public boolean esAdmin(String username) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
        return usuarioOpt.map(usuario -> "ADMIN".equals(usuario.getRol())).orElse(false);
    }

    public boolean esSupervisor(String username) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
        return usuarioOpt.map(usuario -> "SUPERVISOR".equals(usuario.getRol())).orElse(false);
    }

    // ✅ CONTAR USUARIOS
    public long contarUsuarios() {
        return usuarioRepository.count();
    }

    // ✅ CONTAR USUARIOS ACTIVOS
    public long contarUsuariosActivos() {
        return usuarioRepository.findByActivoTrue().size();
    }
}