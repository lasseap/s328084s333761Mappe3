package com.example.s328084s333761mappe3;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LeggTilRom extends AppCompatActivity {

    EditText romNr;
    EditText etasjeNr;
    EditText kapasitet;
    EditText beskrivelse;
    String bygg_Id;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.legg_til_rom);
        setTitle(R.string.leggTilRom);

        Intent i = this.getIntent();
        bygg_Id = i.getExtras().getString(getString(R.string.byggUt));
        romNr = findViewById(R.id.romNrinn);
        etasjeNr =  findViewById(R.id.etasjeNrinn);
        beskrivelse = findViewById(R.id.beskrivelseInn);
        kapasitet = findViewById(R.id.kapasitetInn);
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

        String etasjeNrText = etasjeNr.getText().toString();
        String beskrivelseTekst = beskrivelse.getText().toString();
        String kapasitetTekst = kapasitet.getText().toString();
        String romNrTekst = romNr.getText().toString();
        String feilMelding = "";
        Boolean utfylt = true;
        //sjekker at alle felter er fylt ut korrekt
        if (romNrTekst.equals("")) {
            feilMelding += getString(R.string.feilRomNr);
            utfylt = false;
            //siden romNr på oslomet kan bestå av bokstaver, tall og punktum sjekker vi ikke noe mer enn at det er fylt inn
            //hvis vi kun hadde hatt tall som romnr ville vi sjekket at det som var fylt inn var et tall
        }
        if (etasjeNrText.equals("")) {
            if(!feilMelding.equals("")) {
                feilMelding += ", ";
            }
            feilMelding += getString(R.string.feilRomNr);
            utfylt = false;
        }
        else {
            int etasjeNr;
            try {
                etasjeNr = Integer.parseInt(etasjeNrText);
                if( etasjeNr< 21 && etasjeNr < 0) {
                    if(!feilMelding.equals("")) {
                        feilMelding += ", ";
                    }
                    feilMelding +=  getString(R.string.feilEtasjeNr);
                    utfylt = false;
                }
            }
            catch (Exception e) {
                if(!feilMelding.equals("")) {
                    feilMelding += ", ";
                }
                feilMelding +=  getString(R.string.feilEtasjeNr);
                utfylt = false;
            }
        }
        if(kapasitetTekst.equals("")) {
            if(!feilMelding.equals("")) {
                feilMelding += ", ";
            }
            feilMelding += getString(R.string.feilKapasitet);
            utfylt = false;
        }
        else {
            int kapasitet;
            try {
                kapasitet = Integer.parseInt(kapasitetTekst);
                if( kapasitet < 0) {
                    if(!feilMelding.equals("")) {
                        feilMelding += ", ";
                    }
                    feilMelding +=  getString(R.string.feilKapasitet);
                    utfylt = false;
                }
            }
            catch (Exception e) {
                if(!feilMelding.equals("")) {
                    feilMelding += ", ";
                }
                feilMelding +=  getString(R.string.feilKapasitet);
                utfylt = false;
            }
        }
        if(beskrivelseTekst.equals("")) {
            if(!feilMelding.equals("")) {
                feilMelding += ", ";
            }
            feilMelding += getString(R.string.feilBeskrivelse);
            utfylt = false;
        }

        if(utfylt){
            jsonLeggTil(beskrivelseTekst,romNrTekst,kapasitetTekst,etasjeNrText);
            finish();
        }
        else {
            feilMelding += " " + getString(R.string.ikkeFyltUtKorrekt);
            Toast.makeText(getApplicationContext(),feilMelding, Toast.LENGTH_SHORT).show();
        }
    }

    public void jsonLeggTil(String beskrivelse, String romNr, String kapasitet, String etasjeNr) {
        String[] splittetBeskrivelse = beskrivelse.split("\\s+");
        String formatertBeskrivelse = splittetBeskrivelse[0];
        for (int i = 1; i < splittetBeskrivelse.length; i++) {
            formatertBeskrivelse += "%20" + splittetBeskrivelse[i];
        }
        String json = "http://student.cs.hioa.no/~s333761//jsoninRom.php/?Bygg_id="+bygg_Id + "&EtasjeNr=" + etasjeNr + "&RomNr="
                + romNr + "&Kapasitet=" + kapasitet + "&Beskrivelse=" + formatertBeskrivelse;
        SendJSON task = new SendJSON();
        task.execute((new String[]{json}));

    }
}
