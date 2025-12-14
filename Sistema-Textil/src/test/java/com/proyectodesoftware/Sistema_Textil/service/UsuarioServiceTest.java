package com.proyectodesoftware.Sistema_Textil.service;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.proyectodesoftware.Sistema_Textil.Repository.UsuarioRepository;
import com.proyectodesoftware.Sistema_Textil.Service.UsuarioService;
import com.proyectodesoftware.Sistema_Textil.entities.Usuario;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
class UsuarioServiceTest {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void validarUsuario_exitoso() {
        Usuario u = new Usuario();
        u.setUsername("usuario_test_02");
        u.setPassword("1235");
        u.setActivo(true);
        u.setRol("Supervisor");

        usuarioRepository.save(u);

        Optional<Usuario> resultado =
                usuarioService.validarYBuscarUsuario("usuario_test_02", "1235");

        assertTrue(resultado.isPresent());
    }

}
