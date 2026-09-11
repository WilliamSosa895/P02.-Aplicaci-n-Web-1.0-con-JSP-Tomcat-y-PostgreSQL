package mx.uv.dsw.web1;

/** Vista de una practica (postulacion) con datos de organizacion y estudiante. */
public final class Postulacion {
    private final long id;
    private final String organizacion;
    private final String estudiante;
    private final String titulo;
    private final String estado;

    public Postulacion(long id, String organizacion, String estudiante, String titulo, String estado) {
        this.id = id;
        this.organizacion = organizacion;
        this.estudiante = estudiante;
        this.titulo = titulo;
        this.estado = estado;
    }

    public long getId() { return id; }
    public String getOrganizacion() { return organizacion; }
    public String getEstudiante() { return estudiante; }
    public String getTitulo() { return titulo; }
    public String getEstado() { return estado; }
}
