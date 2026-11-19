package letshangllc.timer;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class TimePickerDialog extends DialogFragment {
    NumberPicker numberPickerHour;
    NumberPicker numberPickerMinute;
    NumberPicker numberPickerSecond;

    public static final String PREFS_NAME = "TimePrefs";

    TimePickerCallback timePickerCallback;

    private Context context;

    public void setTimePickerCallback(TimePickerCallback timePickerCallback) {
        this.timePickerCallback = timePickerCallback;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_number_picker, null);

        context = requireContext();
        setUpNumberPickers(view);

        builder.setView(view)
                .setPositiveButton("Okay", (dialog, id) -> {
                    numberPickerHour.clearFocus();
                    numberPickerMinute.clearFocus();
                    numberPickerSecond.clearFocus();
                    int hours = numberPickerHour.getValue();
                    int minutes = numberPickerMinute.getValue();
                    int seconds = numberPickerSecond.getValue();

                    SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putInt("Hours", hours);
                    editor.putInt("Minutes", minutes);
                    editor.putInt("Seconds", seconds);
                    editor.apply();
                    if (timePickerCallback != null) {
                        timePickerCallback.callBack();
                    }
                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                    if (TimePickerDialog.this.getDialog() != null) {
                        TimePickerDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    private void setUpNumberPickers(View view) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        int hours = settings.getInt("Hours", 0);
        int minutes = settings.getInt("Minutes", 0);
        int seconds = settings.getInt("Seconds", 0);

        numberPickerHour = view.findViewById(R.id.nmp_hour);
        numberPickerHour.setMaxValue(24);
        numberPickerHour.setMinValue(0);
        numberPickerHour.setValue(hours);

        numberPickerMinute = view.findViewById(R.id.nmp_minute);
        numberPickerMinute.setMaxValue(60);
        numberPickerMinute.setMinValue(0);
        numberPickerMinute.setValue(minutes);

        numberPickerSecond = view.findViewById(R.id.nmp_second);
        numberPickerSecond.setMaxValue(60);
        numberPickerSecond.setMinValue(0);
        numberPickerSecond.setValue(seconds);
    }
}
