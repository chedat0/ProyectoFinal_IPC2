/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controlador;

import otros.GsonConfig;
import daos.*;
import modelo.*;
import otros.JWT;
import otros.RespuestasServlet;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.jsonwebtoken.Claims;
import org.mindrot.jbcrypt.BCrypt;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author jeffm
 */
@WebServlet(name = "AdminServlet", urlPatterns = {"/api/admin/*"})
public class AdminServlet extends HttpServlet {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final AdministradorDAO adminDAO = new AdministradorDAO();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();
    private final HabilidadDAO habilidadDAO = new HabilidadDAO();
    private final SolicitudDAO solicitudDAO = new SolicitudDAO();
    private final ComisionDAO comisionDAO = new ComisionDAO();
    private final ReporteDAO reporteDAO = new ReporteDAO();
    private final Gson gson = GsonConfig.getGson();

    private Claims validateAdmin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String auth = req.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            RespuestasServlet.unauthorized(resp, "Token requerido");
            return null;
        }
        try {
            Claims c = JWT.validarToken(auth.substring(7));
            if (!"ADMINISTRADOR".equals(c.get("rol", String.class))) {
                RespuestasServlet.forbidden(resp, "Acceso solo para administradores");
                return null;
            }
            return c;
        } catch (Exception e) {
            RespuestasServlet.unauthorized(resp, "Token invalido");
            return null;
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Claims claims = validateAdmin(req, resp);
        if (claims == null) {
            return;
        }

        String path = req.getPathInfo();
        if (path == null) {
            path = "/";
        }

        try {
            if (path.equals("/usuarios")) {
                String tipo = req.getParameter("tipo");
                List<Usuario> usuarios = usuarioDAO.obtenerTodos(tipo);
                RespuestasServlet.ok(resp, gson.toJsonTree(usuarios));

            } else if (path.equals("/categorias")) {
                List<Categoria> categorias = categoriaDAO.obtenerTodas(false);
                RespuestasServlet.ok(resp, gson.toJsonTree(categorias));

            } else if (path.equals("/habilidades")) {
                Integer catId = req.getParameter("categoriaId") != null
                        ? Integer.parseInt(req.getParameter("categoriaId")) : null;
                List<Habilidad> habilidades = habilidadDAO.obtenerTodas(catId, false);
                RespuestasServlet.ok(resp, gson.toJsonTree(habilidades));

            } else if (path.equals("/solicitudes-habilidad")) {
                List<SolicitudHabilidad> solis = solicitudDAO.obtenerHabilidadesPendientes();
                RespuestasServlet.ok(resp, gson.toJsonTree(solis));

            } else if (path.equals("/solicitudes-categoria")) {
                List<SolicitudCategoria> solis = solicitudDAO.obtenerCategoriasPendientes();
                RespuestasServlet.ok(resp, gson.toJsonTree(solis));

            } else if (path.equals("/comision-actual")) {
                Double porcentajeActual = comisionDAO.obtenerComisionActual();
                JsonObject comision = new JsonObject();
                comision.addProperty("porcentaje", porcentajeActual);
                RespuestasServlet.ok(resp, gson.toJsonTree(comision));

            } else if (path.equals("/comision/historial")) {
                List<ConfiguracionComision> historial = comisionDAO.obtenerHistorial();
                RespuestasServlet.ok(resp, gson.toJsonTree(historial));

            } else if (path.equals("/saldo-global")) {
                Double saldo = comisionDAO.getSaldoGlobal();
                JsonObject res = new JsonObject();
                res.addProperty("saldoGlobal", saldo);
                RespuestasServlet.ok(resp, res);

            } else if (path.equals("/reportes/top-freelancers")) {
                String fi = req.getParameter("fechaInicio");
                String ff = req.getParameter("fechaFin");
                if (fi == null) {
                    fi = java.time.LocalDate.now().withDayOfYear(1).toString();
                }
                if (ff == null) {
                    ff = java.time.LocalDate.now().toString();
                }
                List<?> top = reporteDAO.topFreelancers(fi, ff);
                RespuestasServlet.ok(resp, gson.toJsonTree(top));

            } else if (path.equals("/reportes/top-categorias")) {
                String fi = req.getParameter("fechaInicio");
                String ff = req.getParameter("fechaFin");
                if (fi == null) {
                    fi = java.time.LocalDate.now().withDayOfYear(1).toString();
                }
                if (ff == null) {
                    ff = java.time.LocalDate.now().toString();
                }
                List<?> top = reporteDAO.topCategorias(fi, ff);
                RespuestasServlet.ok(resp, gson.toJsonTree(top));

            } else if (path.equals("/reportes/ingresos")) {
                String fechaInicio = req.getParameter("fechaInicio");
                String fechaFin = req.getParameter("fechaFin");
                Map<String, Object> ingresos = reporteDAO.ingresosTotales(fechaInicio, fechaFin);
                RespuestasServlet.ok(resp, gson.toJsonTree(ingresos));

            } else {
                RespuestasServlet.notFound(resp, "Ruta no encontrada");
            }
        } catch (Exception e) {
            RespuestasServlet.internalError(resp, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Claims claims = validateAdmin(req, resp);
        if (claims == null) {
            return;
        }

        String path = req.getPathInfo();
        if (path == null) {
            path = "/";
        }

        try {
            String body = req.getReader().lines().collect(Collectors.joining());
            JsonObject json = JsonParser.parseString(body).getAsJsonObject();

            if (path.equals("/categorias")) {
                Categoria cat = new Categoria();
                cat.setNombre(json.get("nombre").getAsString());
                cat.setDescripcion(json.has("descripcion") ? json.get("descripcion").getAsString() : null);
                Categoria creada = categoriaDAO.ingresar(cat);
                RespuestasServlet.created(resp, gson.toJsonTree(creada));

            } else if (path.equals("/habilidades")) {
                Habilidad hab = new Habilidad();
                hab.setNombre(json.get("nombre").getAsString());
                hab.setDescripcion(json.has("descripcion") ? json.get("descripcion").getAsString() : null);
                hab.setCategoriaId(json.get("categoriaId").getAsInt());
                Habilidad creada = habilidadDAO.ingresar(hab);
                RespuestasServlet.created(resp, gson.toJsonTree(creada));

            } else if (path.equals("/administradores")) {
                String username = json.get("username").getAsString();
                String correo = json.get("correo").getAsString();
                if (usuarioDAO.existePorUsuario(username)) {
                    RespuestasServlet.badRequest(resp, "Username ya existe");
                    return;
                }
                if (usuarioDAO.existePorCorreo(correo)) {
                    RespuestasServlet.badRequest(resp, "Correo ya registrado");
                    return;
                }
                Usuario u = new Usuario();
                u.setUsername(username);
                u.setCorreo(correo);
                u.setPasswordHash(BCrypt.hashpw(json.get("password").getAsString(), BCrypt.gensalt(12)));
                u.setNombreCompleto(json.get("nombre_completo").getAsString());
                if (json.has("cui")) {
                    u.setCui(json.get("cui").getAsString());
                }
                if (json.has("telefono")) {
                    u.setTelefono(json.get("telefono").getAsString());
                }
                u.setTipoUsuario("ADMINISTRADOR");
                Usuario creado = usuarioDAO.ingresar(u);
                String nivelAcceso = json.has("nivelAcceso") ? json.get("nivelAcceso").getAsString() : "ESTANDAR";
                Administrador admin = adminDAO.ingresar(creado.getId(), nivelAcceso);
                RespuestasServlet.created(resp, gson.toJsonTree(admin));

            } else {
                RespuestasServlet.notFound(resp, "Ruta no encontrada");
            }
        } catch (Exception e) {
            RespuestasServlet.internalError(resp, e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Claims claims = validateAdmin(req, resp);
        if (claims == null) {
            return;
        }

        String path = req.getPathInfo();
        if (path == null) {
            path = "/";
        }

        try {
            String body = req.getReader().lines().collect(Collectors.joining());
            JsonObject json = body.isEmpty() ? new JsonObject() : JsonParser.parseString(body).getAsJsonObject();

            if (path.matches("/categorias/\\d+$")) {
                int id = Integer.parseInt(path.split("/")[2]);
                Categoria cat = categoriaDAO.obtenerPorId(id);
                if (cat == null) {
                    RespuestasServlet.notFound(resp, "Categoria no encontrada");
                    return;
                }
                if (json.has("nombre")) {
                    cat.setNombre(json.get("nombre").getAsString());
                }
                if (json.has("descripcion")) {
                    cat.setDescripcion(json.get("descripcion").getAsString());
                }
                categoriaDAO.actualizar(cat);
                RespuestasServlet.ok(resp, gson.toJsonTree(cat));

            } else if (path.matches("/categorias/\\d+/toggle")) {
                int id = Integer.parseInt(path.split("/")[2]);
                Categoria cat = categoriaDAO.obtenerPorId(id);
                if (cat == null) {
                    RespuestasServlet.notFound(resp, "Categoria no encontrada");
                    return;
                }
                categoriaDAO.toggleActiva(id, !cat.getActiva());
                RespuestasServlet.ok(resp, "Estado de categoria actualizado");

            } else if (path.matches("/habilidades/\\d+$")) {
                int id = Integer.parseInt(path.split("/")[2]);
                Habilidad hab = habilidadDAO.obtenerPorId(id);
                if (hab == null) {
                    RespuestasServlet.notFound(resp, "Habilidad no encontrada");
                    return;
                }
                if (json.has("nombre")) {
                    hab.setNombre(json.get("nombre").getAsString());
                }
                if (json.has("descripcion")) {
                    hab.setDescripcion(json.get("descripcion").getAsString());
                }
                if (json.has("categoriaId")) {
                    hab.setCategoriaId(json.get("categoriaId").getAsInt());
                }
                habilidadDAO.actualizar(hab);
                RespuestasServlet.ok(resp, gson.toJsonTree(hab));

            } else if (path.matches("/habilidades/\\d+/toggle")) {
                int id = Integer.parseInt(path.split("/")[2]);
                Habilidad hab = habilidadDAO.obtenerPorId(id);
                if (hab == null) {
                    RespuestasServlet.notFound(resp, "Habilidad no encontrada");
                    return;
                }
                habilidadDAO.toggleActiva(id, !hab.getActiva());
                RespuestasServlet.ok(resp, "Estado de habilidad actualizado");

            } else if (path.matches("/solicitudes-habilidad/\\d+")) {
                int id = Integer.parseInt(path.split("/")[2]);
                String estado = json.get("estado").getAsString();
                int adminUserId = ((Number) claims.get("userId")).intValue();
                Administrador admin = adminDAO.obtenerPorUsuarioId(adminUserId);
                solicitudDAO.responderHabilidad(id, admin.getId(), estado);

                if ("ACEPTADA".equals(estado)) {
                    SolicitudHabilidad sol = solicitudDAO.obtenerHabilidadPorId(id);
                    // categoriaId debe venir en el body al aprobar
                    if (sol != null && json.has("categoriaId")) {
                        Habilidad nueva = new Habilidad();
                        nueva.setNombre(sol.getNombre());
                        nueva.setDescripcion(sol.getDescripcion());
                        nueva.setCategoriaId(json.get("categoriaId").getAsInt());
                        habilidadDAO.ingresar(nueva);
                    }
                }
                RespuestasServlet.ok(resp, "Solicitud de habilidad procesada");

            } else if (path.matches("/solicitudes-categoria/\\d+")) {
                int id = Integer.parseInt(path.split("/")[2]);
                String estado = json.get("estado").getAsString();
                int adminUserId = ((Number) claims.get("userId")).intValue();
                Administrador admin = adminDAO.obtenerPorUsuarioId(adminUserId);
                solicitudDAO.responderCategoria(id, admin.getId(), estado);

                if ("ACEPTADA".equals(estado)) {
                    SolicitudCategoria sol = solicitudDAO.obtenerCategoriaPorId(id);
                    if (sol != null) {
                        Categoria nueva = new Categoria();
                        nueva.setNombre(sol.getNombre());
                        nueva.setDescripcion(sol.getDescripcion());
                        categoriaDAO.ingresar(nueva);
                    }
                }
                
                RespuestasServlet.ok(resp, "Solicitud de categoria procesada");

            } else if (path.matches("/comision")) {
                Double porcentaje = json.get("porcentaje").getAsDouble();
                if (porcentaje < 0 || porcentaje > 100) {
                    RespuestasServlet.badRequest(resp, "Porcentaje debe estar entre 0 y 100");
                    return;
                }
                int adminUserId = ((Number) claims.get("userId")).intValue();
                Administrador admin = adminDAO.obtenerPorUsuarioId(adminUserId);
                comisionDAO.setComision(admin.getId(), porcentaje);
                RespuestasServlet.ok(resp, gson.toJsonTree(comisionDAO.obtenerComisionActual()));

            } else if (path.matches("/usuarios/\\d+/toggle")) {
                int id = Integer.parseInt(path.split("/")[2]);
                Usuario u = usuarioDAO.obtenerPorId(id);
                usuarioDAO.toggleActivo(id, !u.getActivo());
                RespuestasServlet.ok(resp, "Estado de usuario actualizado");

            } else {
                RespuestasServlet.notFound(resp, "Ruta no encontrada");
            }
        } catch (Exception e) {
            RespuestasServlet.internalError(resp, e.getMessage());
        }
    }
}
