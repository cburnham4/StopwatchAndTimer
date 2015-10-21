package letshangllc.timer;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.codetroopers.betterpickers.timepicker.*;

import android.os.Handler;
import android.widget.Toast;

import java.util.logging.LogRecord;

public class Timer extends Fragment{
    TextView tv;
    TextView tv_hours, tv_minutes, tv_seconds;
    Button btn_start, btn_reset;

    long timeInMilliseconds = 0L;
    long milliseconds = 0L;
    long start_time;

    int hours;
    int minutes;
    int seconds;

    public static final String PREFS_NAME = "TimePrefs";

    boolean running = false;

    MilliToTime milliToTime = new MilliToTime();
    Handler handler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_timer, container, false);

        final Context context = this.getContext();

        tv_hours = (TextView) view.findViewById(R.id.tv_hour);
        tv_minutes = (TextView) view.findViewById(R.id.tv_minute);
        tv_seconds = (TextView) view.findViewById(R.id.tv_second);

        tv_hours.setText("00");
        tv_minutes.setText("00");
        tv_seconds.setText("10");

        tv_hours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog();
                timePickerDialog.setTimePickerCallback(new TimePickerCallback() {
                    @Override
                    public void callBack() {
                        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
                        int hours = settings.getInt("Hours", 0);
                        int minutes = settings.getInt("Minutes", 0);
                        int seconds = settings.getInt("Seconds", 0);

                        Log.i("TIME: ", hours +":" +minutes +" : " +seconds);

                        tv_hours.setText("" + String.format("%02d", hours));
                        tv_minutes.setText("" + String.format("%02d", minutes));
                        tv_seconds.setText("" + String.format("%02d", seconds));
                    }
                });
                timePickerDialog.show(getFragmentManager(), "Time Picker");
            }
        });


        btn_start = (Button) view.findViewById(R.id.btn_timer_start);
        btn_reset = (Button) view.findViewById(R.id.btn_reset_timer);

        btn_reset.setVisibility(View.GONE);

        btn_start.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!running) {
                    hours = Integer.parseInt(tv_hours.getText().toString());
                    minutes = Integer.parseInt(tv_minutes.getText().toString());
                    seconds = Integer.parseInt(tv_seconds.getText().toString());

                /* Get the milliseconds from the initial time */
                    milliseconds = hours * 3600000 + minutes * 60000 + seconds * 1000;
                    Log.i("MILLISECONDS", milliseconds + "");
                    if (milliseconds == 0) {
                        return;
                    }
                /* Get the time of the start of the timer*/
                    start_time = SystemClock.uptimeMillis();

                    handler.post(updateTimer);
                    running = true;
                    btn_reset.setVisibility(View.VISIBLE);
                    btn_start.setText("Pause");
                } else {
                    handler.removeCallbacks(updateTimer);
                    running = false;
                    btn_start.setText("Resume");
                }
            }
        });

        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (running) {
                    Toast.makeText(context, "Pause before resetting", Toast.LENGTH_SHORT).show();

                } else {
                    handler.removeCallbacks(updateTimer);
                    tv_hours.setText("" + String.format("%02d", hours));
                    tv_minutes.setText("" + String.format("%02d", minutes));
                    tv_seconds.setText("" + String.format("%02d", seconds));
                    btn_start.setText("Start");
                    btn_reset.setVisibility(View.GONE);
                }

            }
        });

        return view;
    }


    public Runnable updateTimer = new Runnable() {
        public void run() {
            /* Get the amount of time that has passed in ms */
            timeInMilliseconds = SystemClock.uptimeMillis() - start_time;
            /* subtract the total time from the starting time */
            long updatedtime = milliseconds - timeInMilliseconds;//timeSwapBuff +

            /* if no time is remaining then end the task */
            if(updatedtime <= 0){
                handler.removeCallbacks(updateTimer);
                //Sound the alarm
            }else {

                int[] time = milliToTime.milliToTime(updatedtime);
            /* get the seconds out of the time */
                int hours = time[0];
                // Log.i("HOURS" , hours+"");
                int minutes = time[1];
                int seconds = time[2];
                int mili = time[3];

                tv_hours.setText("" + String.format("%02d", hours));
                tv_minutes.setText("" + String.format("%02d", minutes));
                tv_seconds.setText("" + String.format("%02d", seconds));

                handler.postDelayed(this, 100);
            }
        }};
}
