package letshangllc.timer;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
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

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.dialog_number_picker, null);
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
        numberPickerHour = (NumberPicker) view.findViewById(R.id.nmp_hour);
        numberPickerHour.setMaxValue(24);
        numberPickerHour.setMinValue(0);

        numberPickerMinute = (NumberPicker) view.findViewById(R.id.nmp_minute);
        numberPickerMinute.setMaxValue(60);
        numberPickerMinute.setMinValue(0);

        numberPickerSecond = (NumberPicker) view.findViewById(R.id.nmp_second);
        numberPickerSecond.setMaxValue(60);
        numberPickerSecond.setMinValue(0);
    }
}
