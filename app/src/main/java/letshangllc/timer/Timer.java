package letshangllc.timer;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.lzyzsd.circleprogress.DonutProgress;

public class Timer extends Fragment {
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

    long totalTimePaused = 0L;
    long pausedTime;

    int originalHour;
    int originalMinute;
    int originalSecond;
    long originalMilliSeconds;

    int progress;

    Ringtone r;

    MilliToTime milliToTime = new MilliToTime();
    Handler handler = new Handler(Looper.getMainLooper());

    private DonutProgress donutProgress;
    Context context;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timer, container, false);

        context = requireContext();
        findViews(view);

        View.OnClickListener tvTimeOnClickListener = v -> {
            if (!running && !paused) {
                TimePickerDialog timePickerDialog = new TimePickerDialog();
                timePickerDialog.setTimePickerCallback(() -> {
                    SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
                    int hoursPref = settings.getInt("Hours", 0);
                    int minutesPref = settings.getInt("Minutes", 0);
                    int secondsPref = settings.getInt("Seconds", 0);
                    setTextTime(hoursPref, minutesPref, secondsPref);
                });
                timePickerDialog.show(getParentFragmentManager(), "Time Picker");
            } else {
                Toast.makeText(context, "Reset before setting time", Toast.LENGTH_SHORT).show();
            }
        };
        tv_hours.setOnClickListener(tvTimeOnClickListener);
        tv_minutes.setOnClickListener(tvTimeOnClickListener);
        tv_seconds.setOnClickListener(tvTimeOnClickListener);

        btn_reset.setVisibility(View.GONE);

        btn_start.setOnClickListener(v -> {
            if (alarmPlaying) {
                if (r != null && r.isPlaying()) {
                    r.stop();
                }

                alarmPlaying = false;
                btn_start.setText("Start");
                donutProgress.setProgress(0);
                paused = false;
                handler.removeCallbacks(updateTimer);
                setTextTime(originalHour, originalMinute, originalSecond);
                btn_start.setText("Start");
            } else if (!running) {
                hours = Integer.parseInt(tv_hours.getText().toString());
                minutes = Integer.parseInt(tv_minutes.getText().toString());
                seconds = Integer.parseInt(tv_seconds.getText().toString());

                milliseconds = hours * 3600000L + minutes * 60000L + seconds * 1000L;

                if (!paused) {
                    originalHour = hours;
                    originalMinute = minutes;
                    originalSecond = seconds;
                    originalMilliSeconds = milliseconds;
                }
                if (milliseconds == 0) {
                    return;
                }
                start_time = SystemClock.uptimeMillis();
                handler.post(updateTimer);
                running = true;

                btn_reset.setVisibility(View.VISIBLE);
                btn_reset.setBackgroundColor(ContextCompat.getColor(context, R.color.primaryLight));
                btn_start.setText("Pause");
            } else {
                handler.removeCallbacks(updateTimer);
                pausedTime = SystemClock.uptimeMillis();
                running = false;
                paused = true;
                btn_reset.setBackgroundColor(ContextCompat.getColor(context, R.color.primary));
                btn_start.setText("Resume");
            }
        });

        btn_reset.setOnClickListener(v -> {
            if (running) {
                Toast.makeText(context, "Pause before resetting", Toast.LENGTH_SHORT).show();
            } else {
                donutProgress.setProgress(0);
                paused = false;
                handler.removeCallbacks(updateTimer);
                setTextTime(originalHour, originalMinute, originalSecond);
                btn_start.setText("Start");
                btn_reset.setVisibility(View.GONE);
                totalTimePaused = 0;
            }
        });

        return view;
    }

    public final Runnable updateTimer = new Runnable() {
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - start_time + totalTimePaused;
            long updatedtime = milliseconds - timeInMilliseconds;

            if (updatedtime <= 0) {
                setTextTime(0, 0, 0);
                donutProgress.setProgress(100);
                totalTimePaused = 0;
                playAlarm();
                alarmPlaying = true;
                running = false;
                btn_start.setText("Stop");
                btn_reset.setVisibility(View.GONE);
                handler.removeCallbacks(updateTimer);
            } else {
                int[] time = milliToTime.milliToTime(updatedtime);

                int hoursLeft = time[0];
                int minutesLeft = time[1];
                int secondsLeft = time[2];

                long timePassed = originalMilliSeconds - updatedtime;
                double fractionFinished = (0.0 + timePassed) / (0.0 + originalMilliSeconds);
                double percent = fractionFinished * 100;
                progress = (int) percent;
                donutProgress.setProgress(progress);

                setTextTime(hoursLeft, minutesLeft, secondsLeft);
                handler.postDelayed(this, 100);
            }
        }
    };

    private void findViews(View view) {
        btn_start = view.findViewById(R.id.btn_timer_start);
        btn_reset = view.findViewById(R.id.btn_reset_timer);
        tv_hours = view.findViewById(R.id.tv_hour);
        tv_minutes = view.findViewById(R.id.tv_minute);
        tv_seconds = view.findViewById(R.id.tv_second);
        donutProgress = view.findViewById(R.id.donut_progress);
    }

    private void setTextTime(int hours, int minutes, int seconds) {
        tv_hours.setText(String.format("%02d", hours));
        tv_minutes.setText(String.format("%02d", minutes));
        tv_seconds.setText(String.format("%02d", seconds));
    }

    private void playAlarm() {
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        if (alert == null) {
            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if (alert == null) {
                alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }

        try {
            r = RingtoneManager.getRingtone(context, alert);
            if (r != null) {
                r.setAudioAttributes(new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build());
                r.play();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        handler.removeCallbacks(updateTimer);
        if (r != null && r.isPlaying()) {
            r.stop();
        }
        super.onDestroyView();
    }
}
