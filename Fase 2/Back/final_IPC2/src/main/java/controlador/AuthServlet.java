/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import otros.GsonConfig;
import daos.*;
import modelo.*;
import otros.JWT;
import otros.RespuestasServlet;

import com.google.gson.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jeffm
 */

@WebServlet(name = "AuthServlet", urlPatterns = {"/api/auth/*"})
public class AuthServlet extends HttpServlet{
    
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final FreelancerDAO freelancerDAO = new FreelancerDAO();
    private final ComisionDAO comisionDAO = new ComisionDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String path = req.getPathInfo();
        try {
            if ("/register".equals(path)) {
                handleRegister(req, res);
            } else if ("/login".equals(path)) {
                handleLogin(req, res);
            } else {                
                RespuestasServlet.notFound(res, "Endpoint no encontrado");            }
        } catch (Exception e) {
            RespuestasServlet.internalError(res, "Error interno: " + e.getMessage());
        }
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse res) throws Exception {
        JsonObject body = GsonConfig.get().fromJson(req.getReader(), JsonObject.class);

        String nombreCompleto = body.get("nombreCompleto").getAsString().trim();
        String username = body.get("username").getAsString().trim().toLowerCase();
        String correo = body.get("correo").getAsString().trim().toLowerCase();
        String password = body.get("password").getAsString();
        String telefono = body.get("telefono").getAsString().trim();
        String direccion = body.get("direccion").getAsString().trim();
        String cui = body.get("cui").getAsString().trim();
        String fechaNacStr = body.get("fechaNacimiento").getAsString();
        String tipoUsuario = body.get("tipoUsuario").getAsString().toUpperCase();

        // Validaciones
        if (nombreCompleto.isEmpty() || username.isEmpty() || correo.isEmpty() || password.isEmpty()) {
            RespuestasServlet.badRequest(res, "Todos los campos obligatorios son requeridos");
            return;
        }
        if (!tipoUsuario.equals("CLIENTE") && !tipoUsuario.equals("FREELANCER")) {
            RespuestasServlet.badRequest(res, "Tipo de usuario inválido");
            return;
        }
        if (password.length() < 6) {
            RespuestasServlet.badRequest(res, "La contraseña debe tener al menos 6 caracteres");
            return;
        }
        if (usuarioDAO.existePorUsuario(username)) {
            RespuestasServlet.badRequest(res, "El nombre de usuario ya está en uso");
            return;
        }
        if (usuarioDAO.existePorCorreo(correo)) {
            RespuestasServlet.badRequest(res, "El correo electrónico ya está registrado");
            return;
        }
        if (usuarioDAO.existePorDPI(cui)) {
            RespuestasServlet.badRequest(res, "El DPI ya está registrado");
            return;
        }

        Usuario u = new Usuario();
        u.setNombreCompleto(nombreCompleto);
        u.setUsername(username);
        u.setCorreo(correo);
        u.setPasswordHash(BCrypt.hashpw(password, BCrypt.gensalt(12)));
        u.setTelefono(telefono);
        u.setDireccion(direccion);
        u.setCui(cui);
        u.setFechaNacimiento(LocalDate.parse(fechaNacStr));
        u.setTipoUsuario(tipoUsuario);
        usuarioDAO.ingresar(u);

        if (tipoUsuario.equals("CLIENTE")) {
            clienteDAO.ingresar(u.getId());
        } else {
            freelancerDAO.ingresar(u.getId());
        }

        String token = JWT.generarToken(u.getId(), u.getUsername(), u.getTipoUsuario());
        Map<String, Object> resp = new HashMap<>();
        resp.put("token", token);
        resp.put("userId", u.getId());
        resp.put("username", u.getUsername());
        resp.put("tipoUsuario", u.getTipoUsuario());
        resp.put("perfilCompleto", false);
        RespuestasServlet.created(res, resp);
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse res) throws Exception {
        JsonObject body = GsonConfig.get().fromJson(req.getReader(), JsonObject.class);
        String username = body.get("username").getAsString().trim().toLowerCase();
        String password = body.get("password").getAsString();

        Usuario u = usuarioDAO.obtenerPorUsuario(username);
        if (u == null) {
            RespuestasServlet.unauthorized(res, "Credenciales incorrectas");
            return;
        }
        if (!u.getActivo()) {
            RespuestasServlet.forbidden(res, "Tu cuenta ha sido desactivada. Contacta al administrador.");
            return;
        }
        if (!BCrypt.checkpw(password, u.getPasswordHash())) {
            RespuestasServlet.unauthorized(res, "Credenciales incorrectas");
            return;
        }

        usuarioDAO.actualizarUltimaSesion(u.getId());

        boolean perfilCompleto = false;
        if ("CLIENTE".equals(u.getTipoUsuario())) {
            Cliente c = clienteDAO.obtenerPorUsuarioId(u.getId());
            perfilCompleto = c != null && Boolean.TRUE.equals(c.getPerfilCompleto());
        } else if ("FREELANCER".equals(u.getTipoUsuario())) {
            Freelancer f = freelancerDAO.obtenerPorUsuarioId(u.getId());
            perfilCompleto = f != null && Boolean.TRUE.equals(f.getPerfilCompleto());
        } else if ("ADMINISTRADOR".equals(u.getTipoUsuario())) {
            perfilCompleto = true;
        }

        String token = JWT.generarToken(u.getId(), u.getUsername(), u.getTipoUsuario());
        Map<String, Object> resp = new HashMap<>();
        resp.put("token", token);
        resp.put("userId", u.getId());
        resp.put("username", u.getUsername());
        resp.put("nombreCompleto", u.getNombreCompleto());
        resp.put("tipoUsuario", u.getTipoUsuario());
        resp.put("perfilCompleto", perfilCompleto);
        RespuestasServlet.ok(res, resp);
    }
}
