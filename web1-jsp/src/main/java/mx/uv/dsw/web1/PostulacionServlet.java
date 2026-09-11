package mx.uv.dsw.web1;

import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Controlador de la postulacion de un estudiante a una organizacion (crea una practica).
 *
 * POST /postulacion -> parametros organizacionId, estudianteId, titulo.
 *   Casos negativos demostrables:
 *     - referencia inexistente (organizacion o estudiante) -> 400
 *     - postulacion duplicada (misma org, estudiante y titulo) -> 400
 *     - titulo con menos de 3 caracteres o id no numerico -> 400
 */
@WebServlet("/postulacion")
public final class PostulacionServlet extends HttpServlet {

    private final PracticasRepository repo = new PracticasRepository();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String titulo = req.getParameter("titulo");
        long organizacionId;
        long estudianteId;
        try {
            organizacionId = Long.parseLong(req.getParameter("organizacionId"));
            estudianteId = Long.parseLong(req.getParameter("estudianteId"));
        } catch (NumberFormatException e) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST,
                "Debe seleccionar una organizacion y un estudiante validos.");
            return;
        }
        try {
            repo.initSchema();
            repo.crearPostulacion(organizacionId, estudianteId, titulo);
            res.sendRedirect(req.getContextPath() + "/catalog");
        } catch (PracticasRepository.ReglaNegocioException e) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (SQLException e) {
            throw new ServletException("No fue posible registrar la postulacion", e);
        }
    }
}
