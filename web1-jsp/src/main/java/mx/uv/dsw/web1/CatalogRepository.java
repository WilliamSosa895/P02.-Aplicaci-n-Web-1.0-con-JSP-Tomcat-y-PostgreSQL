package mx.uv.dsw.web1;
import java.sql.*; import java.util.*;
public final class CatalogRepository {
  private Connection open() throws SQLException {try{Class.forName("org.postgresql.Driver");}catch(ClassNotFoundException e){throw new SQLException("Driver PostgreSQL no disponible",e);}return DriverManager.getConnection(DbConfig.url(),DbConfig.user(),DbConfig.password());}
  public void initialize() throws SQLException {try(Connection c=open();Statement s=c.createStatement()){s.executeUpdate("create table if not exists catalog_item(id bigserial primary key,name varchar(100) not null)");}}
  public List<CatalogItem> findAll() throws SQLException {initialize();List<CatalogItem> out=new ArrayList<>();try(Connection c=open();PreparedStatement p=c.prepareStatement("select id,name from catalog_item order by id");ResultSet r=p.executeQuery()){while(r.next())out.add(new CatalogItem(r.getLong(1),r.getString(2)));}return out;}
  public void create(String name) throws SQLException {initialize();try(Connection c=open();PreparedStatement p=c.prepareStatement("insert into catalog_item(name) values (?)")){p.setString(1,name);p.executeUpdate();}}
}
