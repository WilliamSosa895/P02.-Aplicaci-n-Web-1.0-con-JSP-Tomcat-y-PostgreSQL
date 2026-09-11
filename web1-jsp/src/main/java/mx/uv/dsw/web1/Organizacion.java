package mx.uv.dsw.web1;

/** Organizacion ficticia que oferta practicas. */
public final class Organizacion {
    private final long id;
    private final String nombre;
    private final String sector;

    public Organizacion(long id, String nombre, String sector) {
        this.id = id;
        this.nombre = nombre;
        this.sector = sector;
    }

    public long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getSector() { return sector; }
}
