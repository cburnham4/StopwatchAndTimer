package letshangllc.timer;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

/**
 * Created by cvburnha on 10/20/2015.
 */
public class TimePickerDialog extends DialogFragment {
    NumberPicker numberPickerHour;
    NumberPicker numberPickerMinute;
    NumberPicker numberPickerSecond;

    public static final String PREFS_NAME = "TimePrefs";

    TimePickerCallback timePickerCallback;

    private Context context;


    public void setTimePickerCallback(TimePickerCallback timePickerCallback){
        this.timePickerCallback = timePickerCallback;
    }
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.dialog_number_picker, null);

        context = this.getContext();

        /* call the private method to set up the number pickers */
        this.setUpNumberPickers(view);
        builder.setView(view)
                // Add action buttons
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                        int hours = numberPickerHour.getValue();
                        int minutes = numberPickerMinute.getValue();
                        int seconds = numberPickerSecond.getValue();

                        /* Store the values in the editor to send values to Timer Fragment */
                        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putInt("Hours" , hours);
                        editor.putInt("Minutes" , minutes);
                        editor.putInt("Seconds" , seconds);
                        editor.commit();
                        /* Call the call back */
                        timePickerCallback.callBack();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        TimePickerDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    private void setUpNumberPickers(View view){

        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        int hours = settings.getInt("Hours", 0);
        int minutes = settings.getInt("Minutes", 0);
        int seconds = settings.getInt("Seconds", 0);

        numberPickerHour = (NumberPicker) view.findViewById(R.id.nmp_hour);
        numberPickerHour.setMaxValue(24);
        numberPickerHour.setMinValue(0);
        numberPickerHour.setValue(hours);

        numberPickerMinute = (NumberPicker) view.findViewById(R.id.nmp_minute);
        numberPickerMinute.setMaxValue(60);
        numberPickerMinute.setMinValue(0);
        numberPickerMinute.setValue(minutes);

        numberPickerSecond = (NumberPicker) view.findViewById(R.id.nmp_second);
        numberPickerSecond.setMaxValue(60);
        numberPickerSecond.setMinValue(0);
        numberPickerSecond.setValue(seconds);
    }
}
