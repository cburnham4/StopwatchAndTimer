package letshangllc.timer;



import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

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
    boolean paused = false;
    boolean alarmPlaying = false;

    long timeResumed;
    long totalTimePaused = 0L;
    long pausedTime;

    int originalHour;
    int originalMinute;
    int originalSecond;
    long originalMilliSeconds;

    int progress;

    Ringtone r;

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

        AdsHelper adsHelper = new AdsHelper(view, getResources().getString(R.string.admob_timer_id),this.getActivity());
        adsHelper.runAds();
        this.findViews(view);

        View.OnClickListener tvTimeOnClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(!running && !paused){
                    TimePickerDialog timePickerDialog = new TimePickerDialog();
                /*
                    Use a callback to get the selected values to make sure the numbers have been inserted
                    into shared preferences.
                 */
                    timePickerDialog.setTimePickerCallback(new TimePickerCallback() {
                        @Override
                        public void callBack() {
                            SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
                            int hours = settings.getInt("Hours", 0);
                            int minutes = settings.getInt("Minutes", 0);
                            int seconds = settings.getInt("Seconds", 0);

                        /* set the time based on the preferences*/
                            setTextTime(hours, minutes, seconds);
                        }
                    });
                /* show the dialog */
                    timePickerDialog.show(getFragmentManager(), "Time Picker");
                }else{
                    Toast.makeText(context, "Reset before setting time", Toast.LENGTH_SHORT).show();
                }

            }
        };
        tv_hours.setOnClickListener(tvTimeOnClickListener);
        tv_minutes.setOnClickListener(tvTimeOnClickListener);
        tv_seconds.setOnClickListener(tvTimeOnClickListener);

        btn_reset.setVisibility(View.GONE);

        btn_start.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(alarmPlaying) {
                    if (r.isPlaying()) {
                        r.stop();
                    }

                    alarmPlaying = false;
                    btn_start.setText("Start");
                    donutProgress.setProgress(0);
                    paused = false;
                    handler.removeCallbacks(updateTimer);
                    setTextTime(originalHour, originalMinute, originalSecond);
                    btn_start.setText("Start");
                }else if (!running) {
                    hours = Integer.parseInt(tv_hours.getText().toString());
                    minutes = Integer.parseInt(tv_minutes.getText().toString());
                    seconds = Integer.parseInt(tv_seconds.getText().toString());

                    /* Get the milliseconds from the initial time */
                    milliseconds = hours * 3600000 + minutes * 60000 + seconds * 1000;

                    if(!paused){
                        originalHour = hours;
                        originalMinute = minutes;
                        originalSecond = seconds;
                        originalMilliSeconds = milliseconds;
                    }
                    if (milliseconds == 0) {
                        return;
                    }
                    /* Get the time of the start of the timer*/
                    start_time = SystemClock.uptimeMillis();

                    /* start the timer */
                    handler.post(updateTimer);
                    running = true;

                    btn_reset.setVisibility(View.VISIBLE);
                    btn_reset.setBackgroundColor(getResources().getColor(R.color.primaryLight));
                    btn_start.setText("Pause");
                } else {
                    /* pause the timer */
                    handler.removeCallbacks(updateTimer);
                    pausedTime = SystemClock.uptimeMillis();
                    running = false;
                    paused = true;
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
                    /* Stop the timer and reset the timer and buttons */
                    donutProgress.setProgress(0);
                    paused = false;
                    handler.removeCallbacks(updateTimer);
                    setTextTime(originalHour, originalMinute, originalSecond);
                    btn_start.setText("Start");
                    btn_reset.setVisibility(View.GONE);
                    totalTimePaused = 0;
                }
            }
        });

        return view;
    }


    public Runnable updateTimer = new Runnable() {
        public void run() {

            /* Get the amount of time that has passed in ms */
            timeInMilliseconds = SystemClock.uptimeMillis() - start_time +totalTimePaused;

            /* subtract the total time from the starting time */
            long updatedtime = milliseconds - timeInMilliseconds;

            /* if no time is remaining then end the task */
            if(updatedtime <= 0){
                /* Stop running if the time has hit 0 */
                setTextTime(0, 0, 0);
                donutProgress.setProgress(100);
                totalTimePaused = 0;
                //Sound the alarm
                playAlarm();
                alarmPlaying = true;
                running = false;

                /* change start button to stop alarm */
                btn_start.setText("Stop");
                btn_reset.setVisibility(View.GONE);
                handler.removeCallbacks(updateTimer);


            }else {
                /* get the time out of the milliseconds */
                int[] time = milliToTime.milliToTime(updatedtime);

                int hours = time[0];
                int minutes = time[1];
                int seconds = time[2];

                /* Apply the percent finished to the Donut View */
                long timePassed = originalMilliSeconds - updatedtime;
               // double fractionFinished = (0.0+timeInMilliseconds)/(0.0+milliseconds);
                double fractionFinished = (0.0+timePassed)/(0.0+originalMilliSeconds);
                double percent = fractionFinished *100;
                progress = (int) percent;
                donutProgress.setProgress(progress);

                /* Update the timer*/
                setTextTime(hours, minutes, seconds);

                /* Run updateTimte again in 100ms */
                handler.postDelayed(this, 100);
            }
        }};

    private void findViews(View view){
        btn_start = (Button) view.findViewById(R.id.btn_timer_start);
        btn_reset = (Button) view.findViewById(R.id.btn_reset_timer);
        tv_hours = (TextView) view.findViewById(R.id.tv_hour);
        tv_minutes = (TextView) view.findViewById(R.id.tv_minute);
        tv_seconds = (TextView) view.findViewById(R.id.tv_second);
        donutProgress = (DonutProgress) view.findViewById(R.id.donut_progress);
    }

    private void setTextTime(int hours, int minutes, int seconds){
        /* Update the time using two digits */
        tv_hours.setText("" + String.format("%02d", hours));
        tv_minutes.setText("" + String.format("%02d", minutes));
        tv_seconds.setText("" + String.format("%02d", seconds));
    }

    private void playAlarm(){
        /* Get the alarm tone */
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        if(alert == null){
            // alert is null, using backup
            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            // If notication is null the use ringtone
            if(alert == null) {
                // alert backup is null, using 2nd backup
                alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }

        try {
            /* Play the alarm */
            r = RingtoneManager.getRingtone(context, alert);
            r.setStreamType(AudioManager.STREAM_ALARM);
            r.play();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }



}
