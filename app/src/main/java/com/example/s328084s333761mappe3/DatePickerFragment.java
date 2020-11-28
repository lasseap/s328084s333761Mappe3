package com.example.s328084s333761mappe3;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.DatePicker;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Bruker dagens dato som default dato i velgeren
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker = new DatePickerDialog(getActivity(), this, year, month, day);
        //Setter minimumsdatoen i DatePickeren til dagens dato, slik at bruker ikke kan velge en dato som allerede har vært
        datePicker.getDatePicker().setMinDate(c.getTime().getTime());
        return datePicker;
    }

    //Metode for å lagre datoen når en bruker velger en dato
    public void onDateSet(DatePicker view, int year, int month, int day) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        SharedPreferences.Editor editor = prefs.edit();
        month += 1; //Virker som Calendar-objektet bruker måneder fra 0 til 11, må derfor plusse på 1
        editor.putString(getString(R.string.velgDato),day + "." + month + "." + year);
        //Putter datoen inn i SharedPreferences, slik at vi har tilgang til den overalt
        editor.apply();
        mCallback.onDialogDismissListener();
    }

    public interface OnDialogDismissListener {
        void onDialogDismissListener();
    }

    OnDialogDismissListener mCallback;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            mCallback = (OnDialogDismissListener) getActivity();
        }
        catch (ClassCastException e) {
            throw new  ClassCastException("kallende klasse må implementere interface");
        }
    }

}
