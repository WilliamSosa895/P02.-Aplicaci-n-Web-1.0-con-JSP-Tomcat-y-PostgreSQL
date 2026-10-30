package mx.uv.dsw.practica;

/** Se lanza cuando no existe el recurso solicitado; el manejador la traduce a 404. */
public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
