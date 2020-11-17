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
            finish();
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
            jsonLeggTil(beskrivelseTekst,adresseTekst,koordinaterTekst,antallEtasjer);
        }
        else {
            feilMelding += " " + getString(R.string.ikkeFyltUtKorrekt);
            Toast.makeText(getApplicationContext(),feilMelding, Toast.LENGTH_SHORT).show();
        }
    }

    public void jsonLeggTil(String beskrivelse, String adresse, String koordinater, String antEtasjer) {
        String json = "http://student.cs.hioa.no/~s333761//jsoninBygg.php/?Beskrivelse=" + beskrivelse + "&Adresse=" + adresse + "&Koordinater="
                + koordinater + "&AntEtasjer=" + antEtasjer;
        SendJSON task = new SendJSON();
        task.execute((new String[]{json}));

    }
}
