/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hangxu.skiresortserver;

import dao.ConnectionUtil;
import dao.MyVertDao;
import data.MyVert;
import data.RFIDLiftData;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 *
 * @author hannah
 */
public class TaskManager {
    
    private static TaskManager taskManagerInstance = null;
    
    public static TaskManager getTaskManagerInstance() {
        if (taskManagerInstance == null) {
            return new TaskManager();
        }
        return taskManagerInstance;
    }
    
    private Connection connectionOfRecord;
    private Connection connectionOfMyVert;
    private Connection connectionOfGET; 
    private ConnectionUtil connectionUtilOfRecord = new ConnectionUtil();
    private ConnectionUtil connectionUtilOfMyVert = new ConnectionUtil();
    private ConnectionUtil connectionUtilOfGET = new ConnectionUtil();
    private ExecutorService recordExecutor;
    private ExecutorService myVertExecutor;
    
    // Constructor of TaskManager class
    public TaskManager() {
        
        try {
            connectionOfRecord = connectionUtilOfRecord.getConnection();
            connectionOfMyVert = connectionUtilOfMyVert.getConnection();
            connectionOfGET = connectionUtilOfGET.getConnection();           
        } catch (SQLException e) {
            e.printStackTrace();
        }
        recordExecutor = Executors.newFixedThreadPool(4);
        myVertExecutor = Executors.newFixedThreadPool(2);
    }
    
    public synchronized void insertRecordToDB(RFIDLiftData record) {
        recordExecutor.submit(new RFIDLiftDataProcessor(connectionOfRecord, record));
        myVertExecutor.submit(new MyVertProcessor(connectionOfMyVert, record));
    }
    
    public MyVert getMyVertFromDB(int skierId, int dayNum) {
        MyVert myVert = null;
        try {
            myVert = MyVertDao.getMyVertDaoInstance().getMyVertBySkierIdAndLiftId(connectionOfGET, skierId, dayNum);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return myVert;
    }
}
