package mx.uv.dsw.web1;
import java.io.IOException; import javax.servlet.annotation.WebServlet; import javax.servlet.http.*;
@WebServlet("/health") public final class HealthServlet extends HttpServlet {
  @Override protected void doGet(HttpServletRequest req,HttpServletResponse res)throws IOException {res.setContentType("application/json");res.setCharacterEncoding("UTF-8");res.getWriter().write("{\"status\":\"UP\",\"stage\":\"web1\"}");}
}
