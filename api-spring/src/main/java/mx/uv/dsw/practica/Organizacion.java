package mx.uv.dsw.practica;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/** Organizacion ficticia que oferta practicas (entidad del modelo PR10). */
@Entity
public class Organizacion {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank @Size(max = 100)
    private String nombre;
    @Size(max = 80)
    private String sector;

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String v) { nombre = v; }
    public String getSector() { return sector; }
    public void setSector(String v) { sector = v; }
}
