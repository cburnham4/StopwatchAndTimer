package letshangllc.timer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class Stopwatch extends Fragment {
    Button btnStart, btnReset;
    TextView time;
    long starttime = 0L;
    long lastLapTime = 0L;
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedtime = 0L;
    int t = 1;
    int secs = 0;
    int mins = 0;
    int milliseconds = 0;
    Handler handler = new Handler();
    int lapNum = 1;

    ArrayList<Lap> arrayOfLaps;
    boolean paused = true;
    AdsHelper adsHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stopwatch, container, false);

        adsHelper = new AdsHelper(view, getResources().getString(R.string.admob_timer_id),this.getActivity());
        adsHelper.runAds();

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
                if (t == 1) {
                    //timer will start
                    btnStart.setText("Pause");
                    btnReset.setText("LAP");
                    starttime = SystemClock.uptimeMillis();

                    /* Start the timer */
                    handler.postDelayed(updateTimer, 0);

                    btnReset.setVisibility(View.VISIBLE);
                    t = 0;
                    paused = false;
                } else {
                    //timer will pause
                    btnStart.setText("Start");
                    /* Since the timer is Paused, allow the user to reset time */
                    btnReset.setText("Reset");
                    timeSwapBuff += timeInMilliseconds;

                    /*Pause the timer*/
                    handler.removeCallbacks(updateTimer);
                    t = 1;
                    paused = true;
                }

            }
        });
        final Context app = this.getActivity();
        btnReset.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                /*If the timer is paused then allow the user to reset clock */
                if(paused){
                    /*Reset all the time values then stop timer*/
                    handler.removeCallbacks(updateTimer);
                    starttime = 0L;
                    timeInMilliseconds = 0L;
                    timeSwapBuff = 0L;
                    updatedtime = 0L;
                    t = 1;
                    secs = 0;
                    mins = 0;
                    milliseconds = 0;
                    lastLapTime = 0L;
                    btnStart.setText("Start");

                    time.setText("00:00:00");

                    btnReset.setVisibility(View.GONE);
                    lapNum = 1;
                    arrayOfLaps.clear();
                    adapter.clear();
                }
                /*If the time is running then Add lap when clicked*/
                else{

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

            }});
        return view;
    }

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

    @Override
    public void onPause() {
        adsHelper.onPause();
        super.onPause();
    }

    public void onResume(){
        adsHelper.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        adsHelper.onDestroy();
        super.onDestroy();
    }
}
