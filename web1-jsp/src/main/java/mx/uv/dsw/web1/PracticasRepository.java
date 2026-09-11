package mx.uv.dsw.web1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a datos del dominio PR10 (gestion de practicas profesionales) con JDBC
 * parametrizado. Concentra el SQL para que las vistas JSP solo presenten.
 *
 * El esquema se crea de forma idempotente al arrancar (initSchema), por lo que
 * el despliegue en contenedor no depende de ejecutar db/schema.sql a mano.
 * El archivo db/schema.sql conserva el mismo modelo para ejecucion local y
 * para revision humana.
 */
public final class PracticasRepository {

    /** Error de regla de negocio (se traduce a HTTP 400 en el Servlet). */
    public static final class ReglaNegocioException extends Exception {
        public ReglaNegocioException(String mensaje) { super(mensaje); }
    }

    private Connection open() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver PostgreSQL no disponible", e);
        }
        return DriverManager.getConnection(DbConfig.url(), DbConfig.user(), DbConfig.password());
    }

    /** Crea el modelo minimo de 6 entidades si no existe y siembra datos ficticios. */
    public void initSchema() throws SQLException {
        String[] ddl = {
            "create table if not exists organizacion(" +
                "id bigserial primary key," +
                "nombre varchar(100) not null," +
                "sector varchar(80)," +
                "contacto varchar(120)," +
                "creada_en timestamp not null default now()," +
                "constraint uq_organizacion_nombre unique(nombre)," +
                "constraint ck_organizacion_nombre_min check(char_length(btrim(nombre))>=3))",
            "create table if not exists estudiante_ficticio(" +
                "id bigserial primary key," +
                "nombre varchar(100) not null," +
                "programa varchar(100)," +
                "correo varchar(120)," +
                "constraint uq_estudiante_correo unique(correo)," +
                "constraint ck_estudiante_nombre_min check(char_length(btrim(nombre))>=3))",
            "create table if not exists practica(" +
                "id bigserial primary key," +
                "organizacion_id bigint not null references organizacion(id) on delete restrict," +
                "estudiante_id bigint not null references estudiante_ficticio(id) on delete restrict," +
                "titulo varchar(120) not null," +
                "plan varchar(300)," +
                "estado varchar(20) not null default 'POSTULADA'," +
                "creada_en timestamp not null default now()," +
                "constraint ck_practica_estado check(estado in " +
                    "('POSTULADA','ACEPTADA','EN_CURSO','CONCLUIDA','RECHAZADA'))," +
                "constraint uq_practica_postulacion unique(organizacion_id,estudiante_id,titulo))",
            "create table if not exists avance(" +
                "id bigserial primary key," +
                "practica_id bigint not null references practica(id) on delete cascade," +
                "descripcion varchar(300) not null," +
                "porcentaje integer not null default 0," +
                "registrado_en timestamp not null default now()," +
                "constraint ck_avance_porcentaje check(porcentaje between 0 and 100))",
            "create table if not exists evidencia(" +
                "id bigserial primary key," +
                "avance_id bigint not null references avance(id) on delete cascade," +
                "tipo varchar(40) not null," +
                "referencia varchar(200)," +
                "nota varchar(300))",
            "create table if not exists evento_plazo(" +
                "id bigserial primary key," +
                "practica_id bigint not null references practica(id) on delete cascade," +
                "tipo varchar(40) not null," +
                "instante timestamp not null default now()," +
                "unidad varchar(20)," +
                "valor varchar(60)," +
                "simulado boolean not null default true)"
        };
        try (Connection c = open(); Statement s = c.createStatement()) {
            for (String sql : ddl) {
                s.executeUpdate(sql);
            }
        }
        seedIfEmpty();
    }

    /** Siembra organizaciones y estudiantes ficticios solo si aun no hay datos. */
    private void seedIfEmpty() throws SQLException {
        try (Connection c = open()) {
            boolean vacio;
            try (Statement s = c.createStatement();
                 ResultSet r = s.executeQuery("select count(*) from organizacion")) {
                r.next();
                vacio = r.getInt(1) == 0;
            }
            if (!vacio) {
                return;
            }
            try (Statement s = c.createStatement()) {
                s.executeUpdate("insert into organizacion(nombre,sector,contacto) values " +
                    "('Soluciones Verdes SA (ficticia)','Tecnologia ambiental','contacto.ficticio@ejemplo.test')," +
                    "('Consultora Delta (ficticia)','Consultoria TI','rrhh.ficticio@ejemplo.test')," +
                    "('Cooperativa Norte (ficticia)','Logistica','enlace.ficticio@ejemplo.test') " +
                    "on conflict (nombre) do nothing");
                s.executeUpdate("insert into estudiante_ficticio(nombre,programa,correo) values " +
                    "('Ana Estudiante Ficticia','Ingenieria de Software','ana.ficticia@ejemplo.test')," +
                    "('Luis Estudiante Ficticio','Ingenieria de Software','luis.ficticio@ejemplo.test')," +
                    "('Sara Estudiante Ficticia','Redes y Telecomunicaciones','sara.ficticia@ejemplo.test') " +
                    "on conflict (correo) do nothing");
            }
        }
    }

    public List<Organizacion> listarOrganizaciones() throws SQLException {
        List<Organizacion> out = new ArrayList<>();
        try (Connection c = open();
             PreparedStatement p = c.prepareStatement(
                 "select id,nombre,sector from organizacion order by nombre");
             ResultSet r = p.executeQuery()) {
            while (r.next()) {
                out.add(new Organizacion(r.getLong(1), r.getString(2), r.getString(3)));
            }
        }
        return out;
    }

    public List<EstudianteFicticio> listarEstudiantes() throws SQLException {
        List<EstudianteFicticio> out = new ArrayList<>();
        try (Connection c = open();
             PreparedStatement p = c.prepareStatement(
                 "select id,nombre,programa from estudiante_ficticio order by nombre");
             ResultSet r = p.executeQuery()) {
            while (r.next()) {
                out.add(new EstudianteFicticio(r.getLong(1), r.getString(2), r.getString(3)));
            }
        }
        return out;
    }

    public List<Postulacion> listarPostulaciones() throws SQLException {
        List<Postulacion> out = new ArrayList<>();
        String sql = "select p.id, o.nombre, e.nombre, p.titulo, p.estado " +
                     "from practica p " +
                     "join organizacion o on o.id = p.organizacion_id " +
                     "join estudiante_ficticio e on e.id = p.estudiante_id " +
                     "order by p.id";
        try (Connection c = open();
             PreparedStatement p = c.prepareStatement(sql);
             ResultSet r = p.executeQuery()) {
            while (r.next()) {
                out.add(new Postulacion(r.getLong(1), r.getString(2),
                        r.getString(3), r.getString(4), r.getString(5)));
            }
        }
        return out;
    }

    /** Registra una organizacion. Valida longitud y unicidad de nombre. */
    public void crearOrganizacion(String nombre, String sector, String contacto)
            throws SQLException, ReglaNegocioException {
        if (nombre == null || nombre.trim().length() < 3) {
            throw new ReglaNegocioException("El nombre de la organizacion debe tener al menos 3 caracteres.");
        }
        try (Connection c = open();
             PreparedStatement p = c.prepareStatement(
                 "insert into organizacion(nombre,sector,contacto) values (?,?,?)")) {
            p.setString(1, nombre.trim());
            p.setString(2, sector == null || sector.isBlank() ? null : sector.trim());
            p.setString(3, contacto == null || contacto.isBlank() ? null : contacto.trim());
            p.executeUpdate();
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                throw new ReglaNegocioException("Ya existe una organizacion con ese nombre.");
            }
            throw e;
        }
    }

    /** Registra una postulacion (practica). Rechaza referencias inexistentes y duplicados. */
    public void crearPostulacion(long organizacionId, long estudianteId, String titulo)
            throws SQLException, ReglaNegocioException {
        if (titulo == null || titulo.trim().length() < 3) {
            throw new ReglaNegocioException("El titulo de la practica debe tener al menos 3 caracteres.");
        }
        try (Connection c = open();
             PreparedStatement p = c.prepareStatement(
                 "insert into practica(organizacion_id,estudiante_id,titulo) values (?,?,?)")) {
            p.setLong(1, organizacionId);
            p.setLong(2, estudianteId);
            p.setString(3, titulo.trim());
            p.executeUpdate();
        } catch (SQLException e) {
            String estado = e.getSQLState();
            if ("23503".equals(estado)) {
                throw new ReglaNegocioException(
                    "La organizacion o el estudiante indicado no existe.");
            }
            if ("23505".equals(estado)) {
                throw new ReglaNegocioException(
                    "Esa postulacion ya fue registrada (misma organizacion, estudiante y titulo).");
            }
            throw e;
        }
    }
}
