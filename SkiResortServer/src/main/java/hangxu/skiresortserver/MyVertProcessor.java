package hangxu.skiresortserver;

import dao.MyVertDao;
import data.RFIDLiftData;
import java.sql.Connection;
import java.sql.SQLException;

public class MyVertProcessor implements Runnable {
    
    Connection connection;
    RFIDLiftData record;
    
    public MyVertProcessor(Connection connection, RFIDLiftData record) {
        this.connection = connection;
        this.record = record;
    }
    
    @Override
    public void run() {
        try {
            MyVertDao.getMyVertDaoInstance().updateMyVert(connection, record);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
