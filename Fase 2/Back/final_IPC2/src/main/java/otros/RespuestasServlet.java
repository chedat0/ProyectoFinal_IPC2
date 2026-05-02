/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package otros;

import otros.GsonConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jeffm
 */
public class RespuestasServlet{
    public static void ok(HttpServletResponse res, Object data) throws IOException {
        res.setStatus(HttpServletResponse.SC_OK);
        writeJson(res, data);
    }

    public static void created(HttpServletResponse res, Object data) throws IOException {
        res.setStatus(HttpServletResponse.SC_CREATED);
        writeJson(res, data);
    }

    public static void error(HttpServletResponse res, int status, String message) throws IOException {
        res.setStatus(status);
        Map<String, Object> body = new HashMap<>();
        body.put("error", true);
        body.put("message", message);
        writeJson(res, body);
    }

    public static void badRequest(HttpServletResponse res, String message) throws IOException {
        error(res, HttpServletResponse.SC_BAD_REQUEST, message);
    }

    public static void unauthorized(HttpServletResponse res, String message) throws IOException {
        error(res, HttpServletResponse.SC_UNAUTHORIZED, message);
    }

    public static void forbidden(HttpServletResponse res, String message) throws IOException {
        error(res, HttpServletResponse.SC_FORBIDDEN, message);
    }

    public static void notFound(HttpServletResponse res, String message) throws IOException {
        error(res, HttpServletResponse.SC_NOT_FOUND, message);
    }

    public static void internalError(HttpServletResponse res, String message) throws IOException {
        error(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
    }

    private static void writeJson(HttpServletResponse res, Object data) throws IOException {
        res.setContentType("application/json;charset=UTF-8");
        res.getWriter().write(GsonConfig.get().toJson(data));
    }
    
    public static Map<String, Object> successBody(String message, Object data) {
        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        body.put("message", message);
        body.put("data", data);
        return body;
    }
}
