package mx.uv.dsw.web1;
public final class DbConfig { private DbConfig(){} public static String url(){return System.getenv().getOrDefault("DB_URL","jdbc:postgresql://localhost:5432/dsw");} public static String user(){return System.getenv().getOrDefault("DB_USER","dsw");} public static String password(){return System.getenv().getOrDefault("DB_PASSWORD","dsw_local");} }
