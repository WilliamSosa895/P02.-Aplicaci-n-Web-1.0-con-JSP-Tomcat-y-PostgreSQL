package mx.uv.dsw.practica;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/** Estudiante ficticio que se postula a una practica (entidad del modelo PR10). */
@Entity
public class EstudianteFicticio {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank @Size(max = 100)
    private String nombre;
    @Size(max = 100)
    private String programa;

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String v) { nombre = v; }
    public String getPrograma() { return programa; }
    public void setPrograma(String v) { programa = v; }
}
