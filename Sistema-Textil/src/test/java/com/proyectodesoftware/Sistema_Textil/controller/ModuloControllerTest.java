package com.proyectodesoftware.Sistema_Textil.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyectodesoftware.Sistema_Textil.entities.Modulo;

import jakarta.transaction.Transactional;

import com.proyectodesoftware.Sistema_Textil.SistemaTextilApplication;
import com.proyectodesoftware.Sistema_Textil.Service.ModuloService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(classes = SistemaTextilApplication.class)
@AutoConfigureMockMvc
@Transactional
class ModuloControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ModuloService moduloService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void crearModulo_exitoso() throws Exception {
        Modulo modulo = new Modulo();
        modulo.setNumeroModulo(1);
        modulo.setNombreModulo("Producción");

        when(moduloService.guardar(any(Modulo.class)))
                .thenReturn(modulo);

        mockMvc.perform(post("/modulos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(modulo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreModulo").value("Producción"));
    }
}
