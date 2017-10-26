package data;

public class MyVert {
    private int skierId;
    private int dayNum;
    private int liftTimes;
    private int verticalMeters;

    public MyVert() {

    }

    public MyVert(int skierId, int dayNum, int liftTimes, int verticalMeters) {
        this.skierId = skierId;
        this.dayNum = dayNum;
        this.liftTimes = liftTimes;
        this.verticalMeters = verticalMeters;
    }
    
    public void setSkierId(int skierId) {
        this.skierId = skierId;
    }
    
    public int getSkierId() {
        return skierId;
    }
    
    public void setDayNum(int dayNum) {
        this.dayNum = dayNum;
    }
    
    public int getDayNum() {
        return dayNum;
    }

    public void setLiftTimes(int liftTimes) {
        this.liftTimes = liftTimes;
    }

    public int getLiftTimes() {
        return liftTimes;
    }

    public void setVerticalMeters(int verticalMeters) {
        this.verticalMeters = verticalMeters;
    }

    public int getVerticalMeters() {
        return verticalMeters;
    }
}
