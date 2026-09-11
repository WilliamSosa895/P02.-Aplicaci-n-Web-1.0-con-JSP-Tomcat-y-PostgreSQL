package mx.uv.dsw.web1;

import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Controlador del catalogo (flujo principal Web 1.0 de PR10).
 *
 * GET  /catalog  -> lista organizaciones, estudiantes y postulaciones; presenta index.jsp.
 * POST /catalog  -> registra una organizacion (parametro "name"). Un nombre menor a
 *                   3 caracteres se rechaza con 400 (caso negativo demostrable).
 *
 * El parametro "name" se conserva para mantener el contrato que verifica
 * scripts/verify-module.sh M02 (POST name=... y GET que recupera el dato).
 */
@WebServlet("/catalog")
public final class CatalogServlet extends HttpServlet {

    private final PracticasRepository repo = new PracticasRepository();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        try {
            repo.initSchema();
            req.setAttribute("organizaciones", repo.listarOrganizaciones());
            req.setAttribute("estudiantes", repo.listarEstudiantes());
            req.setAttribute("postulaciones", repo.listarPostulaciones());
            req.setAttribute("error", req.getParameter("error"));
            req.getRequestDispatcher("/index.jsp").forward(req, res);
        } catch (SQLException e) {
            throw new ServletException("PostgreSQL no disponible", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String name = req.getParameter("name");
        String sector = req.getParameter("sector");
        try {
            repo.initSchema();
            repo.crearOrganizacion(name, sector, null);
            res.sendRedirect(req.getContextPath() + "/catalog");
        } catch (PracticasRepository.ReglaNegocioException e) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (SQLException e) {
            throw new ServletException("No fue posible guardar la organizacion", e);
        }
    }
}
