import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.net.URLDecoder;

public class DatabaseHandler{
    
    private String locationPath;
    private static String DATABASE_NAME = "toilets";
    
    public DatabaseHandler(){
        locationPath = getClass().getClassLoader().getResource("").getPath();  // /G:/Documents/%23SCHOOL/P-Seminar%20Inf/Toilets%20Server/
        //System.out.println(URLDecoder.decode(locationPath)); 
        init();
    }
    
    private void init(){
        String dbUrl = "jdbc:sqlite:" + prepareUrlString(locationPath) + DATABASE_NAME + ".db";
        
        //String dbUrl = "jdbc:sqlite:G:\\Documents\\#SCHOOL\\P-Seminar Inf\\Toilets Server\\toilets.db";
        
        //String dbUrl = "jdbc:sqlite:G:\\Documents\\%23SCHOOL\\P-Seminar%20Inf\\Toilets%20Server\\toilets.db";
        
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
    
    public static String prepareUrlString(String u){
        String url = "";
        
        u = URLDecoder.decode(u);
        
        String[] splitted = u.split("\\/", -1);
        
        for(int i = 1; i < splitted.length; i++){
            url = url + splitted[i] + "\\";
        }
        
        return url;
    }
}