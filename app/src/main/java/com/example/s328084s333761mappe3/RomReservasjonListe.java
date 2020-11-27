package com.example.s328084s333761mappe3;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
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

public class RomReservasjonListe extends AppCompatActivity {

    int rom_id;
    String etasjeNrText;
    String kapasitetText;
    String beskrivelseText;
    String romNrText;

    SharedPreferences prefs;
    TextView romNr;
    TextView etasjeNr;
    TextView kapasitet;
    TextView beskrivelse;
    static ArrayList<RomReservasjon> resevasjoner;

    public static ArrayList<RomReservasjon> reservasjonliste() {
        return resevasjoner;
    }

    public RomReservasjonListe() {}

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.liste_meny, menu);
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
            //Når brukeren trykker på pluss-ikonet sendes bruker til LeggTilRomREservasjon-aktivitet
            Intent leggTilIntent = new Intent(this,LeggTilRomReservasjon.class);
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

        //Oppretter en liste med alle møte-objekter

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
        Log.d("TAG", "oppdaterer res "+ json);
        GetRomReservasjonJSON task = new GetRomReservasjonJSON();
        task.execute(new
                String[]{json});


    }


    @Override
    public void onResume() {
        super.onResume();
        oppdater();
    }

    public ArrayList<RomReservasjon> lagRomReservasjonliste(String romJson) {
        String[] splittet = romJson.split("_");
        ArrayList<RomReservasjon> reservasjonliste = new ArrayList<>();
        for (String string : splittet) {
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
            Log.d("TAG","I post reservasjon: "+s);
            ListView lv = (ListView) findViewById(R.id.liste);
            if(!s.equals("")) {
                resevasjoner = lagRomReservasjonliste(s);
                final ReservasjonAdapter adapter = new ReservasjonAdapter(RomReservasjonListe.this, resevasjoner);
                lv.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

        }
    }
}
