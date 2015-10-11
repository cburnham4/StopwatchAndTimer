package letshangllc.timer;

/**
 * Created by cvburnha on 10/5/2015.
 */
public class MyTime {
    int hour;
    int minute;
    int second;
    int millisecond;

    public MyTime(int hour, int minute, int second, int millisecond) {
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        this.millisecond = millisecond;
    }

    public String toString(){
        if(minute<10){
            return ("0" + minute + ":" + String.format("%02d", second) + ":"
                    + String.format("%02d", millisecond));
        }else{
            return ("0" + minute + ":" + String.format("%02d", second) + ":"
                    + String.format("%02d", millisecond));
        }
    }
}
