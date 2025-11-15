package com.proyectodesoftware.Sistema_Textil.Service;

import com.proyectodesoftware.Sistema_Textil.Repository.ProduccionHoraRepository;
import com.proyectodesoftware.Sistema_Textil.entities.ProduccionHora;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProduccionHoraService {

    private final ProduccionHoraRepository produccionHoraRepository;

    public ProduccionHoraService(ProduccionHoraRepository produccionHoraRepository) {
        this.produccionHoraRepository = produccionHoraRepository;
    }
      public List<ProduccionHora> listarTodas() {
        return produccionHoraRepository.findAll();
    }

    public List<ProduccionHora> listarPorProduccion(Long produccionId) {
        return produccionHoraRepository.findByProduccionId(produccionId);
    }

    public Optional<ProduccionHora> buscarPorId(Long id) {
        return produccionHoraRepository.findById(id);
    }

    public ProduccionHora guardar(ProduccionHora produccionHora) {
        return produccionHoraRepository.save(produccionHora);
    }

    public void eliminar(Long id) {
        produccionHoraRepository.deleteById(id);
    }
}
