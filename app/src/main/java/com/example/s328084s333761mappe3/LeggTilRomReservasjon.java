package com.example.s328084s333761mappe3;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

public class LeggTilRomReservasjon extends AppCompatActivity implements DatePickerFragment.OnDialogDismissListener, TimePickerFragment.OnDialogDismissListener {

    TextView datoBoks;
    TextView fraTidBoks;
    TextView tilTidBoks;

    // DBHandler db; Webdatabase
    String dato;
    String tid;
    SharedPreferences prefs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.legg_til_romreservasjon);

        setTitle(R.string.leggTilRomReservasjon);

        //Finner Viewene vi trenger i layouten
        datoBoks = findViewById(R.id.datoBoks);
        fraTidBoks = findViewById(R.id.fraTidBoks);
        tilTidBoks = findViewById(R.id.tilTidBoks);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
    }

    //Åpner TimePicker-fragmentet
    public void visTidspunkt(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    //Åpner DatePicker-fragmentet
    public void visDato(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onDialogDismissListener() {
        //Henter inn tid og dato fra SharedPreferences
        String tidspunkt = prefs.getString(getString(R.string.velgTidspunkt),"");
        String datoboks = prefs.getString(getString(R.string.velgDato),"");
        if(!tidspunkt.equals("")) {
            //Formaterer tid på formen mm:tt
            String[] splittetTid = tidspunkt.split(":");
            if (Integer.parseInt(splittetTid[0]) < 10 && Integer.parseInt(splittetTid[1]) < 10) {
                tidspunkt = "0" + splittetTid[0] + ":0" + splittetTid[1];
            } else if (Integer.parseInt(splittetTid[0]) < 10) {
                tidspunkt = "0" + splittetTid[0] + ":" + splittetTid[1];
            } else if (Integer.parseInt(splittetTid[1]) < 10) {
                tidspunkt = splittetTid[0] + ":0" + splittetTid[1];
            }
        }
        if(!datoboks.equals("")) {
            //Formaterer dato på formen dd.mm.yyyy
            String[] splittetDato = datoboks.split("\\.");
            if (Integer.parseInt(splittetDato[0]) < 10 && Integer.parseInt(splittetDato[1]) < 10) {
                datoboks = "0" + splittetDato[0] + ".0" + splittetDato[1] + "." + splittetDato[2];
            } else if (Integer.parseInt(splittetDato[0]) < 10) {
                datoboks = "0" + splittetDato[0] + "." + splittetDato[1] + "." + splittetDato[2];
            } else if (Integer.parseInt(splittetDato[1]) < 10) {
                datoboks = splittetDato[0] + ".0" + splittetDato[1] + "." + splittetDato[2];
            }
        }
        //Legger inn valgt tid og dato i TextViews
        fraTidBoks.setText(tidspunkt);
        tilTidBoks.setText(tidspunkt);
        datoBoks.setText(datoboks);
    }

    //Legger til ikon for lagre i actionbaren
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.leggtil_meny, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*
        if (item.getItemId() == R.id.leggTilAction) {
            //Når brukeren trykker på lagre-ikonet kjøres leggtil-funksjonen
            leggtil();
        } else {
            return super.onOptionsItemSelected(item);
        }
        */
        return true;
    }

}


