package com.example.s328084s333761mappe3;

import android.app.Activity;
import android.app.Fragment;
import android.app.LauncherActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
    public View v;
    //Context applicationContext = MapsActivity.getContextOfApplication();

    public RomListe() {}

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
            //Når brukeren trykker på pluss-ikonet kjøres leggtil-funksjonen
            Intent leggTilIntent = new Intent(this,LeggTilRom.class);
            leggTilIntent.putExtra(getString(R.string.byggUt),bygg_id);
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
        String splittet[] = koordinater.split(",");
        String formatertKoordinater = splittet[0].substring(0,5) +"," + splittet[1].substring(0,5);
        koordinaterBox.setText(formatertKoordinater);
        //Oppretter en liste med alle rom-objekter

        GetRomJSON task = new GetRomJSON();
        String json = "http://student.cs.hioa.no/~s333761//jsonoutRom.php/?Bygg_id="+bygg_id;
        Log.d("TAG", json);
        task.execute(new
                String[]{json});

    }

    //Oppdaterer listefragmenetet
    public void oppdater() {
        GetRomJSON task = new GetRomJSON();
        String json = "http://student.cs.hioa.no/~s333761//jsonoutRom.php/?Bygg_id="+bygg_id;
        Log.d("TAG", json);
        task.execute(new
                String[]{json});
    }


    @Override
    public void onResume() {
        super.onResume();
        oppdater();
    }

    public ArrayList<Rom> lagRomliste(String romJson) {
        String[] splittet = romJson.split(":");
        ArrayList<Rom> romliste = new ArrayList<>();
        for (String string : splittet) {
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



    private class GetRomJSON extends AsyncTask<String, Void,String> {
        JSONObject jsonObject;
        SharedPreferences prefs;

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
            Log.d("TAG","I post rom "+ s);
            if(!s.equals("")) {
                ArrayList<Rom> rom = lagRomliste(s);
                final RomAdapter adapter = new RomAdapter(RomListe.this,rom);
                lv.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Rom data = adapter.getItem(i);
                        Intent reservasjonIntent = new Intent(RomListe.this,RomReservasjonListe.class); //Åpne romreservasjon her
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
