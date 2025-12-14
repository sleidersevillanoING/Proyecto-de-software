package com.proyectodesoftware.Sistema_Textil.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.proyectodesoftware.Sistema_Textil.Repository.ProduccionRepository;
import com.proyectodesoftware.Sistema_Textil.Service.ProduccionHoraService;
import com.proyectodesoftware.Sistema_Textil.entities.Produccion;
import com.proyectodesoftware.Sistema_Textil.entities.ProduccionHora;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
class ProduccionHoraServiceTest {

    @Autowired
    private ProduccionHoraService produccionHoraService;

    @Autowired
    private ProduccionRepository produccionRepository;

    @Test
    void registrarProduccionHora_exitoso() {

        // 🔹 Producción padre
        Produccion produccion = new Produccion();
        produccion.setFecha(LocalDate.now());
        produccion.setReferencia("REF-TEST-001");
        produccion.setCantidadTotal(0);
        produccion.setProduccionTotal(1);
        

        produccion = produccionRepository.save(produccion);

        // 🔹 Producción por hora
        ProduccionHora ph = new ProduccionHora();
        ph.setHora(LocalTime.of(8, 0));
        ph.setCantidad(20);
        ph.setProduccion(produccion);

        ProduccionHora guardado = produccionHoraService.guardar(ph);

        assertNotNull(guardado.getId());
        assertEquals(20, guardado.getCantidad());
    }
}
