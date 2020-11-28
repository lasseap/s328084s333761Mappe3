package com.example.s328084s333761mappe3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;


public class LeggTilBygg extends AppCompatActivity {
    TextView koordinater;
    TextView adresse;
    EditText beskrivelse;
    EditText antEtasjer;
    String adresseStreng;
    String koordinaterStreng;

    //Funksjon som kalles når man trykker tilbake på mobilen
    @Override
    public void onBackPressed() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Lagring av bygg er avbrutt, fjerner derfor markøren som ble opprettet
        int antall = prefs.getInt(getString(R.string.antallMarkers), 0);
        antall = antall - 1;
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(getString(R.string.antallMarkers),antall);
        editor.apply();
        MapsActivity.deleteMarker();
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.legg_til_bygg);

        setTitle(R.string.leggTilBygg);

        //Henter ut infoen som ble sendt med intentet
        Intent i = this.getIntent();
        adresseStreng = i.getExtras().getString(getString(R.string.adresse));
        koordinaterStreng = i.getExtras().getString(getString(R.string.koordinater));
        koordinater = (TextView) findViewById(R.id.koordinaterInn);
        adresse = (TextView) findViewById(R.id.adresseInn);
        //Setter inn adresse og koordinater i boksene deres
        adresse.setText(adresseStreng);
        //Viser bare de to første desimalene i koordinatene
        String splittet[] = koordinaterStreng.split(",");
        String formatertKoordinater = splittet[0].substring(0,5) +"," + splittet[1].substring(0,5);
        koordinater.setText(formatertKoordinater);
        beskrivelse = (EditText) findViewById(R.id.beskrivelseInn);
        antEtasjer = (EditText) findViewById(R.id.antEtasjerInn);
    }

    //Legger til ikon for lagre i actionbaren
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.leggtil_meny, menu);
        //Gjør ikonet svart
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

    //Funksjon for å legge til et nytt bygg
    public void leggtil() {

        String antallEtasjer = antEtasjer.getText().toString();
        String beskrivelseTekst = beskrivelse.getText().toString();

        //Sjekker inputen fra bruker og lager feilmelding basert på hva som er feil
        String feilMelding = "";
        String feilMelding2 = "";
        Boolean utfylt = true;
        //Sjekker at beskrivelse er fylt inn
        if(beskrivelseTekst.equals("")) {
            feilMelding += getString(R.string.feilBeskrivelse);
            utfylt = false;
        }
        //Sjekker at antall etasjer er fylt inn
        if ((antallEtasjer.equals(""))) {
            if(!feilMelding.equals("")) {
                feilMelding += ", ";
            }
            feilMelding +=  getString(R.string.feilAntEtasjer);
            utfylt = false;
        }
        else {
            int antEtasjer;
            //Sjekker at antall etasjer er heltall mellom 1 og 20
            try {
                antEtasjer = Integer.parseInt(antallEtasjer);
                if( antEtasjer > 20 || antEtasjer < 1) {
                    if(!feilMelding.equals("")) {
                        feilMelding += ", ";
                    }
                    feilMelding +=  getString(R.string.feilAntEtasjer);
                    utfylt = false;
                    feilMelding2 += getString(R.string.antetasjer_feil);
                }
            }
            catch (Exception e) {
                if(!feilMelding.equals("")) {
                    feilMelding += ", ";
                }
                feilMelding +=  getString(R.string.feilAntEtasjer);
                utfylt = false;
                feilMelding2 += getString(R.string.antetasjer_feil);
            }

        }
        //Hvis det ikke er noen feil legges bygget inn på webserver
        if(utfylt){
            jsonLeggTil(beskrivelseTekst,adresseStreng,koordinaterStreng,antallEtasjer);
            finish();
        }
        //Hvis det er feil vises feilmelding til bruker
        else {
            feilMelding += " " + getString(R.string.ikkeFyltUtKorrekt) + ". ";
            Toast.makeText(getApplicationContext(),feilMelding + feilMelding2, Toast.LENGTH_SHORT).show();
        }
    }

    //Funksjon for å legge til et bygg i webserver
    public void jsonLeggTil(String beskrivelse, String adresse, String koordinater, String antEtasjer) {
        //Formaterer variabler med mellomrom fordi httpurlconnection ikke takler vanlige mellomrom i url-en
        String[] splittet = adresse.split("\\s+");
        String formatertAdresse = splittet[0];
        for (int i = 1; i < splittet.length; i++) {
            formatertAdresse += "%20" + splittet[i];
        }
        String[] splittetBeskrivelse = beskrivelse.split("\\s+");
        String formatertBeskrivelse = splittetBeskrivelse[0];
        for (int i = 1; i < splittetBeskrivelse.length; i++) {
            formatertBeskrivelse += "%20" + splittetBeskrivelse[i];
        }
        //Kjører sendJSON task for å legge til bygg på webserver
        String json = "http://student.cs.hioa.no/~s333761/jsoninBygg.php/?Beskrivelse=" + formatertBeskrivelse + "&Adresse=" + formatertAdresse + "&Koordinater="
                + koordinater + "&AntEtasjer=" + antEtasjer;
        SendJSON task = new SendJSON();
        task.execute((new String[]{json}));

    }
}
