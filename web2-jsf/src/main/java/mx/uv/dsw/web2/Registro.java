package mx.uv.dsw.web2;

/**
 * Registro presentado en la tabla PrimeFaces (incremento Web 2.0 de PR10).
 * Corresponde a una fila de web2_record: un registro de postulacion de practica.
 * "nombre" es el titulo de la practica; "estado" y "detalle" enriquecen el flujo.
 */
public final class Registro {
    private final long id;
    private final String nombre;
    private final String estado;
    private final String detalle;

    public Registro(long id, String nombre, String estado, String detalle) {
        this.id = id;
        this.nombre = nombre;
        this.estado = estado;
        this.detalle = detalle;
    }

    public long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEstado() { return estado; }
    public String getDetalle() { return detalle; }
}
