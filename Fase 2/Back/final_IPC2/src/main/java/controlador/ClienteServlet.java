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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author jeffm
 */

@WebServlet(name = "ClienteServlet", urlPatterns = {"/api/cliente/*"})
public class ClienteServlet extends HttpServlet {

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final ProyectoDAO proyectoDAO = new ProyectoDAO();
    private final PropuestaDAO propuestaDAO = new PropuestaDAO();
    private final ContratoDAO contratoDAO = new ContratoDAO();
    private final EntregaDAO entregaDAO = new EntregaDAO();
    private final CalificacionDAO calificacionDAO = new CalificacionDAO();
    private final RecargaDAO recargaDAO = new RecargaDAO();
    private final ComisionDAO comisionDAO = new ComisionDAO();
    private final ReporteDAO reporteDAO = new ReporteDAO();
    private final SolicitudDAO solicitudDAO = new SolicitudDAO();

    private Cliente getAuthenticatedCliente(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String token = JWT.extraerTokenDelHeader(req.getHeader("Authorization"));
        if (token == null || !JWT.esTokenValido(token)) {
            RespuestasServlet.unauthorized(res, "Token inválido o expirado");
            return null;
        }
        String rol = JWT.getRol(token);
        if (!"CLIENTE".equals(rol)) {
            RespuestasServlet.forbidden(res, "Acceso denegado");
            return null;
        }
        int userId = JWT.getUserId(token);
        return clienteDAO.obtenerPorUsuarioId(userId);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String path = req.getPathInfo();
        try {
            Cliente cliente = getAuthenticatedCliente(req, res);
            if (cliente == null) return;

            if ("/perfil".equals(path)) {
                RespuestasServlet.ok(res, cliente);
            } else if ("/recargas".equals(path)) {
                RespuestasServlet.ok(res, recargaDAO.obtenerPorCliente(cliente.getId()));
            } else if ("/proyectos".equals(path)) {
                String estado = req.getParameter("estado");
                RespuestasServlet.ok(res, proyectoDAO.obtenerPorCliente(cliente.getId(), estado));
            } else if (path != null && path.matches("/proyectos/\\d+")) {
                int proyId = Integer.parseInt(path.split("/")[2]);
                Proyecto p = proyectoDAO.obtenerPorId(proyId);
                if (p == null || !p.getClienteId().equals(cliente.getId())) {
                    RespuestasServlet.notFound(res, "Proyecto no encontrado"); return;
                }
                RespuestasServlet.ok(res, p);
            } else if (path != null && path.matches("/proyectos/\\d+/propuestas")) {
                int proyId = Integer.parseInt(path.split("/")[2]);
                Proyecto p = proyectoDAO.obtenerPorId(proyId);
                if (p == null || !p.getClienteId().equals(cliente.getId())) {
                    RespuestasServlet.notFound(res, "Proyecto no encontrado"); return;
                }
                RespuestasServlet.ok(res, propuestaDAO.obtenerPorProyecto(proyId));
            } else if ("/contratos".equals(path)) {
                String estado = req.getParameter("estado");
                RespuestasServlet.ok(res, contratoDAO.obtenerPorCliente(cliente.getId(), estado));
            } else if (path != null && path.matches("/contratos/\\d+")) {
                int cid = Integer.parseInt(path.split("/")[2]);
                Contrato c = contratoDAO.obtenerPorId(cid);
                if (c == null || !c.getClienteId().equals(cliente.getId())) {
                    RespuestasServlet.notFound(res, "Contrato no encontrado"); return;
                }
                RespuestasServlet.ok(res, c);
            } else if (path != null && path.matches("/contratos/\\d+/entregas")) {
                int cid = Integer.parseInt(path.split("/")[2]);
                RespuestasServlet.ok(res, entregaDAO.obtenerPorContrato(cid));
            } else if ("/reportes/proyectos".equals(path)) {
                String fi = req.getParameter("fechaInicio"); String ff = req.getParameter("fechaFin");
                if (fi == null || ff == null) { RespuestasServlet.badRequest(res, "Fechas requeridas"); return; }
                RespuestasServlet.ok(res, reporteDAO.historialProyectosCliente(cliente.getId(), fi, ff));
            } else if ("/reportes/recargas".equals(path)) {
                RespuestasServlet.ok(res, recargaDAO.obtenerPorCliente(cliente.getId()));
            } else if ("/reportes/gastos-categoria".equals(path)) {
                String fi = req.getParameter("fechaInicio"); String ff = req.getParameter("fechaFin");
                if (fi == null || ff == null) { RespuestasServlet.badRequest(res, "Fechas requeridas"); return; }
                RespuestasServlet.ok(res, reporteDAO.gastosPorCategoria(cliente.getId(), fi, ff));
            } else {
                RespuestasServlet.notFound(res, "Endpoint no encontrado");
            }
        } catch (Exception e) {
            RespuestasServlet.internalError(res, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String path = req.getPathInfo();
        try {
            Cliente cliente = getAuthenticatedCliente(req, res);
            if (cliente == null) return;

            if ("/recargas".equals(path)) {
                JsonObject body = GsonConfig.get().fromJson(req.getReader(), JsonObject.class);
                double monto = body.get("monto").getAsDouble();
                if (monto  <= 0) { RespuestasServlet.badRequest(res, "Monto inválido"); return; }
                clienteDAO.recargarSaldo(cliente.getId(), monto);
                RecargaSaldo r = new RecargaSaldo();
                r.setClienteId(cliente.getId());
                r.setMonto(monto);
                r.setMetodoPago(body.has("metodoPago") ? body.get("metodoPago").getAsString() : "TRANSFERENCIA");
                r.setReferencia(body.has("referencia") && !body.get("referencia").isJsonNull() ? body.get("referencia").getAsString() : null);
                r.setDescripcion(body.has("descripcion") ? body.get("descripcion").getAsString() : "Recarga de saldo");
                recargaDAO.create(r);
                RespuestasServlet.created(res, RespuestasServlet.successBody("Saldo recargado exitosamente", r));

            } else if ("/proyectos".equals(path)) {
                if (!Boolean.TRUE.equals(cliente.getPerfilCompleto())) {
                    RespuestasServlet.badRequest(res, "Debes completar tu perfil antes de publicar proyectos"); return;
                }
                JsonObject body = GsonConfig.get().fromJson(req.getReader(), JsonObject.class);
                Proyecto p = new Proyecto();
                p.setClienteId(cliente.getId());
                p.setCategoriaId(body.get("categoriaId").getAsInt());
                p.setTitulo(body.get("titulo").getAsString());
                p.setDescripcion(body.get("descripcion").getAsString());
                p.setPresupuestoMaximo(body.get("presupuestoMaximo").getAsDouble());
                p.setFechaLimite(java.time.LocalDate.parse(body.get("fechaLimite").getAsString()));
                List<Integer> hIds = new ArrayList<>();
                if (body.has("habilidadIds")) {
                    for (var e : body.get("habilidadIds").getAsJsonArray()) hIds.add(e.getAsInt());
                }
                proyectoDAO.ingresar(p, hIds);
                RespuestasServlet.created(res, p);

            } else if ("/solicitudes-categoria".equals(path)) {
                JsonObject body = GsonConfig.get().fromJson(req.getReader(), JsonObject.class);
                SolicitudCategoria s = new SolicitudCategoria();
                s.setClienteId(cliente.getId());
                s.setNombre(body.get("nombre").getAsString());
                s.setDescripcion(body.has("descripcion") ? body.get("descripcion").getAsString() : null);
                solicitudDAO.crearCategoria(s);
                RespuestasServlet.created(res, s);

            } else if (path != null && path.matches("/calificaciones")) {
                JsonObject body = GsonConfig.get().fromJson(req.getReader(), JsonObject.class);
                int contratoId = body.get("contratoId").getAsInt();
                if (calificacionDAO.existsPorContrato(contratoId)) {
                    RespuestasServlet.badRequest(res, "Ya calificaste este contrato"); return;
                }
                Contrato c = contratoDAO.obtenerPorId(contratoId);
                if (c == null || !c.getClienteId().equals(cliente.getId())) {
                    RespuestasServlet.forbidden(res, "No autorizado"); return;
                }
                Calificacion cal = new Calificacion();
                cal.setContratoId(contratoId); cal.setClienteId(cliente.getId()); cal.setFreelancerId(c.getFreelancerId());
                cal.setEstrellas(body.get("estrellas").getAsInt());
                cal.setComentario(body.has("comentario") ? body.get("comentario").getAsString() : null);
                calificacionDAO.ingresar(cal);
                new FreelancerDAO().actualizarCalificacion(c.getFreelancerId());
                RespuestasServlet.created(res, cal);
            } else {
                RespuestasServlet.notFound(res, "Endpoint no encontrado");
            }
        } catch (Exception e) {
            RespuestasServlet.internalError(res, e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String path = req.getPathInfo();
        try {
            Cliente cliente = getAuthenticatedCliente(req, res);
            if (cliente == null) return;

            if ("/perfil".equals(path)) {
                JsonObject body = GsonConfig.get().fromJson(req.getReader(), JsonObject.class);
                cliente.setNombreEmpresa(body.has("nombreEmpresa") && !body.get("nombreEmpresa").isJsonNull() ? body.get("nombreEmpresa").getAsString() : null);
                cliente.setDescripcion(body.has("descripcion") && !body.get("descripcion").isJsonNull() ? body.get("descripcion").getAsString() : null);
                cliente.setSector(body.has("sector") && !body.get("sector").isJsonNull() ? body.get("sector").getAsString() : null);
                cliente.setSitioWeb(body.has("sitioWeb") && !body.get("sitioWeb").isJsonNull() ? body.get("sitioWeb").getAsString() : null);
                cliente.setPais(body.has("pais") && !body.get("pais").isJsonNull() ? body.get("pais").getAsString() : "Guatemala");
                clienteDAO.actualizarPerfil(cliente);
                RespuestasServlet.ok(res, RespuestasServlet.successBody("Perfil actualizado", null));

            } else if (path != null && path.matches("/proyectos/\\d+")) {
                int proyId = Integer.parseInt(path.split("/")[2]);
                JsonObject body = GsonConfig.get().fromJson(req.getReader(), JsonObject.class);
                Proyecto p = proyectoDAO.obtenerPorId(proyId);
                if (p == null || !p.getClienteId().equals(cliente.getId())) {
                    RespuestasServlet.notFound(res, "Proyecto no encontrado"); return;
                }
                if (!"ABIERTO".equals(p.getEstado())) {
                    RespuestasServlet.badRequest(res, "Solo se pueden editar proyectos en estado ABIERTO"); return;
                }
                p.setCategoriaId(body.get("categoriaId").getAsInt());
                p.setTitulo(body.get("titulo").getAsString());
                p.setDescripcion(body.get("descripcion").getAsString());
                p.setPresupuestoMaximo(body.get("presupuestoMaximo").getAsDouble());
                p.setFechaLimite(java.time.LocalDate.parse(body.get("fechaLimite").getAsString()));
                List<Integer> hIds = new ArrayList<>();
                if (body.has("habilidadIds")) for (var e : body.get("habilidadIds").getAsJsonArray()) hIds.add(e.getAsInt());
                proyectoDAO.actualizar(p, hIds);
                RespuestasServlet.ok(res, p);

            } else if (path != null && path.matches("/propuestas/\\d+/aceptar")) {
                int propId = Integer.parseInt(path.split("/")[2]);
                Propuesta prop = propuestaDAO.obtenerPorId(propId);
                if (prop == null) { RespuestasServlet.notFound(res, "Propuesta no encontrada"); return; }
                Proyecto proy = proyectoDAO.obtenerPorId(prop.getProyectoId());
                if (!proy.getClienteId().equals(cliente.getId())) { RespuestasServlet.forbidden(res, "No autorizado"); return; }
                if (!"ABIERTO".equals(proy.getEstado())) { RespuestasServlet.badRequest(res, "El proyecto no está en estado ABIERTO"); return; }
                // Verificar saldo
                if (cliente.getSaldoDisponible() < prop.getMontoOfertado()) {
                    RespuestasServlet.badRequest(res, "Saldo insuficiente para aceptar esta propuesta"); return;
                }
                // Bloquear saldo
                clienteDAO.bloquearSaldo(cliente.getId(), prop.getMontoOfertado());
                // Crear contrato
                double comision = comisionDAO.obtenerComisionActual();
                double comisionMonto = (Math.round(prop.getMontoOfertado() * comision / 100.0)* 100.0) / 100.0;
                Contrato c = new Contrato();
                c.setPropuestaId(propId); c.setProyectoId(proy.getId()); c.setClienteId(cliente.getId());
                c.setFreelancerId(prop.getFreelancerId()); c.setMonto(prop.getMontoOfertado()); c.setPorcentajeComision(comision); c.setComisionMonto(comisionMonto);
                contratoDAO.ingresar(c);                
                propuestaDAO.actualizarEstado(propId, "ACEPTADA");
                proyectoDAO.actualizarEstado(proy.getId(), "EN_PROGRESO");
                // Rechazar otras propuestas pendientes
                propuestaDAO.obtenerPorProyecto(proy.getId()).stream()
                    .filter(pr -> "PENDIENTE".equals(pr.getEstado()) && !pr.getId().equals(propId))
                    .forEach(pr -> { try { propuestaDAO.actualizarEstado(pr.getId(), "RECHAZADA"); } catch (Exception ignored) {} });
                RespuestasServlet.ok(res, RespuestasServlet.successBody("Propuesta aceptada. Contrato creado.", c));

            } else if (path != null && path.matches("/propuestas/\\d+/rechazar")) {
                int propId = Integer.parseInt(path.split("/")[2]);
                Propuesta prop = propuestaDAO.obtenerPorId(propId);
                if (prop == null) { RespuestasServlet.notFound(res, "Propuesta no encontrada"); return; }
                propuestaDAO.actualizarEstado(propId, "RECHAZADA");
                RespuestasServlet.ok(res, RespuestasServlet.successBody("Propuesta rechazada", null));

            } else if (path != null && path.matches("/entregas/\\d+/aprobar")) {
                int entregaId = Integer.parseInt(path.split("/")[2]);
                Entrega entrega = entregaDAO.obtenerPorId(entregaId);
                if (entrega == null) { RespuestasServlet.notFound(res, "Entrega no encontrada"); return; }
                Contrato contrato = contratoDAO.obtenerPorId(entrega.getContratoId());
                if (!contrato.getClienteId().equals(cliente.getId())) { RespuestasServlet.forbidden(res, "No autorizado"); return; }
                // Aprobar entrega
                entregaDAO.actualizarEstado(entregaId, "APROBADA", null);
                // Liberar pago al freelancer
                double montoFreelancer = Math.round((contrato.getMonto() - contrato.getComisionMonto()) * 100.0 ) / 100.0;
                new FreelancerDAO().acreditarSaldo(contrato.getFreelancerId(), montoFreelancer);
                // Descontar saldo bloqueado del cliente
                clienteDAO.descontarSaldoBloqueado(cliente.getId(), contrato.getMonto());
                // Completar contrato y proyecto
                contratoDAO.completar(contrato.getId());
                proyectoDAO.actualizarEstado(contrato.getProyectoId(), "COMPLETADO");
                RespuestasServlet.ok(res, RespuestasServlet.successBody("Entrega aprobada. Pago liberado al freelancer.", null));

            } else if (path != null && path.matches("/entregas/\\d+/rechazar")) {
                int entregaId = Integer.parseInt(path.split("/")[2]);
                JsonObject body = GsonConfig.get().fromJson(req.getReader(), JsonObject.class);
                String motivo = body.get("motivo").getAsString();
                Entrega entrega = entregaDAO.obtenerPorId(entregaId);
                if (entrega == null) { RespuestasServlet.notFound(res, "Entrega no encontrada"); return; }
                Contrato contrato = contratoDAO.obtenerPorId(entrega.getContratoId());
                if (!contrato.getClienteId().equals(cliente.getId())) { RespuestasServlet.forbidden(res, "No autorizado"); return; }
                entregaDAO.actualizarEstado(entregaId, "RECHAZADA", motivo);
                proyectoDAO.actualizarEstado(contrato.getProyectoId(), "EN_PROGRESO");
                RespuestasServlet.ok(res, RespuestasServlet.successBody("Entrega rechazada. El freelancer puede subir una nueva.", null));

            } else if (path != null && path.matches("/contratos/\\d+/cancelar")) {
                int cid = Integer.parseInt(path.split("/")[2]);
                JsonObject body = GsonConfig.get().fromJson(req.getReader(), JsonObject.class);
                String motivo = body.get("motivo").getAsString();
                if (motivo == null || motivo.trim().isEmpty()) { RespuestasServlet.badRequest(res, "El motivo de cancelación es obligatorio"); return; }
                Contrato c = contratoDAO.obtenerPorId(cid);
                if (c == null || !c.getClienteId().equals(cliente.getId())) { RespuestasServlet.notFound(res, "Contrato no encontrado"); return; }
                if (!"ACTIVO".equals(c.getEstado())) { RespuestasServlet.badRequest(res, "El contrato no está activo"); return; }
                // Devolver saldo bloqueado al cliente
                clienteDAO.liberarSaldoBloqueado(cliente.getId(), c.getMonto());
                clienteDAO.recargarSaldo(cliente.getId(), c.getMonto());
                // Cancelar contrato y proyecto
                contratoDAO.cancelar(cid, motivo);
                proyectoDAO.actualizarEstado(c.getProyectoId(), "CANCELADO");
                RespuestasServlet.ok(res, RespuestasServlet.successBody("Contrato cancelado. Saldo devuelto.", null));
            } else {
                RespuestasServlet.notFound(res, "Endpoint no encontrado");
            }
        } catch (Exception e) {
            RespuestasServlet.internalError(res, e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String path = req.getPathInfo();
        try {
            Cliente cliente = getAuthenticatedCliente(req, res);
            if (cliente == null) return;
            if (path != null && path.matches("/proyectos/\\d+")) {
                int proyId = Integer.parseInt(path.split("/")[2]);
                Proyecto p = proyectoDAO.obtenerPorId(proyId);
                if (p == null || !p.getClienteId().equals(cliente.getId())) { RespuestasServlet.notFound(res, "Proyecto no encontrado"); return; }
                if (!"ABIERTO".equals(p.getEstado())) { RespuestasServlet.badRequest(res, "Solo se pueden cancelar proyectos ABIERTOS"); return; }
                proyectoDAO.actualizarEstado(proyId, "CANCELADO");
                RespuestasServlet.ok(res, RespuestasServlet.successBody("Proyecto cancelado", null));
            } else {
                RespuestasServlet.notFound(res, "Endpoint no encontrado");
            }
        } catch (Exception e) {
            RespuestasServlet.internalError(res, e.getMessage());
        }
    }

}
