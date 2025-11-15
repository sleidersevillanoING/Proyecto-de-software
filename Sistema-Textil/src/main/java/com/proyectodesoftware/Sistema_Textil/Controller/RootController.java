package com.proyectodesoftware.Sistema_Textil.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RootController {

    // ✅ Maneja la raíz del sitio
    @GetMapping("/")
    public String raiz() {
        return "redirect:/web/login";
    }
    
    // ✅ Maneja /login por si alguien accede directamente
    @GetMapping("/login")
    public String loginDirecto() {
        return "redirect:/web/login";
    }
}