package hangxu.skiresortserver;

import dao.RFIDLiftDataDao;
import data.RFIDLiftData;
import java.sql.Connection;
import java.sql.SQLException;

public class RFIDLiftDataProcessor implements Runnable {
    
    Connection connection;
    RFIDLiftData record;
    
    public RFIDLiftDataProcessor(Connection connection, RFIDLiftData record) {
        this.connection = connection;
        this.record = record;
    }
    
    @Override
    public void run() {
        try {
            RFIDLiftDataDao.getRFIDDaoInstance().insertRecord(connection, record);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
