package com.example.s328084s333761mappe3;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class LeggTilBygg extends Activity {
    TextView koordinater;
    TextView adresse;
    EditText beskrivelse;
    EditText antEtasjer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.legg_til_bygg);

        koordinater = (TextView) findViewById(R.id.koordinaterInn);
        adresse = (TextView) findViewById(R.id.adresseInn);
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
        } else {
            return super.onOptionsItemSelected(item);
        }

        return true;
    }

    public void leggtil() {

        String antallEtasjer = antEtasjer.getText().toString();
        String beskrivelseTekst = beskrivelse.getText().toString();
        String koordinaterTekst = koordinater.getText().toString();
        String adresseTekst = adresse.getText().toString();
        if(!(beskrivelseTekst.equals("")&&(antallEtasjer.equals("")))){
            jsonLeggTil(beskrivelseTekst,adresseTekst,koordinaterTekst,antallEtasjer);
        }
        else {
            Toast.makeText(getApplicationContext(),R.string.ikkeFyltUtKorrekt, Toast.LENGTH_SHORT).show();
        }
    }

    public void jsonLeggTil(String beskrivelse, String adresse, String koordinater, String antEtasjer) {
        String json = "{Beskrivelse: " + beskrivelse + ", Adresse: " + adresse + ", Koordinater: "
                + koordinater + ", AntEtasjer: " + antEtasjer + "}";
        SendJSON task = new SendJSON(json);

    }
}
