package mx.uv.dsw.web2;
import java.sql.*; import java.util.*;
public final class RegistroRepository {
  private Connection open() throws SQLException {try{Class.forName("org.postgresql.Driver");}catch(ClassNotFoundException e){throw new SQLException("Driver PostgreSQL no disponible",e);}String url=System.getenv().getOrDefault("DB_URL","jdbc:postgresql://localhost:5432/dsw");String user=System.getenv().getOrDefault("DB_USER","dsw");String pass=System.getenv().getOrDefault("DB_PASSWORD","dsw_local");return DriverManager.getConnection(url,user,pass);}
  public void initialize() throws SQLException {try(Connection c=open();Statement s=c.createStatement()){s.executeUpdate("create table if not exists web2_record(id bigserial primary key,name varchar(100) not null)");}}
  public List<String> findAll() throws SQLException {initialize();List<String> out=new ArrayList<>();try(Connection c=open();PreparedStatement p=c.prepareStatement("select name from web2_record order by id");ResultSet r=p.executeQuery()){while(r.next())out.add(r.getString(1));}return out;}
  public void create(String name) throws SQLException {initialize();try(Connection c=open();PreparedStatement p=c.prepareStatement("insert into web2_record(name) values (?)")){p.setString(1,name);p.executeUpdate();}}
}
