package dao;

import data.MyVert;
import data.RFIDLiftData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MyVertDao {
    
    private final static int LIFT_VERT1 = 200;
    private final static int LIFT_VERT2 = 300;
    private final static int LIFT_VERT3 = 400;
    private final static int LIFT_VERT4 = 500;
    
    private static MyVertDao myVertDaoInstance = null;
    
    public MyVertDao() {
        
    }
    
    // make sure there is only one thread to instantiate MyVertDao
    public static synchronized MyVertDao getMyVertDaoInstance() {
        
        if (myVertDaoInstance == null) {
            return new MyVertDao();
        }
        return myVertDaoInstance;
    }
    
    public void insertMyVert(Connection connection, MyVert myVert) throws SQLException {
        
        PreparedStatement preparedStmt = null;
        
        String insertMyVertSql = "INSERT INTO MyVert "
                + "(SkierID, DayNum, Vertical, LiftTimes) VALUES "
                + "(?,?,?,?)";
        
        try {
            // TODO: delete it after testing
            System.out.println("======Insert to MyVert Start======");
            
            preparedStmt = connection.prepareStatement(insertMyVertSql);
            
            preparedStmt.setInt(1, myVert.getSkierId());
            preparedStmt.setInt(2, myVert.getDayNum());
            preparedStmt.setInt(3, myVert.getVerticalMeters());
            preparedStmt.setInt(4, myVert.getLiftTimes());
            
            // execute insert SQL statement
            preparedStmt.executeUpdate();
            
            // TODO: delete it after testing
            System.out.println("======Insert to MyVert Succeed======");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // close preparedStatement
            if (preparedStmt != null) {
                preparedStmt.close();
            }
        }
    }
    
    public void updateMyVert(Connection connection, RFIDLiftData record) throws SQLException {
        
        PreparedStatement preparedStmt = null;
        
        String updateMyVertSql = "UPDATE MyVert"
                + " SET Vertical = Vertical + ?, LiftTimes = LiftTimes + 1"
                + " WHERE SkierID = ? && LiftID = ?";
        
        try {
            // TODO: delete it after testing
            System.out.println("======Update MyVert Start======");
            
            preparedStmt = connection.prepareStatement(updateMyVertSql);
            
            preparedStmt.setInt(1, getVertByLiftId(record.getLiftID()));
            preparedStmt.setInt(2, record.getSkierID());
            preparedStmt.setInt(3, record.getLiftID());
            
            // execute update SQL statement
            int colNum = preparedStmt.executeUpdate();
            
            // TODO: delete it after testing
            System.out.println("======Update MyVert Succeed======");
            
            if (colNum == 0) {
                insertMyVert(connection, new MyVert(record.getSkierID(), record.getLiftID(), 
                getVertByLiftId(record.getLiftID()), 1));
            }           
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // close preparedStatement
            if (preparedStmt != null) {
                preparedStmt.close();
            }
        }
    }
    
    public MyVert getMyVertBySkierIdAndLiftId(Connection connection, int skierId, int dayNum) throws SQLException {
        
        PreparedStatement preparedStmt = null;
        MyVert myVert = null;
        
        String selectMyVertSql = "SELECT * FROM MyVert"
                + " WHERE SkierID = ? && DayNum = ?";
        
        try {
            preparedStmt = connection.prepareStatement(selectMyVertSql);
            
            preparedStmt.setInt(1, skierId);
            preparedStmt.setInt(2, dayNum);
            
            ResultSet resultSet = preparedStmt.executeQuery();
            if (resultSet.next()) {
                int vertical = resultSet.getInt("Vertical");
                int liftTimes = resultSet.getInt("LiftTimes");
                myVert = new MyVert(skierId, dayNum, vertical, liftTimes);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // close preparedStatement
            if (preparedStmt != null) {
                preparedStmt.close();
            }
        }
        return myVert;
    }
    
    // helper method for getting vertical by lift ID
    private int getVertByLiftId(int liftId) {
        
        if (liftId <= 10) return LIFT_VERT1;
        else if (liftId <= 20) return LIFT_VERT2;
        else if (liftId <= 30) return LIFT_VERT3;
        else return LIFT_VERT4;
    }
}
