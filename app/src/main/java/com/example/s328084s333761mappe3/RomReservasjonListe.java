package com.example.s328084s333761mappe3;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

public class RomReservasjonListe extends AppCompatActivity {

    int rom_id;
    String etasjeNrText;
    String kapasitetText;
    String beskrivelseText;
    String romNrText;
    TextView romNr;
    TextView etasjeNr;
    TextView kapasitet;
    TextView beskrivelse;
    static ArrayList<RomReservasjon> reservasjoner;

    //Funksjon som returnerer listen med romreservasjoner
    public static ArrayList<RomReservasjon> reservasjonliste() {
        return reservasjoner;
    }

    public RomReservasjonListe() {}

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.liste_meny, menu);
        //Gjør ikonet svart
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
            //Når brukeren trykker på pluss-ikonet sendes bruker til LeggTilRomReservasjon-aktivitet
            Intent leggTilIntent = new Intent(this,LeggTilRomReservasjon.class);
            //Sender med info om rommet
            leggTilIntent.putExtra(getString(R.string.romUt),rom_id);
            leggTilIntent.putExtra(getString(R.string.romNr),romNrText);
            startActivity(leggTilIntent);
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reservasjonliste_layout);

        setTitle(R.string.reservasjoner);

        //Henter infoen som ble sendt med intenten
        Intent i = this.getIntent();
        rom_id = i.getExtras().getInt(getString(R.string.romUt));
        romNrText = i.getExtras().getString(getString(R.string.romNr));
        kapasitetText = i.getExtras().getString(getString(R.string.romKapasitet));
        beskrivelseText = i.getExtras().getString(getString(R.string.romBeskrivelse));
        etasjeNrText = i.getExtras().getString(getString(R.string.romEtasje));

        etasjeNr = (TextView) findViewById(R.id.etasjeNr);
        romNr = (TextView) findViewById(R.id.romNr);
        kapasitet = (TextView) findViewById(R.id.kapasitet);
        beskrivelse = (TextView) findViewById(R.id.beskrivelse);

        //Henter alle romreservasjoner av rommet
        GetRomReservasjonJSON task = new GetRomReservasjonJSON();
        task.execute(new
                String[]{"http://student.cs.hioa.no/~s333761//jsonoutRomReservasjon.php/?Rom_id="+rom_id});
        romNr.setText(romNrText);
        beskrivelse.setText(beskrivelseText);
        kapasitet.setText(kapasitetText);
        etasjeNr.setText(etasjeNrText);

    }

    //Oppdaterer listefragmenetet
    public void oppdater() {

        String json ="http://student.cs.hioa.no/~s333761//jsonoutRomReservasjon.php/?Rom_id="+rom_id;
        GetRomReservasjonJSON task = new GetRomReservasjonJSON();
        task.execute(new
                String[]{json});
    }

    //Oppdaterer listen når brukeren returnerer til aktiviteten
    @Override
    public void onResume() {
        super.onResume();
        oppdater();
    }

    //Funksjon som lager en liste med romreservasjoner fra jsonstrengen
    public ArrayList<RomReservasjon> lagRomReservasjonliste(String romJson) {
        //Splitter opp romreservasjonene
        String[] splittet = romJson.split("_");
        ArrayList<RomReservasjon> reservasjonliste = new ArrayList<>();
        for (String string : splittet) {
            //Splitter opp feltene i romreservasjonen
            String[] splittetRom = string.split(";");
            RomReservasjon reservasjon = new RomReservasjon();
            reservasjon.Id = Integer.parseInt(splittetRom[0]);
            reservasjon.Rom_Id = Integer.parseInt(splittetRom[1]);
            reservasjon.dato = splittetRom[2];
            reservasjon.fra = splittetRom[3];
            reservasjon.til = splittetRom[4];
            reservasjonliste.add(reservasjon);
        }
        return reservasjonliste;
    }

    //Metode som tar inn en liste med reservasjons-objekter og sorterer disse på dato og tid
    public ArrayList<RomReservasjon> sorterReservasjoner(ArrayList<RomReservasjon> reservasjoner) {
        ArrayList<RomReservasjon> sortertListe = new ArrayList(); //Oppretter en ny liste med reservasjons-objekter
        sortertListe.add(reservasjoner.get(0)); //Legger til det første reservasjonen i innparameter-listen i det sorterte arrayet
        boolean leter; //boolean som sier om vi leter etter riktig plass i sortertListe eller om vi har funnet riktig plass
        for (int i = 1; i < reservasjoner.size(); i++) {
            leter = true;
            String dato = reservasjoner.get(i).dato;
            //Splitter opp datoen for å lage egne int-variabler for dag, måned og år som vi kan sammenligne
            String[] splittet = dato.split("\\.");
            int dag = Integer.parseInt(splittet[0]);
            int måned = Integer.parseInt(splittet[1]);
            int år = Integer.parseInt(splittet[2]);
            int plassering = 0; //Plasseringen til det originale reservasjonen i den sorterte listen
            while(leter) {
                String datoSortert = sortertListe.get(plassering).dato;
                //Splitter opp datoen for å lage egne int-variabler for dag, måned og år som vi kan sammenligne
                String[] splittetSortert = datoSortert.split("\\.");
                int dagSortert = Integer.parseInt(splittetSortert[0]);
                int månedSortert = Integer.parseInt(splittetSortert[1]);
                int årSortert = Integer.parseInt(splittetSortert[2]);
                if(årSortert == år) { //Hvis begge reservasjonene er i samme år må vi se videre på måneden
                    if(månedSortert == måned) { //Hvis begge reservasjonene er i samme måned må vi se videre må dagen
                        if(dagSortert == dag) { //Hvis begge reservasjonene er på samme dag må vi se videre på tiden
                            String tid = reservasjoner.get(i).fra;
                            //Splitter opp tiden for å lage egne int-variabler for time og minutter som vi kan sammenligne
                            String tidSortert = sortertListe.get(plassering).fra;
                            String[] splittetTid = tid.split(":");
                            String[] splittetTidSortert = tidSortert.split(":");
                            int time = Integer.parseInt(splittetTid[0]);
                            int timeSortert = Integer.parseInt(splittetTidSortert[0]);
                            int minutt = Integer.parseInt(splittetTid[1]);
                            int minuttSortert = Integer.parseInt(splittetTidSortert[1]);

                            if(timeSortert == time) { //Hvis begge reservasjonene finner sted i samme time må vi se videre på minuttene
                                if(minuttSortert == minutt) {
                                    //Hvis begge reservasjonene finner sted på nøyaktig samtidig legger vi til originalreservasjonen bakerst i den sorterte listen
                                    sortertListe.add(reservasjoner.get(i));
                                    leter = false;
                                }
                                else if(minuttSortert < minutt) {
                                    //Hvis originalreservasjonene sin minutt-verdi er høyere enn reservasjonen vi sammenligner med i den sorterte listen
                                    //så finner originalreservasjonen sted etter reservasjonen vi sammenligner med, og originalreservasjonen skal derfor legges
                                    // inn bak reservasjonen i den sorterte listen. Øker derfor plassering-variabelen med en
                                    plassering++;
                                    if(plassering == sortertListe.size()) {
                                        //Hvis plassering er like stor som antall elementer i den sorterte listen
                                        //betyr det at vi er på slutten av den sorterte listen og det er ikke flere
                                        //reservasjoner å sammenligne med. Legger derfor originalreservasjonen bakerst i den sorterte listen
                                        sortertListe.add(reservasjoner.get(i));
                                        leter = false;
                                    }
                                }
                                else {
                                    //Originalreservasjonen sin minutt-verdi er lavere enn reservasjonen vi sammenligner med i den sorterte listen
                                    //som betyr at originalreservasjonen finner sted før reservasjonen vi sammenligner med. Originalreservasjonen legges derfor
                                    //inn på plassen foran reservasjonen vi sammenligner med
                                    sortertListe.add(plassering,reservasjoner.get(i));
                                    leter = false;
                                }
                            }
                            else if(timeSortert < time) {
                                //Hvis originalreservasjonen sin time-verdi er høyere enn reservasjonen vi sammenligner med i den sorterte listen
                                //så finner originalreservasjonen sted etter reservasjonen vi sammenligner med, og originalreservasjonen skal derfor legges
                                //inn bak reservasjonen i den sorterte listen. Øker derfor plassering-variabelen med en
                                plassering++;
                                if(plassering == sortertListe.size()) {
                                    sortertListe.add(reservasjoner.get(i));
                                    leter = false;
                                }
                            }
                            else {
                                //Originalreservasjonen sin time-verdi er lavere enn reservasjonen vi sammenligner med i den sorterte listen
                                //som betyr at originalreservasjonen finner sted før reservasjonen vi sammenligner med. Originalreservasjonen legges derfor
                                //inn på plassen foran reservasjonen vi sammenligner med
                                sortertListe.add(plassering,reservasjoner.get(i));
                                leter = false;
                            }
                        }
                        else if(dagSortert < dag) {
                            //Hvis originalreservasjonen sin dag-verdi er høyere enn reservasjonen vi sammenligner med i den sorterte listen
                            //så finner originalreservasjonen sted etter reservasjonen vi sammenligner med, og originalreservasjonen skal derfor legges
                            //inn bak reservasjonen i den sorterte listen. Øker derfor plassering-variabelen med en
                            plassering++;
                            if(plassering == sortertListe.size()) {
                                sortertListe.add(reservasjoner.get(i));
                                leter = false;
                            }
                        }
                        else {
                            //OriginalReservasjonen sin dag-verdi er lavere enn reservasjonen vi sammenligner med i den sorterte listen
                            //som betyr at originalreservasjonen finner sted før reservasjonen vi sammenligner med. Originalreservasjonen legges derfor
                            //inn på plassen foran reservasjonen vi sammenligner med
                            sortertListe.add(plassering,reservasjoner.get(i));
                            leter = false;
                        }
                    }
                    else if(månedSortert < måned) {
                        //Hvis originalreservasjonen sin måned-verdi er høyere enn reservasjonen vi sammenligner med i den sorterte listen
                        //så finner originalreservasjon sted etter reservasjonen vi sammenligner med, og originalreservasjonen skal derfor legges
                        //inn bak reservasjonen i den sorterte listen. Øker derfor plassering-variabelen med en
                        plassering++;
                        if(plassering == sortertListe.size()) {
                            sortertListe.add(reservasjoner.get(i));
                            leter = false;
                        }
                    }
                    else {
                        //Originalreservasjonen sin måned-verdi er lavere enn reservasjonen vi sammenligner med i den sorterte listen
                        //som betyr at originalreservasjonen finner sted før reservasjonen vi sammenligner med. Originalreservasjonen legges derfor
                        //inn på plassen foran reservasjonen vi sammenligner med
                        sortertListe.add(plassering,reservasjoner.get(i));
                        leter = false;
                    }
                }
                else if(årSortert < år) {
                    //Hvis originalreservasjonen sin år-verdi er høyere enn reservasjonen vi sammenligner med i den sorterte listen
                    //så finner originalreservasjonen sted etter reservasjonen vi sammenligner med, og originalreservasjonen skal derfor legges
                    //inn bak reservasjonen i den sorterte listen. Øker derfor plassering-variabelen med en
                    plassering++;
                    if(plassering == sortertListe.size()) {
                        sortertListe.add(reservasjoner.get(i));
                        leter = false;
                    }
                }
                else {
                    //Originalreservasjonen sin år-verdi er lavere enn reservasjonen vi sammenligner med i den sorterte listen
                    //som betyr at originalreservasjonen finner sted før reservasjonen vi sammenligner med. Originalreservasjonen legges derfor
                    //inn på plassen foran reservasjonen vi sammenligner med
                    sortertListe.add(plassering,reservasjoner.get(i));
                    leter = false;
                }
            }
        }
        return sortertListe;
    }

    private class GetRomReservasjonJSON extends AsyncTask<String, Void,String> {

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
                        //Henter ut data fra arrayen og lagrer som en streng
                        for (int i = 0; i < mat.length(); i++) {
                            JSONObject jsonobject = mat.getJSONObject(i);
                            String id = jsonobject.getString("id");
                            String rom_id = jsonobject.getString("Rom_id");
                            String dato = jsonobject.getString("Dato");
                            String tidFra = jsonobject.getString("TidFra");
                            String tidTil = jsonobject.getString("TidTil");

                            retur = retur + id + ";" + rom_id + ";" + dato + ";" + tidFra + ";" + tidTil;
                            if(!(i == mat.length()-1)) {
                                retur += "_";
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
        protected void onPostExecute(String s) {

            ListView lv = (ListView) findViewById(R.id.liste);
            if(!s.equals("")) {
                //Lager en liste med romreservasjoner fra jsonstrengen
                reservasjoner = lagRomReservasjonliste(s);
                reservasjoner = sorterReservasjoner(reservasjoner);
                //Setter de inn i listen
                final ReservasjonAdapter adapter = new ReservasjonAdapter(RomReservasjonListe.this, reservasjoner);
                lv.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

        }
    }
}
