package mx.uv.dsw.web2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Capa de acceso a datos del incremento Web 2.0 (PR10).
 * Mantiene la tabla web2_record que verifica scripts/verify-module.sh M03:
 * la columna "name" es obligatoria y las columnas de dominio (estado, detalle)
 * son opcionales, de modo que un INSERT solo con name sigue siendo valido.
 * La configuracion se toma del entorno; sin secretos en el repositorio.
 */
public final class RegistroRepository {

    private Connection open() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver PostgreSQL no disponible", e);
        }
        String url  = System.getenv().getOrDefault("DB_URL", "jdbc:postgresql://localhost:5432/dsw02");
        String user = System.getenv().getOrDefault("DB_USER", "postgres");
        String pass = System.getenv().getOrDefault("DB_PASSWORD", "postgres");
        return DriverManager.getConnection(url, user, pass);
    }

    /** Crea la tabla del modulo de forma idempotente. name obligatorio; resto opcional. */
    public void initialize() throws SQLException {
        String ddl = "create table if not exists web2_record(" +
                "id bigserial primary key," +
                "name varchar(100) not null," +
                "estado varchar(20)," +
                "detalle varchar(300)," +
                "creado_en timestamp not null default now())";
        try (Connection c = open(); Statement s = c.createStatement()) {
            s.executeUpdate(ddl);
        }
    }

    public List<Registro> findAll() throws SQLException {
        initialize();
        List<Registro> out = new ArrayList<>();
        try (Connection c = open();
             PreparedStatement p = c.prepareStatement(
                 "select id, name, estado, detalle from web2_record order by id");
             ResultSet r = p.executeQuery()) {
            while (r.next()) {
                out.add(new Registro(r.getLong(1), r.getString(2), r.getString(3), r.getString(4)));
            }
        }
        return out;
    }

    /** Inserta un registro validado. estado y detalle pueden ser nulos. */
    public void create(String nombre, String estado, String detalle) throws SQLException {
        initialize();
        try (Connection c = open();
             PreparedStatement p = c.prepareStatement(
                 "insert into web2_record(name, estado, detalle) values (?,?,?)")) {
            p.setString(1, nombre);
            p.setString(2, estado == null || estado.isBlank() ? null : estado);
            p.setString(3, detalle == null || detalle.isBlank() ? null : detalle.trim());
            p.executeUpdate();
        }
    }
}
