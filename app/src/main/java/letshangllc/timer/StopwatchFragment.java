package letshangllc.timer;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;


public class StopwatchFragment extends Fragment {
    Button btnStart, btnReset;
    TextView time;
    long starttime = 0L, lastLapTime = 0L;
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedtime = 0L;
    int secs = 0;
    int mins = 0;
    int milliseconds = 0;
    Handler handler = new Handler(Looper.getMainLooper());
    int lapNum = 1;

    ArrayList<Lap> arrayOfLaps;
    boolean paused = true;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stopwatch, container, false);

        btnStart = view.findViewById(R.id.start);
        btnReset = view.findViewById(R.id.reset);

        time = view.findViewById(R.id.timer);
        btnReset.setVisibility(View.GONE);

        arrayOfLaps = new ArrayList<>();
        final LapAdapter adapter = new LapAdapter(requireContext(), arrayOfLaps);
        final ListView listView = view.findViewById(R.id.lv_laps);
        listView.setAdapter(adapter);

        btnStart.setOnClickListener(v -> {
            if (paused) {
                btnStart.setText("Pause");
                btnReset.setText("LAP");
                starttime = SystemClock.uptimeMillis();
                handler.post(updateTimer);
                btnReset.setVisibility(View.VISIBLE);
                paused = false;
            } else {
                btnStart.setText("Start");
                btnReset.setText("Reset");
                timeSwapBuff += timeInMilliseconds;
                handler.removeCallbacks(updateTimer);
                paused = true;
            }
        });

        btnReset.setOnClickListener(v -> {
            if (paused) {
                handler.removeCallbacks(updateTimer);
                starttime = 0L;
                timeInMilliseconds = 0L;
                timeSwapBuff = 0L;
                updatedtime = 0L;
                secs = 0;
                mins = 0;
                milliseconds = 0;
                lastLapTime = 0L;
                btnStart.setText("Start");
                time.setText("00:00:00");
                btnReset.setVisibility(View.GONE);
                lapNum = 1;
                arrayOfLaps.clear();
                paused = true;
                adapter.clear();
            } else {
                MyTime overallTime = new MyTime(0, mins, secs, milliseconds);
                Long lapTime = updatedtime - lastLapTime;
                lastLapTime = updatedtime;

                secs = (int) (lapTime / 1000);
                mins = secs / 60;
                secs = secs % 60;
                milliseconds = (int) (lapTime % 1000);
                milliseconds /= 10;

                MyTime myLapTime = new MyTime(0, mins, secs, milliseconds);
                arrayOfLaps.add(new Lap(lapNum++, myLapTime, overallTime));
                adapter.notifyDataSetChanged();
            }
        });

        return view;
    }

    public final Runnable updateTimer = new Runnable() {
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - starttime;
            updatedtime = timeSwapBuff + timeInMilliseconds;
            secs = (int) (updatedtime / 1000);
            mins = secs / 60;
            secs = secs % 60;
            milliseconds = (int) (updatedtime % 1000);
            milliseconds /= 10;
            if (mins < 10) {
                time.setText("0" + mins + ":" + String.format("%02d", secs) + ":"
                        + String.format("%02d", milliseconds));
            } else {
                time.setText("" + mins + ":" + String.format("%02d", secs) + ":"
                        + String.format("%02d", milliseconds));
            }
            handler.postDelayed(this, 0);
        }
    };

    @Override
    public void onDestroyView() {
        handler.removeCallbacks(updateTimer);
        super.onDestroyView();
    }
}
