package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionUtil {
    
   private final static String USER_NAME = "hannah";
   private final static String PASSWORD = "xh19921022";
   private final static String DB_URL = "jdbc:mysql://";
   private final static String HOST = "hannahdbinstance.ckjt7k8h1ux3.us-east-2.rds.amazonaws.com";
   private final static int PORT = 3306;
   private final static String SCHEMA = "mydb";
   private final static String JDBC_DRIVER = "com.mysql.jdbc.Driver";
   
   public Connection getConnection() throws SQLException {
       
        // set up connection properties
        Properties properties = new Properties();
        properties.put("user", USER_NAME);
        properties.put("password", PASSWORD);
            
        System.out.println("======Starting MySQL JDBC======");
            
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
            
        System.out.println("======MySQL JDBC Driver Registered======");
            
        Connection connection = null;
       
        connection = DriverManager.getConnection(DB_URL + HOST + ":" + PORT + "/" + SCHEMA, properties); 
        
        if (connection != null) {
            System.out.println("======Connection Succeed======");
        } else {
            System.out.println("======Connection Failed======");
        }        
        return connection;     
    }
   
   // close connection
   public static void closeConnection(Connection connection) throws SQLException {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }
}
