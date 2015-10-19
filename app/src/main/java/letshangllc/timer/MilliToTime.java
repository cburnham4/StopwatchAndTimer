package letshangllc.timer;

import java.util.concurrent.TimeUnit;

/**
 * Created by cvburnha on 10/13/2015.
 */
public class MilliToTime {
    public MilliToTime(){

    }

    public int[] milliToTime(long milliseconds){
        int[] times = new int[4];

        int hours = (int) milliseconds / (60 * 60 * 1000);
        milliseconds -= hours * (60 * 60 * 1000);
        int minutes = (int) milliseconds / (60 * 1000);
        milliseconds -= minutes * (60 * 1000);
        int seconds = (int) milliseconds / 1000;

        int milli= (int) (milliseconds % 1000);
        milli /=  10;
        times[0] = hours;
        times[1] = minutes;
        times[2] = seconds;
        times[3] = milli;
        return times;
    }
}
