package mx.uv.dsw.web2;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

/**
 * Managed bean de alcance de vista (incremento Web 2.0 de PR10).
 * Coordina el formulario JSF/PrimeFaces: captura, valida a nivel servidor,
 * persiste una postulacion de practica y actualiza la tabla por AJAX.
 */
@ManagedBean
@ViewScoped
public class RegistroBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nombre;   // titulo de la practica
    private String estado;   // estado de la postulacion
    private String detalle;  // detalle opcional

    private List<Registro> registros = new ArrayList<>();
    private final RegistroRepository repo = new RegistroRepository();

    private final List<String> estados = Arrays.asList(
            "POSTULADA", "ACEPTADA", "EN_CURSO", "CONCLUIDA", "RECHAZADA");

    @PostConstruct
    public void init() {
        reload();
    }

    public void guardar() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        // Validacion de servidor adicional a la de los componentes (no confiar solo en la vista).
        if (nombre == null || nombre.trim().length() < 3) {
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                    "El titulo debe tener al menos 3 caracteres.", null));
            return;
        }
        try {
            repo.create(nombre.trim(), estado, detalle);
            nombre = "";
            estado = null;
            detalle = "";
            reload();
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Postulacion registrada y persistida.", null));
        } catch (SQLException e) {
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "PostgreSQL no disponible.", null));
        }
    }

    private void reload() {
        try {
            registros = repo.findAll();
        } catch (SQLException e) {
            registros = new ArrayList<>();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_ERROR, "No fue posible leer los registros.", null));
        }
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getDetalle() { return detalle; }
    public void setDetalle(String detalle) { this.detalle = detalle; }

    public List<Registro> getRegistros() { return registros; }
    public List<String> getEstados() { return estados; }
}
