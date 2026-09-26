package mx.uv.dsw.web1;
import java.io.IOException; import java.sql.SQLException; import javax.servlet.*; import javax.servlet.annotation.WebServlet; import javax.servlet.http.*;
@WebServlet("/catalog") public final class CatalogServlet extends HttpServlet {private final CatalogRepository repo=new CatalogRepository();
  @Override protected void doGet(HttpServletRequest req,HttpServletResponse res)throws ServletException,IOException{try{req.setAttribute("items",repo.findAll());req.getRequestDispatcher("/index.jsp").forward(req,res);}catch(SQLException e){throw new ServletException("PostgreSQL no disponible",e);}}
  @Override protected void doPost(HttpServletRequest req,HttpServletResponse res)throws ServletException,IOException{String name=req.getParameter("name");if(name==null||name.trim().length()<3){res.sendError(400,"name debe tener al menos 3 caracteres");return;}try{repo.create(name.trim());res.sendRedirect(req.getContextPath()+"/catalog");}catch(SQLException e){throw new ServletException("No fue posible guardar",e);}}
}
