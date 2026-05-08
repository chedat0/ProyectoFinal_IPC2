package controlador;

import otros.GsonConfig;
import daos.CategoriaDAO;
import daos.HabilidadDAO;
import modelo.Categoria;
import modelo.Habilidad;
import otros.RespuestasServlet;
import com.google.gson.Gson;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "CatalogoServlet", urlPatterns = {"/api/catalogo/*"})
public class CatalogoServlet extends HttpServlet {

    private final CategoriaDAO categoriaDAO = new CategoriaDAO();
    private final HabilidadDAO habilidadDAO = new HabilidadDAO();
    private final Gson gson = GsonConfig.getGson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        if (path == null) path = "/";

        try {
            if (path.equals("/categorias")) {
                List<Categoria> categorias = categoriaDAO.obtenerTodas(true);
                RespuestasServlet.ok(resp, gson.toJsonTree(categorias));

            } else if (path.equals("/habilidades")) {
                Integer catId = req.getParameter("categoriaId") != null
                        ? Integer.parseInt(req.getParameter("categoriaId")) : null;
                List<Habilidad> habilidades = habilidadDAO.obtenerTodas(catId, true);
                RespuestasServlet.ok(resp, gson.toJsonTree(habilidades));

            } else {
                RespuestasServlet.notFound(resp, "Ruta no encontrada");
            }
        } catch (Exception e) {
            RespuestasServlet.internalError(resp, e.getMessage());
        }
    }
}
