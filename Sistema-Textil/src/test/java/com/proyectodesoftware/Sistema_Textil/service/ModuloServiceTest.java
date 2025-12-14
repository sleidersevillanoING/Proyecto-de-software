package com.proyectodesoftware.Sistema_Textil.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.proyectodesoftware.Sistema_Textil.entities.Modulo;



import com.proyectodesoftware.Sistema_Textil.Repository.ModuloRepository;
import com.proyectodesoftware.Sistema_Textil.Repository.UsuarioRepository;
import com.proyectodesoftware.Sistema_Textil.Service.ModuloService;

@ExtendWith(MockitoExtension.class)
class ModuloServiceTest {

    @Mock
    private ModuloRepository moduloRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private ModuloService moduloService;

    @BeforeEach
    void limpiarBD() {
        usuarioRepository.deleteAll();
    }

    @Test
    void registrarModulo_exitoso() {
        Modulo modulo = new Modulo();
        modulo.setNumeroModulo(1);
        modulo.setNombreModulo("Corte");

        when(moduloRepository.save(any(Modulo.class)))
                .thenReturn(modulo);

        Modulo resultado = moduloService.guardar(modulo);

        assertNotNull(resultado);
        assertEquals("Corte", resultado.getNombreModulo());
    }
}

