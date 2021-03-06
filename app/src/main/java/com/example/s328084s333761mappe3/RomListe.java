package com.example.s328084s333761mappe3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class RomListe extends AppCompatActivity {

    private String bygg_id;
    String adresseText;
    String antEtasjerText;
    TextView adresse;
    TextView antEtasjer;
    String koordinater;
    String beskrivelse;
    TextView koordinaterBox;
    TextView beskrivelseBox;

    public RomListe() {}

    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.liste_meny, menu);
        //gjør ikonet svart
        MenuItem leggTilItem = menu.findItem(R.id.leggTilAction);
        Drawable leggTilIcon = DrawableCompat.wrap(leggTilItem.getIcon());
        ColorStateList colorSelector = ResourcesCompat.getColorStateList(getResources(), R.color.black, getTheme());
        DrawableCompat.setTintList(leggTilIcon, colorSelector);
        leggTilItem.setIcon(leggTilIcon);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.leggTilAction) {
            //Når brukeren trykker på pluss-ikonet kjøres leggtil-funksjonen
            Intent leggTilIntent = new Intent(this,LeggTilRom.class);
            //Sender info om bygget med intentet
            leggTilIntent.putExtra(getString(R.string.byggUt),bygg_id);
            leggTilIntent.putExtra(getString(R.string.antall_etasjer),antEtasjerText);
            startActivity(leggTilIntent);
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.romliste_layout);
        setTitle(R.string.rom);

        //Henter info som ble sendt med intentet
        Intent i = this.getIntent();
        adresseText = i.getExtras().getString(getString(R.string.byggUt));
        antEtasjerText = i.getExtras().getString(getString(R.string.bygg_etasjer));
        bygg_id = i.getExtras().getString(getString(R.string.bygg_id));
        beskrivelse = i.getExtras().getString(getString(R.string.bygg_beskrivelse));
        koordinater = i.getExtras().getString(getString(R.string.bygg_koordinater));

        adresse = (TextView) findViewById(R.id.adresse);
        antEtasjer = (TextView) findViewById(R.id.antEtasjer);
        koordinaterBox = findViewById(R.id.koordinater);
        beskrivelseBox = findViewById(R.id.beskrivelse);
        adresse.setText(adresseText);
        antEtasjer.setText(antEtasjerText);
        beskrivelseBox.setText(beskrivelse);
        //Viser bare de to første desimalene av koordinatene
        String splittet[] = koordinater.split(",");
        String formatertKoordinater = splittet[0].substring(0,5) +"," + splittet[1].substring(0,5);
        koordinaterBox.setText(formatertKoordinater);
        //Oppretter en liste med alle rom-objekter

        //Henter alle rommene til bygget fra webserver
        GetRomJSON task = new GetRomJSON();
        String json = "http://student.cs.hioa.no/~s333761//jsonoutRom.php/?Bygg_id="+bygg_id;
        task.execute(new
                String[]{json});

    }

    //Oppdaterer listefragmenetet
    public void oppdater() {
        GetRomJSON task = new GetRomJSON();
        String json = "http://student.cs.hioa.no/~s333761//jsonoutRom.php/?Bygg_id="+bygg_id;
        task.execute(new
                String[]{json});
    }


    //Oppdaterer listen når brukeren returnerer til aktiviteten
    @Override
    public void onResume() {
        super.onResume();
        oppdater();
    }

    //Funksjon for å lage en liste med rom fra jsonstrengen
    public ArrayList<Rom> lagRomliste(String romJson) {
        //Splitter opp strengen for hvert rom
        String[] splittet = romJson.split(":");
        ArrayList<Rom> romliste = new ArrayList<>();
        for (String string : splittet) {
            //Splitter opp hvert rom
            String[] splittetRom = string.split(";");
            Rom rom = new Rom();
            rom.Id = Integer.parseInt(splittetRom[0]);
            rom.Bygg_Id = Integer.parseInt(splittetRom[1]);
            rom.EtasjeNr = splittetRom[2];
            rom.RomNr = splittetRom[3];
            rom.Kapasitet = splittetRom[4];
            rom.Beskrivelse = splittetRom[5];
            romliste.add(rom);
        }
        return romliste;
    }

    public ArrayList<Rom> sorterRom(ArrayList<Rom> romListe) {
        ArrayList<Rom> sortertListe = new ArrayList<>(); //Oppretter ny liste
        sortertListe.add(romListe.get(0)); //Legger til det første rommet i innparameter-listen i det sorterte arrayet
        boolean leter; //boolean som sier om vi leter etter riktig plass i sortertListe eller om vi har funnet riktig plass
        for (int i = 1; i < romListe.size(); i++) {
            leter = true;
            String etasje = romListe.get(i).EtasjeNr;
            int etasjeNr = Integer.parseInt(etasje);
            int plassering = 0; //Plasseringen til det originale reservasjonen i den sorterte listen
            while (leter) {
                String etasjeSortert = sortertListe.get(plassering).getEtasjeNr();
                int etasjeNrSortert = Integer.parseInt(etasjeSortert);
                if(etasjeNr < etasjeNrSortert) {
                    //Det nye rommet ligger i en etasje under den vi sjekker mot og skal derfor foran rommet i listen
                    sortertListe.add(plassering,romListe.get(i));
                    leter = false; //Ferdig med dette rommet og avslutter letingen
                }
                else if (etasjeNr == etasjeNrSortert) {
                    //Hvis de ligger i samme etasje skal det nye rommet ligge bakerst av rommene i den etasjen
                    plassering++;
                    if(plassering == sortertListe.size()) {
                        //Hvis plassering er like stor som antall elementer i den sorterte listen
                        //betyr det at vi er på slutten av den sorterte listen og det er ikke flere
                        //rom å sammenligne med. Legger derfor det nye bakerst i den sorterte listen
                        sortertListe.add(romListe.get(i));
                        leter = false;
                    }

                }
                else {
                    //Hvis det nye rommets etasjenr er større en sjekkrommet, skal den legges etter sjekkrommet
                    plassering++;
                    if(plassering == sortertListe.size()) {
                        //Hvis plassering er like stor som antall elementer i den sorterte listen
                        //betyr det at vi er på slutten av den sorterte listen og det er ikke flere
                        //rom å sammenligne med. Legger derfor det nye bakerst i den sorterte listen
                        sortertListe.add(romListe.get(i));
                        leter = false;
                    }

                }
            }
        }
        return sortertListe;
    }


    //Klasse for å hente rom fra webserver
    private class GetRomJSON extends AsyncTask<String, Void,String> {

        @Override
        protected String doInBackground(String... urls) {
            String retur = "";
            String s = "";
            String output = "";
            for (String url : urls) {
                try {
                    URL urlen = new URL(urls[0]);
                    HttpURLConnection conn = (HttpURLConnection)
                            urlen.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Accept",
                            "application/json");
                    if (conn.getResponseCode() != 200) {
                        throw new RuntimeException("Failed : HTTP error code : "
                                + conn.getResponseCode());
                    }
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            (conn.getInputStream())));
                    System.out.println("Output from Server .... \n");
                    while ((s = br.readLine()) != null) {
                        output = output + s;
                    }
                    conn.disconnect();
                    try {
                        JSONArray mat = new JSONArray(output);
                        //Henter ut data fra JSONarrayen og lagrer i en streng
                        for (int i = 0; i < mat.length(); i++) {
                            JSONObject jsonobject = mat.getJSONObject(i);
                            String id = jsonobject.getString("id");
                            String bygg_id = jsonobject.getString("Bygg_id");
                            String etasjeNr = jsonobject.getString("EtasjeNr");
                            String romNr = jsonobject.getString("RomNr");
                            String kapasitet = jsonobject.getString("Kapasitet");
                            String beskrivelse = jsonobject.getString("Beskrivelse");
                            retur = retur + id + ";" + bygg_id + ";" + etasjeNr + ";" + romNr + ";" + kapasitet + ";" + beskrivelse;
                            if(!(i == mat.length()-1)) {
                                retur += ":";
                            }
                        }
                        return retur;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return retur;
                } catch (Exception e) {
                    return "Noe gikk feil";
                }
            }
            return retur;
        }

        @Override
        protected void onPostExecute(String s) {
            ListView lv = (ListView) findViewById(R.id.liste);

            if(!s.equals("")) {
                //Lager en liste med rom fra jsonstrengen
                ArrayList<Rom> rom = lagRomliste(s);
                rom = sorterRom(rom);
                //Setter inn i listview
                final RomAdapter adapter = new RomAdapter(RomListe.this,rom);
                lv.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Rom data = adapter.getItem(i);
                        Intent reservasjonIntent = new Intent(RomListe.this,RomReservasjonListe.class); //Åpne romreservasjon her
                        //Sender med info om det valgte rommet
                        reservasjonIntent.putExtra(getString(R.string.romUt),data.Id);
                        reservasjonIntent.putExtra(getString(R.string.romEtasje),data.EtasjeNr);
                        reservasjonIntent.putExtra(getString(R.string.romBeskrivelse),data.Beskrivelse);
                        reservasjonIntent.putExtra(getString(R.string.romNr),data.RomNr);
                        reservasjonIntent.putExtra(getString(R.string.romKapasitet),data.Kapasitet);
                        startActivity(reservasjonIntent);
                    }
                });
            }
        }
    }
}
