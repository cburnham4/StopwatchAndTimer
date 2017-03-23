package letshangllc.timer;

import android.content.Context;
import android.os.Bundle;

import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.*;


public class StopwatchFragment extends Fragment {
    /* Static variables */
    private static String IS_PAUSED = "paused", START_TIME = "start_time", LAST_LAP_TIME = "last_lap_time",
    TIME_IN_MILLI = "time_in_milli", TIME_SWAP_BUFF = "time_swap_buff", UPDATED_TIME = "updated_time";

    ;

    Button btnStart, btnReset;
    TextView time;
    long starttime = 0L, lastLapTime = 0L;
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedtime = 0L;
    int state = 1; // 1 = Running, 0 = Stopped
    int secs = 0;
    int mins = 0;
    int milliseconds = 0;
    Handler handler = new Handler();
    int lapNum = 1;

    ArrayList<Lap> arrayOfLaps;
    boolean paused = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stopwatch, container, false);

        //updateValuesFromBundle(savedInstanceState);


        btnStart = (Button) view.findViewById(R.id.start);
        btnReset = (Button) view.findViewById(R.id.reset);

        time = (TextView) view.findViewById(R.id.timer);
        /*Start with the reset and lap buttons not there*/
        btnReset.setVisibility(View.GONE);

        // Construct the data source
        arrayOfLaps = new ArrayList<Lap>();
        // Create the adapter to convert the array to views
        final LapAdapter adapter = new LapAdapter(this.getContext(), arrayOfLaps);
        // Attach the adapter to a ListView
        final ListView listView = (ListView) view.findViewById(R.id.lv_laps);
        listView.setAdapter(adapter);

        btnStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (paused) {
                    //startTimer();
                    btnStart.setText("Pause");
                    btnReset.setText("LAP");
                    starttime = SystemClock.uptimeMillis();

        /* Start the timer */
                    handler.post(updateTimer);

                    btnReset.setVisibility(View.VISIBLE);
                    state = 0;
                    paused = false;
                } else {
                    //pauseTime();
                    //timer will pause
                    btnStart.setText("Start");
                    /* Since the timer is Paused, allow the user to reset time */
                    btnReset.setText("Reset");
                    timeSwapBuff += timeInMilliseconds;

        /*Pause the timer*/
                    handler.removeCallbacks(updateTimer);
                    state = 1;
                    paused = true;
                }

            }
        });
        final Context app = this.getActivity();
        btnReset.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                /*If the timer is paused then allow the user to reset clock */
                if (paused) {
                   // resetTimer(adapter);
                            /*Reset all the time values then stop timer*/
                    handler.removeCallbacks(updateTimer);
                    starttime = 0L;
                    timeInMilliseconds = 0L;
                    timeSwapBuff = 0L;
                    updatedtime = 0L;
                    state = 1;
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
                }
                /*If the time is running then Add lap when clicked*/
                else {

                    /*Since the timer is running and the variables instance variables then just use them */
                    MyTime overallTime = new MyTime(0, mins, secs, milliseconds);

                    /* Get the time of the current lap by subtracting the total time by the time of the last lap*/
                    Long lapTime = updatedtime - lastLapTime;
                    /*Set the current time as the lastLapTime to be subtracted next time*/
                    lastLapTime = updatedtime;

                    secs = (int) (lapTime / 1000);
                    mins = secs / 60;
                    secs = secs % 60;
                    milliseconds = (int) (lapTime % 1000);
                    milliseconds /= (int) 10;

                    MyTime myLapTime = new MyTime(0, mins, secs, milliseconds);
                    arrayOfLaps.add(new Lap(lapNum++, myLapTime, overallTime));
                    adapter.notifyDataSetChanged();
                }

            }
        });

        return  view;
    }

//    private void startTimer(){
//        //timer will start
//        btnStart.setText("Pause");
//        btnReset.setText("LAP");
//        starttime = SystemClock.uptimeMillis();
//
//        /* Start the timer */
//        handler.post(updateTimer);
//
//        btnReset.setVisibility(View.VISIBLE);
//        state = 0;
//        paused = false;
//    }
//
//    private void pauseTime(){
//        //timer will pause
//        btnStart.setText("Start");
//                    /* Since the timer is Paused, allow the user to reset time */
//        btnReset.setText("Reset");
//        timeSwapBuff += timeInMilliseconds;
//
//        /*Pause the timer*/
//        handler.removeCallbacks(updateTimer);
//        state = 1;
//        paused = true;
//    }
//
//    private void resetTimer(LapAdapter adapter){
//        /*Reset all the time values then stop timer*/
//        handler.removeCallbacks(updateTimer);
//        starttime = 0L;
//        timeInMilliseconds = 0L;
//        timeSwapBuff = 0L;
//        updatedtime = 0L;
//        state = 1;
//        secs = 0;
//        mins = 0;
//        milliseconds = 0;
//        lastLapTime = 0L;
//        btnStart.setText("Start");
//
//        time.setText("00:00:00");
//
//        btnReset.setVisibility(View.GONE);
//        lapNum = 1;
//        arrayOfLaps.clear();
//        paused = true;
//        adapter.clear();
//    }

//    public void onSaveInstanceState(Bundle savedInstanceState) {
//        savedInstanceState.putBoolean(IS_PAUSED, paused);
//        savedInstanceState.putLong(START_TIME, starttime);
////        savedInstanceState.putParcelable(LOCATION_KEY, mCurrentLocation);
////        savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, mLastUpdateTime);
//        super.onSaveInstanceState(savedInstanceState);
//    }
//
//    private void updateValuesFromBundle(Bundle savedInstanceState) {
//        if (savedInstanceState != null) {
//            // Update the value of mRequestingLocationUpdates from the Bundle, and
//            // make sure that the Start Updates and Stop Updates buttons are
//            // correctly enabled or disabled.
//            if (savedInstanceState.keySet().contains(IS_PAUSED)) {
//                paused = savedInstanceState.getBoolean(
//                        IS_PAUSED);
//            }
//
//        }
//    }

    public Runnable updateTimer = new Runnable() {
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - starttime;
            updatedtime = timeSwapBuff + timeInMilliseconds;
            secs = (int) (updatedtime / 1000);
            mins = secs / 60;
            secs = secs % 60;
            milliseconds = (int) (updatedtime % 1000);
            milliseconds /= (int) 10;
            if(mins<10){
                time.setText("0" + mins + ":" + String.format("%02d", secs) + ":"
                        + String.format("%02d", milliseconds));
            }else{
                time.setText("" + mins + ":" + String.format("%02d", secs) + ":"
                        + String.format("%02d", milliseconds));
            }
            handler.postDelayed(this, 0);
        }};

}
