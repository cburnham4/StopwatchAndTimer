package letshangllc.timer;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
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

import android.os.Handler;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.DonutProgress;

public class Timer extends Fragment{
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

    private DonutProgress donutProgress;
    Context context;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_timer, container, false);

        context = this.getContext();

        tv_hours = (TextView) view.findViewById(R.id.tv_hour);
        tv_minutes = (TextView) view.findViewById(R.id.tv_minute);
        tv_seconds = (TextView) view.findViewById(R.id.tv_second);

        donutProgress = (DonutProgress) view.findViewById(R.id.donut_progress);

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

                        setTextTime(hours, minutes, seconds);
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
                    btn_reset.setBackgroundColor(getResources().getColor(R.color.primaryLight));
                    btn_start.setText("Pause");
                } else {
                    handler.removeCallbacks(updateTimer);
                    running = false;
                    btn_reset.setBackgroundColor(getResources().getColor(R.color.primary));
                    btn_start.setText("Resume");
                }
            }
        });

        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (running) {
                    /* Make sure the user pauses the timer before resetting */
                    Toast.makeText(context, "Pause before resetting", Toast.LENGTH_SHORT).show();

                } else {
                    handler.removeCallbacks(updateTimer);
                    setTextTime(hours, minutes, seconds);
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
                /* Stop running if the time has hit 0 */
                setTextTime(0, 0, 0);
                donutProgress.setProgress(100);
                playAlarm();
                handler.removeCallbacks(updateTimer);

                //Sound the alarm
            }else {
                /* get the time out of the milliseconds */
                int[] time = milliToTime.milliToTime(updatedtime);

                int hours = time[0];
                int minutes = time[1];
                int seconds = time[2];
                //donutProgress.setProgress(59);
                double percentFinished = (0.0+timeInMilliseconds)/(0.0+milliseconds);
                double percent = percentFinished *100;
                Log.i("PERCENT" , percent+"");
                donutProgress.setProgress((int) percent);
                /* Update the time using two digits */
                setTextTime(hours, minutes, seconds);
                /*
                tv_hours.setText("" + String.format("%02d", hours));
                tv_minutes.setText("" + String.format("%02d", minutes));
                tv_seconds.setText("" + String.format("%02d", seconds));
                */
                /* Run updateTimte again in 100ms */
                handler.postDelayed(this, 100);
            }
        }};

    private void setTextTime(int hours, int minutes, int seconds){
        tv_hours.setText("" + String.format("%02d", hours));
        tv_minutes.setText("" + String.format("%02d", minutes));
        tv_seconds.setText("" + String.format("%02d", seconds));
    }

    private void playAlarm(){
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        if(alert == null){
            // alert is null, using backup
            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            // I can't see this ever being null (as always have a default notification)
            // but just incase
            if(alert == null) {
                // alert backup is null, using 2nd backup
                alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }

        try {
            Ringtone r = RingtoneManager.getRingtone(context, alert);
            r.play();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
