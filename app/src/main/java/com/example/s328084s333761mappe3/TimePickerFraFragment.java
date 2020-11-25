package com.example.s328084s333761mappe3;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class TimePickerFraFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Bruker det nåværende tidspunktet som default
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(), this, hour, minute, true);
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        SharedPreferences.Editor editor = prefs.edit();
        //Putter det valgte tidspunktet inn i SharedPreferences
        editor.putString(getString(R.string.tidspunktFra),hourOfDay + ":" + minute);
        editor.apply();
        mCallback.onDialogDismissListener();
    }

    public interface OnDialogDismissListener {
        void onDialogDismissListener();
    }

    TimePickerFraFragment.OnDialogDismissListener mCallback;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            mCallback = (TimePickerFraFragment.OnDialogDismissListener) getActivity();
        }
        catch (ClassCastException e) {
            throw new  ClassCastException("Kallende klasse må implementere interface");
        }
    }
}
