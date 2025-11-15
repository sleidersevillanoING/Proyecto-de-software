package com.proyectodesoftware.Sistema_Textil.Controller;

import com.proyectodesoftware.Sistema_Textil.entities.Usuario;
import com.proyectodesoftware.Sistema_Textil.entities.Modulo;
import com.proyectodesoftware.Sistema_Textil.entities.Produccion;
import com.proyectodesoftware.Sistema_Textil.entities.ProduccionHora;
import com.proyectodesoftware.Sistema_Textil.Service.UsuarioService;
import com.proyectodesoftware.Sistema_Textil.Service.ModuloService;
import com.proyectodesoftware.Sistema_Textil.Service.ProduccionService;
import com.proyectodesoftware.Sistema_Textil.Service.ProduccionHoraService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/web")
public class WebController {

    private final UsuarioService usuarioService;
    private final ModuloService moduloService;
    private final ProduccionService produccionService;
    private final ProduccionHoraService produccionHoraService;

    public WebController(UsuarioService usuarioService, 
                        ModuloService moduloService,
                        ProduccionService produccionService,
                        ProduccionHoraService produccionHoraService) {
        this.usuarioService = usuarioService;
        this.moduloService = moduloService;
        this.produccionService = produccionService;
        this.produccionHoraService = produccionHoraService;
    }

    // ✅ RUTA PRINCIPAL
    @GetMapping("/")
    public String raiz() {
        return "redirect:/web/login";
    }

    @GetMapping("")
    public String home() {
        return "redirect:/web/login";
    }

    /* =======================
   👤 REGISTRAR NUEVO USUARIO
======================= */
@PostMapping("/registrar")
public String registrarUsuario(@RequestParam String username,
                              @RequestParam String password,
                              @RequestParam String nombreCompleto,
                              @RequestParam String rol,
                              HttpSession session) {
    try {
        System.out.println("👤 Intentando registrar usuario: " + username);
        
        // Verificar si el usuario ya existe
        Optional<Usuario> usuarioExistente = usuarioService.buscarPorUsername(username);
        if (usuarioExistente.isPresent()) {
            System.out.println("❌ Usuario ya existe: " + username);
            return "redirect:/web/login?registroError=El+usuario+ya+existe";
        }
        
        // Validar rol
        if (!rol.equals("SUPERVISOR") && !rol.equals("ADMIN")) {
            return "redirect:/web/login?registroError=Rol+inválido";
        }
        
        // Crear nuevo usuario
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setUsername(username);
        nuevoUsuario.setPassword(password); // El servicio debería encriptar esto
        nuevoUsuario.setNombreCompleto(nombreCompleto);
        nuevoUsuario.setRol(rol);
        nuevoUsuario.setActivo(true);
        
        // Guardar usuario
        Usuario usuarioGuardado = usuarioService.guardar(nuevoUsuario);
        System.out.println("✅ Usuario registrado exitosamente: " + usuarioGuardado.getUsername());
        
        return "redirect:/web/login?registroSuccess=Usuario+creado+exitosamente";
        
    } catch (Exception e) {
        System.out.println("💥 Error registrando usuario: " + e.getMessage());
        return "redirect:/web/login?registroError=Error+creando+usuario";
    }
}

    // ✅ PÁGINA DE LOGIN
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error, 
                           Model model, HttpSession session) {
        
        if (session.getAttribute("usuario") != null) {
            return "redirect:/web/dashboard";
        }
        
        if (error != null) {
            model.addAttribute("error", "Credenciales incorrectas o usuario inactivo");
        }
        
        return "login-page";
    }

    // ✅ PROCESAR LOGIN
    @PostMapping("/login")
    public String procesarLogin(@RequestParam String username, 
                               @RequestParam String password,
                               HttpSession session) {
        try {
            System.out.println("🔐 Intentando login WEB para: " + username);
            
            Optional<Usuario> usuarioOpt = usuarioService.validarYBuscarUsuario(username, password);
            
            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();
                System.out.println("✅ Login WEB exitoso: " + usuario.getNombreCompleto());
                session.setAttribute("usuario", usuario);
                return "redirect:/web/dashboard";
            } else {
                System.out.println("❌ Login WEB fallido para: " + username);
                return "redirect:/web/login?error=true";
            }
        } catch (Exception e) {
            System.out.println("💥 Error en login WEB: " + e.getMessage());
            return "redirect:/web/login?error=true";
        }
    }

    // ✅ DASHBOARD
  
  @GetMapping("/dashboard")
public String dashboard(Model model, HttpSession session) {
    Usuario usuario = (Usuario) session.getAttribute("usuario");
    if (usuario == null) {
        return "redirect:/web/login";
    }
    
    model.addAttribute("usuario", usuario);
    
    try {
        // ✅ Obtener estadísticas del día actual de forma segura
        LocalDate hoy = LocalDate.now();
        Object[] estadisticasHoy = produccionService.obtenerEstadisticasProduccion(hoy, hoy);
        
        // ✅ Asegurar que siempre tengamos un array válido
        if (estadisticasHoy != null) {
            System.out.println("📊 Estadísticas del día: " + Arrays.toString(estadisticasHoy));
            model.addAttribute("estadisticasHoy", estadisticasHoy);
        } else {
            // ✅ Crear estadísticas por defecto si son nulas
            System.out.println("ℹ️ No hay estadísticas, usando valores por defecto");
            Object[] estadisticasDefault = new Object[]{0, 0, 0.0, 0, 0, 0.0};
            model.addAttribute("estadisticasHoy", estadisticasDefault);
        }
        
    } catch (Exception e) {
        System.out.println("⚠️ Error obteniendo estadísticas: " + e.getMessage());
        // ✅ No romper el flujo, usar valores por defecto
        Object[] estadisticasDefault = new Object[]{0, 0, 0.0, 0, 0, 0.0};
        model.addAttribute("estadisticasHoy", estadisticasDefault);
    }
    
    return "dashboard";
}

    // ✅ LISTA DE MÓDULOS CON INFORMACIÓN COMPLETA
  /* =======================
   📦 LISTA DE MÓDULOS CON FORMULARIO DE CREACIÓN
======================= */
@GetMapping("/modulos")
public String modulos(Model model, HttpSession session) {
    Usuario usuario = (Usuario) session.getAttribute("usuario");
    if (usuario == null) {
        return "redirect:/web/login";
    }
    
    List<Modulo> modulos = moduloService.listar();
    
    // ✅ OBTENER SUPERVISORES ACTIVOS
    List<Usuario> supervisores = usuarioService.buscarSupervisoresActivos();
    model.addAttribute("supervisores", supervisores);
    
    // Crear una lista con información completa de cada módulo
    List<Map<String, Object>> modulosCompletos = new ArrayList<>();
    
    for (Modulo modulo : modulos) {
        Map<String, Object> moduloInfo = new HashMap<>();
        moduloInfo.put("modulo", modulo);
        
        // Obtener supervisor
        Usuario supervisor = null;
        if (modulo.getSupervisorId() != null) {
            supervisor = usuarioService.buscarPorId(modulo.getSupervisorId()).orElse(null);
        }
        moduloInfo.put("supervisor", supervisor);
        
        // Obtener producción del día
        Produccion produccionDia = produccionService.findByModuloAndFecha(modulo.getId(), LocalDate.now()).orElse(null);
        moduloInfo.put("produccionDia", produccionDia);
        
        // Obtener producción por hora
        List<ProduccionHora> produccionesHora = List.of();
        if (produccionDia != null) {
            produccionesHora = produccionHoraService.listarPorProduccion(produccionDia.getId());
        }
        moduloInfo.put("produccionesHora", produccionesHora);
        
        modulosCompletos.add(moduloInfo);
    }
    
    model.addAttribute("usuario", usuario);
    model.addAttribute("modulosCompletos", modulosCompletos);
    model.addAttribute("horaActual", LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
    model.addAttribute("fechaActual", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    
    return "modulos";
}
    /* =======================
       ➕ CREAR PRODUCCIÓN DÍA
    ======================= */
    @PostMapping("/modulos/{id}/crear-produccion-dia")
    public String crearProduccionDia(@PathVariable Long id, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/web/login";

        try {
            Optional<Modulo> moduloOpt = moduloService.buscarPorId(id);
            if (moduloOpt.isEmpty()) {
                return "redirect:/web/modulos?error=Módulo+no+encontrado";
            }

            Optional<Produccion> existente = produccionService.findByModuloAndFecha(id, LocalDate.now());
            if (existente.isPresent()) {
                return "redirect:/web/modulos?error=Ya+existe+una+producción+para+hoy";
            }

            Produccion nueva = new Produccion();
            nueva.setReferencia("REF-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-MOD" + id);
            nueva.setFecha(LocalDate.now());
            nueva.setModulo(moduloOpt.get());
            nueva.setProduccionTotal(0);
            nueva.setDefectuososTotal(0);
            nueva.setCreadoPor(usuario.getId());

            produccionService.guardar(nueva);
            return "redirect:/web/modulos?success=Producción+creada+para+módulo+" + id;
            
        } catch (Exception e) {
            System.out.println("💥 Error creando producción del día: " + e.getMessage());
            return "redirect:/web/modulos?error=Error+creando+producción";
        }
    }

    /* =======================
       ⏱️ AGREGAR HORA PRODUCCIÓN - CORREGIDO Y MEJORADO
    ======================= */
    @PostMapping("/modulos/{id}/agregar-hora")
    public String agregarProduccionHora(@PathVariable Long id,
                                        @RequestParam String hora,
                                        @RequestParam Integer cantidad,
                                        @RequestParam Integer defectuosos,
                                        HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            System.out.println("🚫 Usuario no autenticado");
            return "redirect:/web/login";
        }

        try {
            System.out.println("🔧 INICIANDO - Agregar hora producción");
            System.out.println("   📍 Módulo ID: " + id);
            System.out.println("   ⏰ Hora: " + hora);
            System.out.println("   📦 Cantidad: " + cantidad);
            System.out.println("   ❌ Defectuosos: " + defectuosos);
            System.out.println("   👤 Usuario: " + usuario.getNombreCompleto());

            // ✅ 1. Verificar que el módulo existe
            System.out.println("🔍 Buscando módulo con ID: " + id);
            Optional<Modulo> moduloOpt = moduloService.buscarPorId(id);
            if (moduloOpt.isEmpty()) {
                System.out.println("❌ MÓDULO NO ENCONTRADO - ID: " + id);
                return "redirect:/web/modulos?error=Módulo+no+encontrado";
            }
            Modulo modulo = moduloOpt.get();
            System.out.println("✅ Módulo encontrado: " + modulo.getNombreModulo() + " (N° " + modulo.getNumeroModulo() + ")");

            // ✅ 2. Buscar producción del día
            System.out.println("📅 Buscando producción del día - Módulo: " + id + ", Fecha: " + LocalDate.now());
            Optional<Produccion> produccionOpt = produccionService.findByModuloAndFecha(id, LocalDate.now());
            if (produccionOpt.isEmpty()) {
                System.out.println("❌ NO HAY PRODUCCIÓN DEL DÍA - Módulo: " + id);
                return "redirect:/web/modulos?error=No+existe+producción+del+día+para+este+módulo";
            }

            Produccion produccion = produccionOpt.get();
            System.out.println("✅ Producción encontrada - ID: " + produccion.getId() + ", Ref: " + produccion.getReferencia());

            // ✅ 3. Parsear y validar hora
            System.out.println("⏰ Parseando hora: " + hora);
            LocalTime horaRegistro;
            try {
                horaRegistro = LocalTime.parse(hora);
                System.out.println("✅ Hora parseada correctamente: " + horaRegistro);
            } catch (Exception e) {
                System.out.println("❌ ERROR PARSING HORA: " + hora + " - " + e.getMessage());
                return "redirect:/web/modulos?error=Formato+de+hora+inválido";
            }

            // ✅ 4. Verificar si ya existe registro para esta hora
            System.out.println("🔎 Verificando duplicados - Hora: " + horaRegistro + ", Producción ID: " + produccion.getId());
            List<ProduccionHora> horasExistentes = produccionHoraService.listarPorProduccion(produccion.getId());
            boolean existe = horasExistentes.stream()
                    .anyMatch(h -> h.getHora().equals(horaRegistro));
            
            if (existe) {
                System.out.println("❌ HORA DUPLICADA - Ya existe registro para: " + horaRegistro);
                System.out.println("   Horas existentes: " + horasExistentes.stream()
                        .map(h -> h.getHora().toString())
                        .collect(Collectors.joining(", ")));
                return "redirect:/web/modulos?error=Ya+hay+registro+para+esa+hora";
            }
            System.out.println("✅ Hora disponible - No hay duplicados");

            // ✅ 5. Validar y preparar datos
            int cantidadFinal = cantidad != null ? cantidad : 0;
            int defectuososFinal = defectuosos != null ? defectuosos : 0;
            
            if (cantidadFinal < 0) {
                System.out.println("⚠️ Cantidad negativa, ajustando a 0");
                cantidadFinal = 0;
            }
            if (defectuososFinal < 0) {
                System.out.println("⚠️ Defectuosos negativos, ajustando a 0");
                defectuososFinal = 0;
            }
            if (defectuososFinal > cantidadFinal) {
                System.out.println("⚠️ Defectuosos mayor que cantidad, ajustando defectuosos a " + cantidadFinal);
                defectuososFinal = cantidadFinal;
            }

            System.out.println("📊 Datos finales - Cantidad: " + cantidadFinal + ", Defectuosos: " + defectuososFinal);

            // ✅ 6. Crear y guardar nueva producción por hora
            System.out.println("🆕 Creando nueva ProduccionHora");
            ProduccionHora nueva = new ProduccionHora();
            nueva.setProduccion(produccion);
            nueva.setHora(horaRegistro);
            nueva.setCantidad(cantidadFinal);
            nueva.setDefectuosos(defectuososFinal);
            
            System.out.println("💾 Guardando ProduccionHora en base de datos");
            ProduccionHora guardada = produccionHoraService.guardar(nueva);
            System.out.println("✅ ProduccionHora guardada - ID: " + guardada.getId());

            // ✅ 7. ACTUALIZAR TOTALES AUTOMÁTICAMENTE usando el nuevo método
            System.out.println("🔄 Actualizando totales del módulo automáticamente");
            try {
                produccionService.actualizarTotalesProduccion(produccion.getId());
                System.out.println("✅ Totales actualizados correctamente");
            } catch (Exception e) {
                System.out.println("⚠️ Error actualizando totales (continuando): " + e.getMessage());
                // No re-lanzamos la excepción para no interrumpir el flujo principal
            }

            // ✅ 8. Éxito - Redireccionar
            System.out.println("🎯 PROCESO COMPLETADO EXITOSAMENTE");
            System.out.println("   Módulo: " + modulo.getNombreModulo());
            System.out.println("   Hora: " + horaRegistro);
            System.out.println("   Cantidad: " + cantidadFinal);
            System.out.println("   Defectuosos: " + defectuososFinal);
            
            return "redirect:/web/modulos?success=Hora+" + horaRegistro.toString().replace(":", "") + 
                   "+agregada+correctamente+en+módulo+" + id;

        } catch (Exception e) {
            System.out.println("💥 ERROR CRÍTICO en agregarProduccionHora:");
            System.out.println("   📍 Módulo ID: " + id);
            System.out.println("   ⏰ Hora: " + hora);
            System.out.println("   👤 Usuario: " + (usuario != null ? usuario.getNombreCompleto() : "null"));
            System.out.println("   🚨 Exception: " + e.getClass().getSimpleName());
            System.out.println("   📝 Mensaje: " + e.getMessage());
            e.printStackTrace();
            
            return "redirect:/web/modulos?error=Error+interno+al+agregar+hora+" + 
                   URLEncoder.encode(e.getMessage() != null ? e.getMessage() : "Error desconocido", StandardCharsets.UTF_8);
        }
    }

    /* =======================
       🔄 ACTUALIZAR TOTALES MANUALMENTE
    ======================= */
    @PostMapping("/modulos/{id}/actualizar-totales")
    public String actualizarTotalesManual(@PathVariable Long id, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/web/login";
        
        try {
            System.out.println("🔄 INICIANDO - Actualizar totales manual para módulo: " + id);
            
            // Buscar producción del día
            Optional<Produccion> produccionOpt = produccionService.findByModuloAndFecha(id, LocalDate.now());
            if (produccionOpt.isEmpty()) {
                return "redirect:/web/modulos?error=No+existe+producción+del+día+para+este+módulo";
            }
            
            Produccion produccion = produccionOpt.get();
            produccionService.actualizarTotalesProduccion(produccion.getId());
            
            System.out.println("✅ Totales actualizados exitosamente para módulo: " + id);
            return "redirect:/web/modulos?success=Totales+actualizados+correctamente";
        } catch (Exception e) {
            System.out.println("💥 ERROR actualizando totales manualmente para módulo " + id + ": " + e.getMessage());
            e.printStackTrace();
            return "redirect:/web/modulos?error=Error+actualizando+totales+" + 
                   URLEncoder.encode(e.getMessage() != null ? e.getMessage() : "Error desconocido", StandardCharsets.UTF_8);
        }
    }

    /* =======================
       📊 GENERAR REPORTES POR MÓDULO
    ======================= */
    @GetMapping("/modulos/{id}/generar-reporte")
    public String generarReporteModulo(@PathVariable Long id,
                                     @RequestParam(required = false) String fechaInicio,
                                     @RequestParam(required = false) String fechaFin,
                                     Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/web/login";

        try {
            // Fechas por defecto (últimos 7 días)
            LocalDate fechaInicioDate = fechaInicio != null ? LocalDate.parse(fechaInicio) : LocalDate.now().minusDays(7);
            LocalDate fechaFinDate = fechaFin != null ? LocalDate.parse(fechaFin) : LocalDate.now();

            // Obtener módulo
            Optional<Modulo> moduloOpt = moduloService.buscarPorId(id);
            if (moduloOpt.isEmpty()) {
                return "redirect:/web/modulos?error=Módulo+no+encontrado";
            }

            Modulo modulo = moduloOpt.get();

            // Obtener estadísticas del módulo
            Object[] estadisticas = produccionService.obtenerEstadisticasPorModulo(fechaInicioDate, fechaFinDate, id);
            
            // Obtener producción diaria para el gráfico
            List<Object[]> produccionDiaria = produccionService.obtenerEstadisticasPorDia(fechaInicioDate, fechaFinDate);
            
            // Obtener todas las producciones del módulo en el rango de fechas
            List<Produccion> producciones = produccionService.buscarPorFecha(fechaInicioDate).stream()
                    .filter(p -> p.getModulo().getId().equals(id))
                    .collect(Collectors.toList());

            model.addAttribute("modulo", modulo);
            model.addAttribute("estadisticas", estadisticas);
            model.addAttribute("produccionDiaria", produccionDiaria);
            model.addAttribute("producciones", producciones);
            model.addAttribute("fechaInicio", fechaInicioDate);
            model.addAttribute("fechaFin", fechaFinDate);
            model.addAttribute("usuario", usuario);

            return "reporte-modulo";

        } catch (Exception e) {
            System.out.println("💥 Error generando reporte: " + e.getMessage());
            return "redirect:/web/modulos?error=Error+generando+reporte";
        }
    }

    /* =======================
       📈 REPORTES GENERALES
    ======================= */
    @GetMapping("/reportes")
    public String reportes(@RequestParam(required = false) String fechaInicio,
                         @RequestParam(required = false) String fechaFin,
                         Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/web/login";

        try {
            // Fechas por defecto (últimos 30 días)
            LocalDate fechaInicioDate = fechaInicio != null ? LocalDate.parse(fechaInicio) : LocalDate.now().minusDays(30);
            LocalDate fechaFinDate = fechaFin != null ? LocalDate.parse(fechaFin) : LocalDate.now();

            // Obtener estadísticas generales
            Object[] estadisticasGenerales = produccionService.obtenerEstadisticasProduccion(fechaInicioDate, fechaFinDate);
            
            // Obtener resumen por módulos
            List<Object[]> resumenModulos = produccionService.obtenerEstadisticasResumenPorModulo(fechaInicioDate, fechaFinDate);
            
            // Obtener producción diaria
            List<Object[]> produccionDiaria = produccionService.obtenerEstadisticasPorDia(fechaInicioDate, fechaFinDate);

            model.addAttribute("estadisticasGenerales", estadisticasGenerales);
            model.addAttribute("resumenModulos", resumenModulos);
            model.addAttribute("produccionDiaria", produccionDiaria);
            model.addAttribute("fechaInicio", fechaInicioDate);
            model.addAttribute("fechaFin", fechaFinDate);
            model.addAttribute("usuario", usuario);

            return "reportes";

        } catch (Exception e) {
            System.out.println("💥 Error cargando reportes: " + e.getMessage());
            model.addAttribute("error", "Error cargando reportes: " + e.getMessage());
            return "reportes";
        }
    }

    /* =======================
       📋 HISTORIAL DE REPORTES
    ======================= */
    @GetMapping("/historial-reportes")
    public String historialReportes(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/web/login";

        model.addAttribute("usuario", usuario);
        return "historial-reportes";
    }

    /* =======================
       🗑️ ELIMINAR REGISTRO POR HORA
    ======================= */
    @PostMapping("/produccion-hora/{id}/eliminar")
    public String eliminarProduccionHora(@PathVariable Long id, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/web/login";

        try {
            Optional<ProduccionHora> produccionHoraOpt = produccionHoraService.buscarPorId(id);
            if (produccionHoraOpt.isPresent()) {
                ProduccionHora produccionHora = produccionHoraOpt.get();
                Long produccionId = produccionHora.getProduccion().getId();
                Long moduloId = produccionHora.getProduccion().getModulo().getId();
                
                // Eliminar el registro por hora
                produccionHoraService.eliminar(id);
                
                // Actualizar totales
                produccionService.actualizarTotalesProduccion(produccionId);
                
                return "redirect:/web/modulos?success=Registro+eliminado+correctamente";
            }
            return "redirect:/web/modulos?error=Registro+no+encontrado";
        } catch (Exception e) {
            System.out.println("💥 Error eliminando registro por hora: " + e.getMessage());
            return "redirect:/web/modulos?error=Error+eliminando+registro";
        }
    }
/* =======================
   ➕ CREAR NUEVO MÓDULO DESDE WEB
======================= */

@PostMapping("/modulos/crear")
public String crearModulo(@RequestParam Integer numeroModulo,
                         @RequestParam String nombreModulo,
                         @RequestParam(required = false) String descripcion,
                         @RequestParam(required = false) Long supervisorId,
                         HttpSession session) {
    Usuario usuario = (Usuario) session.getAttribute("usuario");
    if (usuario == null) return "redirect:/web/login";

    try {
        System.out.println("🏗️ Creando nuevo módulo desde web: " + nombreModulo);
        
        // Crear nuevo módulo
        Modulo nuevoModulo = new Modulo();
        nuevoModulo.setNumeroModulo(numeroModulo);
        nuevoModulo.setNombreModulo(nombreModulo);
        nuevoModulo.setDescripcion(descripcion);
        nuevoModulo.setSupervisorId(supervisorId);
        
        // Guardar módulo usando el servicio
        Modulo moduloGuardado = moduloService.guardar(nuevoModulo);
        System.out.println("✅ Módulo creado exitosamente: " + moduloGuardado.getId());
        
        return "redirect:/web/modulos?success=Módulo+" + nombreModulo.replace(" ", "+") + "+creado+exitosamente";
        
    } catch (IllegalArgumentException e) {
        System.out.println("❌ Error de validación: " + e.getMessage());
        return "redirect:/web/modulos?error=" + e.getMessage().replace(" ", "+");
    } catch (Exception e) {
        System.out.println("💥 Error creando módulo: " + e.getMessage());
        return "redirect:/web/modulos?error=Error+creando+módulo";
    }
}
    // ✅ LOGOUT
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/web/login";
    }

    /* =======================
       🔍 BUSCAR PRODUCCIÓN POR FECHA
    ======================= */
    @GetMapping("/produccion/buscar")
    public String buscarProduccionPorFecha(@RequestParam String fecha, 
                                         Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/web/login";

        try {
            LocalDate fechaBusqueda = LocalDate.parse(fecha);
            List<Produccion> producciones = produccionService.buscarPorFecha(fechaBusqueda);
            
            model.addAttribute("producciones", producciones);
            model.addAttribute("fechaBusqueda", fechaBusqueda);
            model.addAttribute("usuario", usuario);
            
            return "busqueda-produccion";
        } catch (Exception e) {
            System.out.println("💥 Error buscando producción: " + e.getMessage());
            return "redirect:/web/dashboard?error=Error+buscando+producción";
        }
    }
}