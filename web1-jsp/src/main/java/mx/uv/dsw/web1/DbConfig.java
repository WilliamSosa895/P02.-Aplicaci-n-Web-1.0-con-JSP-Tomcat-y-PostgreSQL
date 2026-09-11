package mx.uv.dsw.web1;

/**
 * Configuracion de conexion tomada del entorno (nunca del repositorio).
 * Los valores por defecto corresponden al desarrollo local sin Docker.
 * En el laboratorio con Docker Compose, estas variables las inyecta el
 * contenedor (DB_URL, DB_USER, DB_PASSWORD) y sobreescriben los valores locales.
 * No se versionan credenciales reales; los datos son ficticios y locales.
 */
public final class DbConfig {
    private DbConfig() {}

    public static String url() {
        return System.getenv().getOrDefault("DB_URL", "jdbc:postgresql://localhost:5432/dsw02");
    }

    public static String user() {
        return System.getenv().getOrDefault("DB_USER", "postgres");
    }

    public static String password() {
        return System.getenv().getOrDefault("DB_PASSWORD", "postgres");
    }
}
