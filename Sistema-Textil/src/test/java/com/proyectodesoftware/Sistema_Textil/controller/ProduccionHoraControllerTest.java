
package com.proyectodesoftware.Sistema_Textil.controller;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalTime;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;


import org.springframework.http.MediaType;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyectodesoftware.Sistema_Textil.SistemaTextilApplication;
import com.proyectodesoftware.Sistema_Textil.Controller.ProduccionHoraController;
import com.proyectodesoftware.Sistema_Textil.Service.ProduccionHoraService;
import com.proyectodesoftware.Sistema_Textil.entities.Produccion;
import com.proyectodesoftware.Sistema_Textil.entities.ProduccionHora;




@WebMvcTest(ProduccionHoraController.class)
@ContextConfiguration(classes = SistemaTextilApplication.class)
class ProduccionHoraControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProduccionHoraService produccionHoraService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registrarProduccionHora_exitoso() throws Exception {

        Produccion produccion = new Produccion();
        produccion.setId(1L);

        ProduccionHora ph = new ProduccionHora();
        ph.setId(1L);
        ph.setHora(LocalTime.of(8, 0));
        ph.setCantidad(25);
        ph.setProduccion(produccion);

        when(produccionHoraService.guardar(any(ProduccionHora.class)))
            .thenReturn(ph);

            String json = """
        {
            "hora": "08:00",
            "cantidad": 25,
            "produccion": {
                "id": 1
            }
        }
        """;

        mockMvc.perform(post("/produccion_hora")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.cantidad").value(25))
                .andExpect(jsonPath("$.hora").value("08:00:00"));

    }
}




