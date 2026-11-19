package mx.uv.dsw.telemetry;

/**
 * Reglas de análisis de la telemetría (P06). Clasifican cada lectura segun su
 * valor y su variable. Son deterministas y reproducibles.
 *
 *  - ANOMALA: valor fuera del rango fisico esperado para la variable (dato anomalo).
 *  - ALERTA : valor dentro de rango pero por encima del umbral de atencion.
 *  - NORMAL : valor dentro de la operacion esperada.
 */
public final class Rules {
    private Rules() { }

    public static final String NORMAL = "NORMAL";
    public static final String ALERTA = "ALERTA";
    public static final String ANOMALA = "ANOMALA";

    /** Clasifica una lectura. variable puede ser nula (se asume temperatura). */
    public static String clasificar(String variable, double valor) {
        String v = variable == null ? "temperatura" : variable.toLowerCase();
        switch (v) {
            case "humedad":
                if (valor < 0 || valor > 100) return ANOMALA;
                return valor >= 85 ? ALERTA : NORMAL;
            case "temperatura":
            default:
                if (valor < -10 || valor > 60) return ANOMALA;
                return valor >= 30 ? ALERTA : NORMAL;
        }
    }
}
