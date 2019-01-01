import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseHandler{
    
    private String locationPath;
    private static String DATABASE_NAME = "toilets";
    
    public DatabaseHandler(){
        locationPath = getClass().getClassLoader().getResource("").getPath();
        init();
    }
    
    private void init(){
        String dbUrl = "jdbc:sqlite:" + locationPath + DATABASE_NAME + ".db";
        
        try (Connection conn = DriverManager.getConnection(dbUrl)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }
 
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}