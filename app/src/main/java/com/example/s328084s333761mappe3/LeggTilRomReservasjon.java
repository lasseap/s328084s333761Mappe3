package com.example.s328084s333761mappe3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;

public class LeggTilRomReservasjon extends AppCompatActivity implements DatePickerFragment.OnDialogDismissListener, TimePickerFragment.OnDialogDismissListener, TimePickerFraFragment.OnDialogDismissListener {

    TextView datoBoks;
    TextView fraTidBoks;
    TextView tilTidBoks;
    int rom_id;
    String romNr;
    TextView romNrBox;

    SharedPreferences prefs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.legg_til_romreservasjon);

        Intent i = this.getIntent();
        rom_id = i.getExtras().getInt(getString(R.string.romUt));
        romNr = i.getExtras().getString(getString(R.string.romNr));

        setTitle(R.string.leggTilRomReservasjon);

        //Finner Viewene vi trenger i layouten
        datoBoks = findViewById(R.id.datoBoks);
        fraTidBoks = findViewById(R.id.fraTidBoks);
        tilTidBoks = findViewById(R.id.tilTidBoks);
        romNrBox = findViewById(R.id.romNrBoks);
        romNrBox.setText(romNr);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
    }

    //Åpner TimePicker-fragmentet
    public void visTidspunkt(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    //Åpner TimePicker-fragmentet
    public void visTidspunktFra(View v) {
        DialogFragment newFragment = new TimePickerFraFragment();
        newFragment.show(getSupportFragmentManager(), "timeFraPicker");
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
        String tidspunktFra = prefs.getString(getString(R.string.tidspunktFra),"");
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
        if(!tidspunktFra.equals("")) {
            //Formaterer tid på formen mm:tt
            String[] splittetTid = tidspunktFra.split(":");
            if (Integer.parseInt(splittetTid[0]) < 10 && Integer.parseInt(splittetTid[1]) < 10) {
                tidspunktFra = "0" + splittetTid[0] + ":0" + splittetTid[1];
            } else if (Integer.parseInt(splittetTid[0]) < 10) {
                tidspunktFra = "0" + splittetTid[0] + ":" + splittetTid[1];
            } else if (Integer.parseInt(splittetTid[1]) < 10) {
                tidspunktFra = splittetTid[0] + ":0" + splittetTid[1];
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
        fraTidBoks.setText(tidspunktFra);
        tilTidBoks.setText(tidspunkt);
        datoBoks.setText(datoboks);
    }

    //Legger til ikon for lagre i actionbaren
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.leggtil_meny, menu);
        MenuItem lagreItem = menu.findItem(R.id.lagreAction);
        Drawable lagreIcon = DrawableCompat.wrap(lagreItem.getIcon());
        ColorStateList colorSelector = ResourcesCompat.getColorStateList(getResources(), R.color.black, getTheme());
        DrawableCompat.setTintList(lagreIcon, colorSelector);
        lagreItem.setIcon(lagreIcon);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.lagreAction) {
            //Når brukeren trykker på lagre-ikonet kjøres leggtil-funksjonen
            leggtil();

        } else {
            return super.onOptionsItemSelected(item);
        }

        return true;
    }

    public void leggtil() {

        String dato = datoBoks.getText().toString();
        String tilTid = tilTidBoks.getText().toString();
        String fraTid = fraTidBoks.getText().toString();
        String feilMelding = "";
        Boolean utfylt = true;
        //sjekker at alle felter er fylt ut korrekt
        if (dato.equals("")) {
            feilMelding += getString(R.string.feilDato);
            utfylt = false;
        }
        String[] splittetFra = fraTid.split(":");
        String[] splittetTil = tilTid.split(":");
        try {
            int fraTime = Integer.parseInt(splittetFra[0]);
            int fraMin = Integer.parseInt(splittetFra[1]);
            int tilTime = Integer.parseInt(splittetTil[0]);
            int tilMin = Integer.parseInt(splittetTil[1]);
            if(fraTime > tilTime) {
                if(!feilMelding.equals("")) {
                    feilMelding += ", ";
                }
                feilMelding += getString(R.string.fraTil);
                utfylt = false;
            }
            if(fraTime == tilTime) {
                if(fraMin >= tilMin) {
                    if(!feilMelding.equals("")) {
                        feilMelding += ", ";
                    }
                    feilMelding += getString(R.string.fraTil);
                    utfylt = false;
                }
            }
        } catch (Exception e) {
            if(!feilMelding.equals("")) {
                feilMelding += ", ";
            }
            feilMelding += getString(R.string.feilTid);
            utfylt = false;
        }


        if(!utfylt) {
            feilMelding += " " + getString(R.string.ikkeFyltUtKorrekt);
            Toast.makeText(getApplicationContext(),feilMelding, Toast.LENGTH_SHORT).show();
        }
        else {
            Boolean opptatt = false;
            ArrayList<RomReservasjon> liste = RomReservasjonListe.reservasjonliste();
            if(!(liste == null)) {
                for(RomReservasjon reservasjon : liste) {
                    if(opptattSjekk(reservasjon,dato,fraTid,tilTid)) {
                        opptatt =true;
                    }

                }
            }
            if (opptatt) {
                Toast.makeText(getApplicationContext(),getString(R.string.opptatt), Toast.LENGTH_SHORT).show();
            }
            else {
                jsonLeggTil(dato, fraTid, tilTid);
                finish();
            }
        }


    }

    public void jsonLeggTil(String dato, String fra, String til) {
        String json = "http://student.cs.hioa.no/~s333761//jsoninRomReservasjon.php/?Rom_id="+ rom_id + "&Dato=" + dato + "&TidFra="
                + fra + "&TidTil=" + til;
        Log.d("TAG", json);
        SendJSON task = new SendJSON();
        task.execute((new String[]{json}));

    }

    public boolean opptattSjekk(RomReservasjon reservasjon, String dato, String fraTid, String tilTid) {
        if(reservasjon.dato.equals(dato)) {
            String[] splittetFra = fraTid.split(":");
            String[] splittetTil = tilTid.split(":");
            String[] splittetFraSjekk = reservasjon.fra.split(":");
            String[] splittetTilSjekk = reservasjon.til.split(":");
            try {
                int fraTime = Integer.parseInt(splittetFra[0]);
                int fraMin = Integer.parseInt(splittetFra[1]);
                int tilTime = Integer.parseInt(splittetTil[0]);
                int tilMin = Integer.parseInt(splittetTil[1]);
                int fraTimeSjekk = Integer.parseInt(splittetFraSjekk[0]);
                int fraMinSjekk = Integer.parseInt(splittetFraSjekk[1]);
                int tilTimeSjekk = Integer.parseInt(splittetTilSjekk[0]);
                int tilMinSjekk = Integer.parseInt(splittetTilSjekk[1]);
                if(fraTime > fraTimeSjekk) {
                    if(tilTimeSjekk > fraTime) {
                        return true;
                    }
                    if(tilTimeSjekk == fraTime) {
                        if(tilMinSjekk > fraMin) {
                            return true;
                        }
                    }
                }
                else if(fraTime == fraTimeSjekk) {
                    if(fraMin > fraMinSjekk) {
                        if(tilTimeSjekk > fraTime) {
                            return true;
                        }
                        if(tilTimeSjekk == fraTime) {
                            if(tilMinSjekk > fraMin) {
                                return true;
                            }
                        }
                    }
                    else {
                        if(tilTime == fraTimeSjekk){
                            if(tilMin > fraMinSjekk){
                                return true;
                            }
                        }
                        else {
                            return true;
                        }

                    }
                }
                else {
                    if(tilTime > fraTimeSjekk) {
                        return true;
                    }
                    else {
                        if(tilTime == fraTimeSjekk) {
                            if(tilMin > fraMinSjekk) {
                                return  true;
                            }
                        }
                    }
                }
            } catch (Exception e) {

            }

        }
        return false;
    }

    @Override
    protected void onDestroy() {
        //Når brukeren går ut av endresiden skal velgDato- og velgTidspunkt-variablene nullstilles
        //slik at disse ikke blir satt når en bruker skal endre et annet møte
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(getString(R.string.velgDato),"");
        editor.putString(getString(R.string.velgTidspunkt),"");
        editor.putString(getString(R.string.tidspunktFra),"");
        editor.apply();
        super.onDestroy();
    }

}


