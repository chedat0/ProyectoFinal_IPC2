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

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServlet;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDate;



/**
 *
 * @author jeffm
 */
@WebServlet(name = "FreelancerServlet", urlPatterns = {"/api/freelancer/*"})
public class FreelancerServlet extends HttpServlet {
    
    private final FreelancerDAO freelancerDAO = new FreelancerDAO();
    private final ProyectoDAO proyectoDAO = new ProyectoDAO();
    private final PropuestaDAO propuestaDAO = new PropuestaDAO();
    private final ContratoDAO contratoDAO = new ContratoDAO();
    private final EntregaDAO entregaDAO = new EntregaDAO();
    private final HabilidadDAO habilidadDAO = new HabilidadDAO();
    private final SolicitudDAO solicitudDAO = new SolicitudDAO();
    private final ReporteDAO reporteDAO = new ReporteDAO();
    private final Gson gson = GsonConfig.getGson();

    private Claims validateFreelancer(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String auth = req.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            RespuestasServlet.unauthorized(resp, "Token requerido");
            return null;
        }
        try {
            Claims c = JWT.validarToken(auth.substring(7));
            if (!"FREELANCER".equals(c.get("rol", String.class))) {
                RespuestasServlet.forbidden(resp, "Acceso solo para freelancers");
                return null;
            }
            return c;
        } catch (Exception e) {
            RespuestasServlet.unauthorized(resp, "Token inválido");
            return null;
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Claims claims = validateFreelancer(req, resp);
        if (claims == null) return;

        int usuarioId = ((Number) claims.get("userId")).intValue();
        String path = req.getPathInfo();
        if (path == null) path = "/";

        try {
            // GET /api/freelancer/perfil
            if (path.equals("/perfil")) {
                Freelancer f = freelancerDAO.obtenerPorUsuarioId(usuarioId);
                if (f == null) { RespuestasServlet.notFound(resp, "Perfil no encontrado"); return; }
                RespuestasServlet.ok(resp, gson.toJsonTree(f));

            // GET /api/freelancer/proyectos  (catálogo con filtros)
            } else if (path.equals("/proyectos")) {
                Integer categoriaId = parseIntParam(req, "categoriaId");
                Integer habilidadId = parseIntParam(req, "habilidadId");
                String presMin = req.getParameter("presupuestoMin");
                String presMax = req.getParameter( "presupuestoMax");
                List<Proyecto> proyectos = proyectoDAO.obtenerAbiertos(categoriaId, habilidadId, presMin, presMax);
                RespuestasServlet.ok(resp, gson.toJsonTree(proyectos));

            // GET /api/freelancer/propuestas
            } else if (path.equals("/propuestas")) {
                Freelancer f = freelancerDAO.obtenerPorUsuarioId(usuarioId);
                List<Propuesta> props = propuestaDAO.obtenerporFreelancer(f.getId());
                RespuestasServlet.ok(resp, gson.toJsonTree(props));

            // GET /api/freelancer/contratos
            } else if (path.equals("/contratos")) {
                Freelancer f = freelancerDAO.obtenerPorUsuarioId(usuarioId);
                List<Contrato> contratos = contratoDAO.obtenerPorFreelancer(f.getId(), null);
                RespuestasServlet.ok(resp, gson.toJsonTree(contratos));

            // GET /api/freelancer/contratos/{id}
            } else if (path.matches("/contratos/\\d+")) {
                int contratoId = Integer.parseInt(path.split("/")[2]);
                Freelancer f = freelancerDAO.obtenerPorUsuarioId(usuarioId);
                Contrato c = contratoDAO.obtenerPorId(contratoId);
                if (c == null || c.getFreelancerId() != f.getId()) {
                    RespuestasServlet.notFound(resp, "Contrato no encontrado"); return;
                }
                RespuestasServlet.ok(resp, gson.toJsonTree(c));

            // GET /api/freelancer/contratos/{id}/entregas
            } else if (path.matches("/contratos/\\d+/entregas")) {
                int contratoId = Integer.parseInt(path.split("/")[2]);
                Freelancer f = freelancerDAO.obtenerPorUsuarioId(usuarioId);
                Contrato c = contratoDAO.obtenerPorId(contratoId);
                if (c == null || c.getFreelancerId() != f.getId()) {
                    RespuestasServlet.forbidden(resp, "No autorizado"); return;
                }
                List<Entrega> entregas = entregaDAO.obtenerPorContrato(contratoId);
                RespuestasServlet.ok(resp, gson.toJsonTree(entregas));

            // GET /api/freelancer/reportes/saldo
            } else if (path.equals("/reportes/saldo")) {
                Freelancer f = freelancerDAO.obtenerPorUsuarioId(usuarioId);
                List<?> historial = reporteDAO.contratosCompletadosFreelancer(f.getId(), null, null);
                RespuestasServlet.ok(resp, gson.toJsonTree(historial));

            // GET /api/freelancer/reportes/contratos
            } else if (path.equals("/reportes/contratos")) {
                Freelancer f = freelancerDAO.obtenerPorUsuarioId(usuarioId);
                String fechaInicio = req.getParameter("fechaInicio");
                String fechaFin = req.getParameter("fechaFin");                
                List<?> rep = reporteDAO.contratosCompletadosFreelancer(f.getId(), fechaInicio, fechaFin);
                RespuestasServlet.ok(resp, gson.toJsonTree(rep));

            // GET /api/freelancer/reportes/categorias
            } else if (path.equals("/reportes/categorias")) {
                Freelancer f = freelancerDAO.obtenerPorUsuarioId(usuarioId);
                List<?> rep = reporteDAO.topCategoriasFreelancer(f.getId());
                RespuestasServlet.ok(resp, gson.toJsonTree(rep));

            // GET /api/freelancer/reportes/propuestas
            } else if (path.equals("/reportes/propuestas")) {
                Freelancer f = freelancerDAO.obtenerPorUsuarioId(usuarioId);
                List<Propuesta> todas = propuestaDAO.obtenerporFreelancer(f.getId());
                JsonObject resumen = new JsonObject();
                resumen.addProperty("total", todas.size());
                resumen.addProperty("pendientes", todas.stream().filter(p -> "PENDIENTE".equals(p.getEstado())).count());
                resumen.addProperty("aceptadas", todas.stream().filter(p -> "ACEPTADA".equals(p.getEstado())).count());
                resumen.addProperty("rechazadas", todas.stream().filter(p -> "RECHAZADA".equals(p.getEstado())).count());
                RespuestasServlet.ok(resp, resumen);

            } else {
                RespuestasServlet.notFound(resp, "Ruta no encontrada");
            }
        } catch (Exception e) {
            RespuestasServlet.internalError(resp, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Claims claims = validateFreelancer(req, resp);
        if (claims == null) return;

        int usuarioId = claims.get("userId", Integer.class);
        String path = req.getPathInfo();
        if (path == null) path = "/";

        try {
            String body = req.getReader().lines().collect(Collectors.joining());
            JsonObject json = JsonParser.parseString(body).getAsJsonObject();

            // POST /api/freelancer/propuestas
            if (path.equals("/propuestas")) {
                Freelancer f = freelancerDAO.obtenerPorUsuarioId(usuarioId);
                int proyectoId = json.get("proyectoId").getAsInt();

                // Validar que no haya propuesta duplicada
                if (propuestaDAO.existePropuesta(proyectoId, f.getId())) {
                    RespuestasServlet.badRequest(resp, "Ya enviaste una propuesta a este proyecto");
                    return;
                }

                Proyecto p = proyectoDAO.obtenerPorId(proyectoId);
                if (p == null || !"ABIERTO".equals(p.getEstado())) {
                    RespuestasServlet.badRequest(resp, "Proyecto no disponible");
                    return;
                }

                Propuesta propuesta = new Propuesta();
                propuesta.setProyectoId(proyectoId);
                propuesta.setFreelancerId(f.getId());
                propuesta.setMontoOfertado(json.get("montoOfertado").getAsDouble());
                propuesta.setPlazoDias(json.get("tiempoEntregaDias").getAsInt());
                propuesta.setCartaPresentacion(json.has("mensaje") ? json.get("mensaje").getAsString() : null);

                Propuesta creada = propuestaDAO.ingresar(propuesta);
                RespuestasServlet.created(resp, gson.toJsonTree(creada));

            // POST /api/freelancer/contratos/{id}/entregas
            } else if (path.matches("/contratos/\\d+/entregas")) {
                int contratoId = Integer.parseInt(path.split("/")[2]);
                Freelancer f = freelancerDAO.obtenerPorUsuarioId(usuarioId);
                Contrato c = contratoDAO.obtenerPorId(contratoId);
                if (c == null || c.getFreelancerId() != f.getId()) {
                    RespuestasServlet.forbidden(resp, "No autorizado"); return;
                }
                if (!"ACTIVO".equals(c.getEstado())) {
                    RespuestasServlet.badRequest(resp, "El contrato no está activo"); return;
                }

                Entrega entrega = new Entrega();
                entrega.setContratoId(contratoId);
                entrega.setDescripcion(json.get("descripcion").getAsString());

                Entrega creada = entregaDAO.ingresar(entrega, null);
                RespuestasServlet.created(resp, gson.toJsonTree(creada));

            // POST /api/freelancer/solicitudes-habilidad
            } else if (path.equals("/solicitudes-habilidad")) {
                SolicitudHabilidad sol = new SolicitudHabilidad();
                Freelancer f = freelancerDAO.obtenerPorUsuarioId(usuarioId);
                sol.setFreelancerId(f.getId());
                sol.setNombre(json.get("nombre").getAsString());
                sol.setDescripcion(json.has("descripcion") ? json.get("descripcion").getAsString() : null);                
                SolicitudHabilidad creada = solicitudDAO.crearHabilidad(sol);
                RespuestasServlet.created(resp, gson.toJsonTree(creada));

            } else {
                RespuestasServlet.notFound(resp, "Ruta no encontrada");
            }
        } catch (Exception e) {
            RespuestasServlet.internalError(resp, e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Claims claims = validateFreelancer(req, resp);
        if (claims == null) return;

        int usuarioId = claims.get("userId", Integer.class);
        String path = req.getPathInfo();
        if (path == null) path = "/";

        try {
            String body = req.getReader().lines().collect(Collectors.joining());
            JsonObject json = JsonParser.parseString(body).getAsJsonObject();

            // PUT /api/freelancer/perfil
            if (path.equals("/perfil")) {
                Freelancer f = freelancerDAO.obtenerPorUsuarioId(usuarioId);
                if (json.has("descripcion")) f.setDescripcion(json.get("descripcion").getAsString());
                if (json.has("especialidad")) f.setEspecialidad(json.get("especialidad").getAsString());
                if (json.has("tarifaHora")) f.setTarifaHora(json.get("tarifaHora").getAsDouble());
                if (json.has("nivelExperiencia")) f.setNivelExperiencia(json.get("nivelExperiencia").getAsString());
                if (json.has("portafolioUrl")) f.setPortafolioUrl(json.get("portafolioUrl").getAsString());
                if (json.has("paisResidencia")) f.setPaisResidencia(json.get("paisResidencia").getAsString());
                freelancerDAO.actualizarPerfil(f);
                RespuestasServlet.ok(resp, gson.toJsonTree(freelancerDAO.obtenerPorUsuarioId(usuarioId)));

            // PUT /api/freelancer/habilidades
            } else if (path.equals("/habilidades")) {
                Freelancer f = freelancerDAO.obtenerPorUsuarioId(usuarioId);
                List<Integer> habilidadIds = gson.fromJson(json.get("habilidadIds"), 
                    new com.google.gson.reflect.TypeToken<List<Integer>>(){}.getType());
                freelancerDAO.setHabilidades(f.getId(), habilidadIds);
                RespuestasServlet.ok(resp, gson.toJsonTree(freelancerDAO.obtenerPorUsuarioId(usuarioId)));

            } else {
                RespuestasServlet.notFound(resp, "Ruta no encontrada");
            }
        } catch (Exception e) {
            RespuestasServlet.internalError(resp, e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Claims claims = validateFreelancer(req, resp);
        if (claims == null) return;

        int usuarioId = claims.get("userId", Integer.class);
        String path = req.getPathInfo();
        if (path == null) path = "/";

        try {
            // DELETE /api/freelancer/propuestas/{id}
            if (path.matches("/propuestas/\\d+")) {
                int propuestaId = Integer.parseInt(path.split("/")[2]);
                Freelancer f = freelancerDAO.obtenerPorUsuarioId(usuarioId);
                Propuesta p = propuestaDAO.obtenerPorId(propuestaId);
                if (p == null || p.getFreelancerId() != f.getId()) {
                    RespuestasServlet.notFound(resp, "Propuesta no encontrada"); return;
                }
                if (!"PENDIENTE".equals(p.getEstado())) {
                    RespuestasServlet.badRequest(resp, "Solo puedes eliminar propuestas pendientes"); return;
                }
                propuestaDAO.actualizarEstado(propuestaId, "RETIRADA");
                RespuestasServlet.ok(resp, "Propuesta eliminada");

            } else {
                RespuestasServlet.notFound(resp, "Ruta no encontrada");
            }
        } catch (Exception e) {
            RespuestasServlet.internalError(resp, e.getMessage());
        }
    }

    private Integer parseIntParam(HttpServletRequest req, String name) {
        String v = req.getParameter(name);
        return v != null ? Integer.parseInt(v) : null;
    }
    
    private Double parseDoubleParam(HttpServletRequest req, String name) {
        String v = req.getParameter(name);
        return v != null ? Double.parseDouble(v) : null;
    }
}
