package com.example.s328084s333761mappe3;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;

public class LeggTilRom extends AppCompatActivity {

    EditText romNr;
    EditText etasjeNr;
    EditText kapasitet;
    EditText beskrivelse;
    public String antallEtasjer;
    String bygg_Id;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.legg_til_rom);
        setTitle(R.string.leggTilRom);

        //henter ut info som ble sendt med intenten
        Intent i = this.getIntent();
        bygg_Id = i.getExtras().getString(getString(R.string.byggUt));
        antallEtasjer = i.getExtras().getString(getString(R.string.antall_etasjer));
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
        //gjør ikonet svart
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

        String etasjeNrText = etasjeNr.getText().toString();
        String beskrivelseTekst = beskrivelse.getText().toString();
        String kapasitetTekst = kapasitet.getText().toString();
        String romNrTekst = romNr.getText().toString();
        String feilMelding = "";
        String feilMelding2 = "";
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
                //sjekker at etasjenummer er et heltall fra 1 til 20
                etasjeNr = Integer.parseInt(etasjeNrText);
                if( etasjeNr> 20 || etasjeNr < 1) {
                    if(!feilMelding.equals("")) {
                        feilMelding += ", ";
                    }
                    feilMelding +=  getString(R.string.feilEtasjeNr);
                    utfylt = false;
                    feilMelding2 += getString(R.string.etasjenr_feil);
                }
                else {
                    //sjekker om etasjenr er høyere enn antall etasjer
                    int antall = Integer.parseInt(antallEtasjer);
                    if(etasjeNr > antall) {
                        if(!feilMelding.equals("")) {
                            feilMelding += ", ";
                        }
                        feilMelding +=  getString(R.string.feilEtasjeNr);
                        utfylt = false;
                        feilMelding2 += getString(R.string.etasjenr_feil);
                    }
                }
            }
            catch (Exception e) {
                if(!feilMelding.equals("")) {
                    feilMelding += ", ";
                }
                feilMelding +=  getString(R.string.feilEtasjeNr);
                utfylt = false;
                feilMelding2 += getString(R.string.etasjenr_feil);
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
            //sjekker at kapasitet er et heltall større enn 0
            int kapasitet;
            try {
                kapasitet = Integer.parseInt(kapasitetTekst);
                if( kapasitet < 1) {
                    if(!feilMelding.equals("")) {
                        feilMelding += ", ";
                    }
                    feilMelding +=  getString(R.string.feilKapasitet);
                    utfylt = false;
                    feilMelding2 += getString(R.string.kapasitet_feil);
                }
            }
            catch (Exception e) {
                if(!feilMelding.equals("")) {
                    feilMelding += ", ";
                }
                feilMelding +=  getString(R.string.feilKapasitet);
                utfylt = false;
                feilMelding2 += getString(R.string.kapasitet_feil);
            }
        }
        if(beskrivelseTekst.equals("")) {
            if(!feilMelding.equals("")) {
                feilMelding += ", ";
            }
            feilMelding += getString(R.string.feilBeskrivelse);
            utfylt = false;
        }

        //kaller funsjon for å legge til rom på webserver
        if(utfylt){
            jsonLeggTil(beskrivelseTekst,romNrTekst,kapasitetTekst,etasjeNrText);
            finish();
        }
        //viser feilmelding til bruker
        else {
            feilMelding += " " + getString(R.string.ikkeFyltUtKorrekt) +". ";
            Toast.makeText(getApplicationContext(),feilMelding + feilMelding2, Toast.LENGTH_SHORT).show();
        }
    }

    public void jsonLeggTil(String beskrivelse, String romNr, String kapasitet, String etasjeNr) {
        //formaterer mellomrom, siden httpurlconnection ikke takler vanlige mellomrom i urlen
        String[] splittetBeskrivelse = beskrivelse.split("\\s+");
        String formatertBeskrivelse = splittetBeskrivelse[0];
        for (int i = 1; i < splittetBeskrivelse.length; i++) {
            formatertBeskrivelse += "%20" + splittetBeskrivelse[i];
        }
        //lager SendJSON task for å legge inn rom på webserver
        String json = "http://student.cs.hioa.no/~s333761//jsoninRom.php/?Bygg_id="+bygg_Id + "&EtasjeNr=" + etasjeNr + "&RomNr="
                + romNr + "&Kapasitet=" + kapasitet + "&Beskrivelse=" + formatertBeskrivelse;
        SendJSON task = new SendJSON();
        task.execute((new String[]{json}));

    }
}
