import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Random;
import java.util.ArrayList;

import java.net.URLDecoder;

public class DatabaseHandler{
    
    private String dbUrl;
    private static final String DATABASE_NAME = "toilets";
    private static final String TABLE_NAME = "toilets";
    
    public DatabaseHandler(){
        String locationPath = getClass().getClassLoader().getResource("").getPath();  // /G:/Documents/%23SCHOOL/P-Seminar%20Inf/Toilets%20Server/
        //System.out.println(URLDecoder.decode(locationPath)); 
        dbUrl = "jdbc:sqlite:" + prepareUrlString(locationPath) + DATABASE_NAME + ".db";
        
        //only creates new database or table if not exists
        createNewDatabase();
        createNewTable();
    }
    
    private void createNewDatabase(){
        //String dbUrl = "jdbc:sqlite:G:\\Documents\\#SCHOOL\\P-Seminar Inf\\Toilets Server\\toilets.db";
        
        //String dbUrl = "jdbc:sqlite:G:\\Documents\\%23SCHOOL\\P-Seminar%20Inf\\Toilets%20Server\\toilets.db";
        
        try (Connection conn = connect();) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                //System.out.println("The driver name is " + meta.getDriverName());
                //System.out.println("A new database has been created.");
            }
 
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    private void createNewTable(){
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
                            + " id integer NOT NULL PRIMARY KEY UNIQUE,"
                            + " title text NOT NULL,"
                            + " lat double NOT NULL,"
                            + " lng double NOT NULL,"
                            + " description text,"
                            + " rating float NOT NULL,"
                            + " price float NOT NULL,"
                            + " currency text NOT NULL"
                            + ");";
                            
        try (Connection conn = connect();
                Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public void insert(String title, double lat, double lng, String description, 
                    float rating, float price, String currency){
        String sql = "INSERT INTO " + TABLE_NAME + " VALUES(?,?,?,?,?,?,?,?)";
        
        int id = generateID();
        
        try(Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, title);
            pstmt.setDouble(3, lat);
            pstmt.setDouble(4, lng);
            pstmt.setString(5, description);
            pstmt.setFloat(6, rating);
            pstmt.setFloat(7, price);
            pstmt.setString(8, currency);
            pstmt.executeUpdate();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    public ArrayList<String[]> getToiletsInRange(double latBot, double latTop, double lngLeft, double lngRight){
        ArrayList<String[]> toilets = new ArrayList<>(); 
                        
        try{
            Connection conn = connect();
            Statement stmt = conn.createStatement();
            String sql;
            if(lngLeft <= lngRight){
                sql = "SELECT title, lat, lng, description, rating, price, currency FROM " 
                            + TABLE_NAME 
                            + " WHERE lat>" + latBot 
                            + " AND lat<" + latTop
                            + " AND lng>" + lngLeft 
                            + " AND lng<" + lngRight;
            }else{
                sql = "SELECT title, lat, lng, description, rating, price, currency FROM " 
                            + TABLE_NAME 
                            + " WHERE lat>" + latBot 
                            + " AND lat<" + latTop
                            + " AND NOT(lng>" + lngRight 
                            + " AND lng<" + lngLeft
                            + ")";
            }
            ResultSet rs = stmt.executeQuery(sql);
            
            while(rs.next()){
                String[] toilet = new String[7];    //7 parameters
                for(int i = 1; i <= 7; i++){
                    toilet[i-1] = rs.getString(i);
                }
                toilets.add(toilet);
            }
                
        }catch(SQLException e){
                System.out.println(e.getMessage());
        }
        
        return toilets;
    }
    
    private Connection connect() {
        // SQLite connection string
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(dbUrl);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
    
    private boolean checkID(int id){
        boolean exists = false;
        
        String sql = "SELECT id FROM " + TABLE_NAME + " WHERE id=" + id;
        
        try(Connection conn = connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
                
            if(rs.next()){ 
                exists = true;
            }
                
        }catch(SQLException e){
                System.out.println(e.getMessage());
        }
        
        return exists;
    }
    
    private int generateID(){
        int id;
        do{
            id = randInt(0, 1000000);
            System.out.println("generating");
        }while(checkID(id));
        return id;
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
    
    public static int randInt(int min, int max){
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }
}