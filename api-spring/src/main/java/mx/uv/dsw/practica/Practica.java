package mx.uv.dsw.practica;

import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Recurso principal del dominio PR10: la practica (postulacion). Se relaciona
 * con organizacion y estudiante mediante sus identificadores; la integridad
 * referencial se define en el modelo PostgreSQL (llaves foraneas en db/schema.sql).
 */
@Entity
public class Practica {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "organizacion_id")
    private Long organizacionId;

    @NotNull
    @Column(name = "estudiante_id")
    private Long estudianteId;

    @NotBlank @Size(min = 3, max = 120)
    private String titulo;

    @NotBlank
    @Pattern(regexp = "POSTULADA|ACEPTADA|EN_CURSO|CONCLUIDA|RECHAZADA",
             message = "estado invalido")
    private String estado;

    private Instant creadaEn = Instant.now();

    public Long getId() { return id; }
    public Long getOrganizacionId() { return organizacionId; }
    public void setOrganizacionId(Long v) { organizacionId = v; }
    public Long getEstudianteId() { return estudianteId; }
    public void setEstudianteId(Long v) { estudianteId = v; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String v) { titulo = v; }
    public String getEstado() { return estado; }
    public void setEstado(String v) { estado = v; }
    public Instant getCreadaEn() { return creadaEn; }
    public void setCreadaEn(Instant v) { creadaEn = v; }
}
