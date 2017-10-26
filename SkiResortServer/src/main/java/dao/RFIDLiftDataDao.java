package dao;

import data.RFIDLiftData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RFIDLiftDataDao {
    
    private static RFIDLiftDataDao rFIDDaoInstance = null;
    
    public void RFIDLiftDataDao() {
        
    }
    
    // make sure there is only one thread to instantiate RFIDLiftDataDao
    public static synchronized RFIDLiftDataDao getRFIDDaoInstance() {
        
        if (rFIDDaoInstance == null) {
            return new RFIDLiftDataDao();
        } 
        return rFIDDaoInstance;
    }
    
    public void insertRecord(Connection connection, RFIDLiftData record) throws SQLException {
        
        PreparedStatement preparedStmt = null;
        
        String insertRecordSql = "INSERT INTO Record "
                + "(ResortID, DayNum, SkierID, LiftID, Time) VALUES "
                + "(?,?,?,?,?)";
        
        try {
            preparedStmt = connection.prepareStatement(insertRecordSql);
            preparedStmt.setInt(1, record.getResortID());
            preparedStmt.setInt(2, record.getDayNum());
            preparedStmt.setInt(3, record.getSkierID());
            preparedStmt.setInt(4, record.getLiftID());
            preparedStmt.setInt(5, record.getTime());
            
            // execute insert SQL statement
            preparedStmt.executeUpdate();
            
            // TODO: delete it after testing
            System.out.println("======Insert Into Record Suceed======");
        } catch (SQLException e) {           
            e.printStackTrace();           
        } finally {
            // close preparedStatement
            if (preparedStmt != null) {
                preparedStmt.close();
            }
        }
        
    }
    
    /*
    public List<RFIDLiftData> getListOfRecordsBySkierIdAndDayNum(Connection connection, int skierId, int dayNum) {
        List<RFIDLiftData> listOfRecords = new ArrayList<>();
        String getRecordsSql = "SELECT * FROM Record WHERE SkierID=? AND DayNum=?";
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;
        try {
            preparedStmt = connection.prepareStatement(getRecordsSql);
            preparedStmt.setInt(1, skierId);
            preparedStmt.setInt(2, dayNum);
            resultSet = preparedStmt.executeQuery();
            while (resultSet.next()) {
                int resortId = resultSet.getInt("ResortID");
                int liftId = resultSet.getInt("LiftID");
                int time = resultSet.getInt("Time");
                RFIDLiftData record = new RFIDLiftData(resortId, dayNum, skierId, liftId, time);
                listOfRecords.add(record);
                
                // close preparedStatement
                preparedStmt.close();
                // close resultSet
                resultSet.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listOfRecords;
    }*/
}
