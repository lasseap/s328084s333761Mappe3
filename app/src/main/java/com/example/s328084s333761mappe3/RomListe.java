package com.example.s328084s333761mappe3;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import java.util.ArrayList;

public class RomListe extends AppCompatActivity {

    private String bygg_id;
    SharedPreferences prefs;
    public View v;

    public RomListe() {}

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.liste_meny, menu);
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

        Intent i = this.getIntent();
        String bygg_adresse = i.getExtras().getString("byggUt");
        GetByggJSON taskBygg = new GetByggJSON();
        taskBygg.execute(new String[]{"http://student.cs.hioa.no/~s333761//jsonoutBygg.php/?Adresse="+ bygg_adresse});
        ListView lv = (ListView) v.findViewById(R.id.liste);
        TextView adresse = (TextView) v.findViewById(R.id.adresse);
        TextView koordinater = (TextView) v.findViewById(R.id.koordinater);
        TextView antEtasjer = (TextView) v.findViewById(R.id.antEtasjer);
        TextView beskrivelse = (TextView) v.findViewById(R.id.beskrivelse);
        String jsonBygg = prefs.getString(getString(R.string.byggUt),"");
        String[] splittet = jsonBygg.split(";");
        bygg_id = splittet[0];
        //Oppretter en liste med alle møte-objekter

        GetRomJSON task = new GetRomJSON();
        task.execute(new
                String[]{"http://student.cs.hioa.no/~s333761//jsonoutRom.php/?Bygg_id="+bygg_id});
        adresse.setText(splittet[2]);
        beskrivelse.setText(splittet[1]);
        koordinater.setText(splittet[3]);
        antEtasjer.setText(splittet[4]);
        String romJson = prefs.getString(getString(R.string.romUt),"");
        ArrayList<Rom> rom = lagRomliste(romJson);
        final RomAdapter adapter = new RomAdapter(this,rom);
        lv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Rom data = adapter.getItem(i);
                Intent reservasjonIntent = new Intent(); //Åpne romreservasjon her
            }
        });
    }

    //Oppdaterer listefragmenetet
    public void oppdater() {
        ListView lv = (ListView) v.findViewById(R.id.liste);
        GetRomJSON task = new GetRomJSON();
        task.execute(new
                String[]{"http://student.cs.hioa.no/~s333761//jsonoutRom.php/?Bygg_id="});
        String romJson = prefs.getString(getString(R.string.romUt),"");
        ArrayList<Rom> rom = lagRomliste(romJson);
        final RomAdapter adapter = new RomAdapter(this,rom);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Rom data = adapter.getItem(i);
                //Åpne romreservasjon her
            }
        });
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
}
