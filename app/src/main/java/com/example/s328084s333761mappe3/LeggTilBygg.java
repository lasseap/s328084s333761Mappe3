package com.example.s328084s333761mappe3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class LeggTilBygg extends AppCompatActivity {
    TextView koordinater;
    TextView adresse;
    EditText beskrivelse;
    EditText antEtasjer;
    String adresseStreng;
    String koordinaterStreng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.legg_til_bygg);

        setTitle("Nytt bygg");

        Intent i = this.getIntent();
        adresseStreng = i.getExtras().getString(getString(R.string.adresse));
        koordinaterStreng = i.getExtras().getString(getString(R.string.koordinater));
        koordinater = (TextView) findViewById(R.id.koordinaterInn);
        adresse = (TextView) findViewById(R.id.adresseInn);
        adresse.setText(adresseStreng);
        koordinater.setText(koordinaterStreng);
        beskrivelse = (EditText) findViewById(R.id.beskrivelseInn);
        antEtasjer = (EditText) findViewById(R.id.antEtasjerInn);
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
        if (item.getItemId() == R.id.lagreAction) {
            //Når brukeren trykker på lagre-ikonet kjøres leggtil-funksjonen
            leggtil();
            finish();
        } else {
            return super.onOptionsItemSelected(item);
        }

        return true;
    }

    public void leggtil() {

        String antallEtasjer = antEtasjer.getText().toString();
        String beskrivelseTekst = beskrivelse.getText().toString();
        String feilMelding = "";
        Boolean utfylt = true;
        if(beskrivelseTekst.equals("")) {
            feilMelding += getString(R.string.feilBeskrivelse);
            utfylt = false;
        }
        if ((antallEtasjer.equals(""))) {
            if(!feilMelding.equals("")) {
                feilMelding += ", ";
            }
            feilMelding +=  getString(R.string.feilAntEtasjer);
            utfylt = false;
        }
        else {
            int antEtasjer;
            try {
                antEtasjer = Integer.parseInt(antallEtasjer);
                if( antEtasjer< 21 && antEtasjer < 0) {
                    if(!feilMelding.equals("")) {
                        feilMelding += ", ";
                    }
                    feilMelding +=  getString(R.string.feilAntEtasjer);
                    utfylt = false;
                }
            }
            catch (Exception e) {
                if(!feilMelding.equals("")) {
                    feilMelding += ", ";
                }
                feilMelding +=  getString(R.string.feilAntEtasjer);
                utfylt = false;
            }

        }
        if(utfylt){
            jsonLeggTil(beskrivelseTekst,adresseStreng,koordinaterStreng,antallEtasjer);
        }
        else {
            feilMelding += " " + getString(R.string.ikkeFyltUtKorrekt);
            Toast.makeText(getApplicationContext(),feilMelding, Toast.LENGTH_SHORT).show();
        }
    }

    public void jsonLeggTil(String beskrivelse, String adresse, String koordinater, String antEtasjer) {
        String[] splittet = adresse.split("\\s+");
        String formatertAdresse = splittet[0];
        for (int i = 1; i < splittet.length; i++) {
            formatertAdresse += "%20" + splittet[i];
        }
        String json = "http://student.cs.hioa.no/~s333761/jsoninBygg.php/?Beskrivelse=" + beskrivelse + "&Adresse=" + formatertAdresse + "&Koordinater="
                + koordinater + "&AntEtasjer=" + antEtasjer;
        Log.d("TAG", json);
        SendJSON task = new SendJSON();
        task.execute((new String[]{json}));

    }
}
