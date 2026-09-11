package mx.uv.dsw.web1;

/** Estudiante ficticio que puede postularse a una practica. */
public final class EstudianteFicticio {
    private final long id;
    private final String nombre;
    private final String programa;

    public EstudianteFicticio(long id, String nombre, String programa) {
        this.id = id;
        this.nombre = nombre;
        this.programa = programa;
    }

    public long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getPrograma() { return programa; }
}
