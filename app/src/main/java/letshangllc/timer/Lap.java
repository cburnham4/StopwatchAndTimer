package letshangllc.timer;

/**
 * Created by cvburnha on 10/5/2015.
 */
public class Lap {


    int number;
    MyTime lapTime;
    MyTime overallTime;

    public Lap(int number, MyTime lapTime, MyTime overallTime) {
        this.number = number;
        this.lapTime = lapTime;
        this.overallTime = overallTime;
    }

    public int getNumber() {
        return number;
    }

    public MyTime getLapTime() {
        return lapTime;
    }

    public MyTime getOverallTime() {
        return overallTime;
    }




}
