package com.proyectodesoftware.Sistema_Textil.Service;

import com.proyectodesoftware.Sistema_Textil.Repository.ModuloRepository;
import com.proyectodesoftware.Sistema_Textil.entities.Modulo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ModuloService {

    private final ModuloRepository moduloRepository;

    public ModuloService(ModuloRepository moduloRepository) {
        this.moduloRepository = moduloRepository;
    }

    // ✅ Listar todos los módulos
    public List<Modulo> listar() {
        return moduloRepository.findAll();
    }

    // ✅ Buscar módulo por ID
    public Optional<Modulo> buscarPorId(Long id) {
        return moduloRepository.findById(id);
    }

    // ✅ GUARDAR O ACTUALIZAR MÓDULO - CORREGIDO Y MEJORADO
    public Modulo guardar(Modulo modulo) {
        try {
            System.out.println("💾 Guardando módulo: " + modulo.getNombreModulo());
            
            // Validaciones antes de guardar
            if (modulo.getNumeroModulo() == null) {
                throw new IllegalArgumentException("El número de módulo es obligatorio");
            }
            
            if (modulo.getNombreModulo() == null || modulo.getNombreModulo().trim().isEmpty()) {
                throw new IllegalArgumentException("El nombre del módulo es obligatorio");
            }
            
            // Verificar duplicados solo si es un nuevo módulo (sin ID)
            if (modulo.getId() == null) {
                if (moduloRepository.existsByNumeroModulo(modulo.getNumeroModulo())) {
                    throw new IllegalArgumentException("Ya existe un módulo con el número: " + modulo.getNumeroModulo());
                }
            } else {
                // Si es una actualización, verificar que no haya conflicto con otros módulos
                Optional<Modulo> moduloExistente = moduloRepository.findByNumeroModulo(modulo.getNumeroModulo());
                if (moduloExistente.isPresent() && !moduloExistente.get().getId().equals(modulo.getId())) {
                    throw new IllegalArgumentException("Ya existe otro módulo con el número: " + modulo.getNumeroModulo());
                }
            }
            
            // Limpiar y formatear datos
            modulo.setNombreModulo(modulo.getNombreModulo().trim());
            if (modulo.getDescripcion() != null) {
                modulo.setDescripcion(modulo.getDescripcion().trim());
            }
            
            // Guardar en la base de datos
            Modulo moduloGuardado = moduloRepository.save(modulo);
            System.out.println("✅ Módulo guardado exitosamente - ID: " + moduloGuardado.getId());
            
            return moduloGuardado;
            
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Error de validación al guardar módulo: " + e.getMessage());
            throw e; // Relanzar para que el controlador pueda manejarlo
        } catch (Exception e) {
            System.out.println("💥 Error inesperado al guardar módulo: " + e.getMessage());
            throw new RuntimeException("Error al guardar el módulo: " + e.getMessage(), e);
        }
    }

    // ✅ Verificar si existe un número de módulo
    public boolean existePorNumeroModulo(Integer numeroModulo) {
        return moduloRepository.existsByNumeroModulo(numeroModulo);
    }

    // ✅ Buscar por número de módulo
    public Optional<Modulo> buscarPorNumeroModulo(Integer numeroModulo) {
        return moduloRepository.findByNumeroModulo(numeroModulo);
    }

    // ✅ Eliminar módulo por ID
    public void eliminar(Long id) {
        try {
            if (moduloRepository.existsById(id)) {
                moduloRepository.deleteById(id);
                System.out.println("✅ Módulo eliminado - ID: " + id);
            } else {
                throw new IllegalArgumentException("No existe un módulo con ID: " + id);
            }
        } catch (Exception e) {
            System.out.println("💥 Error eliminando módulo: " + e.getMessage());
            throw e;
        }
    }

    // ✅ CONTAR MÓDULOS
    public long contarModulos() {
        return moduloRepository.count();
    }

    // ✅ VERIFICAR SI EXISTE MÓDULO POR ID
    public boolean existePorId(Long id) {
        return moduloRepository.existsById(id);
    }
}