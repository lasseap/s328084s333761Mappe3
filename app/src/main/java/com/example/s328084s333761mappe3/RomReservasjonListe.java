package com.example.s328084s333761mappe3;

import android.content.Intent;
import android.content.SharedPreferences;
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

import java.util.ArrayList;

public class RomReservasjonListe extends AppCompatActivity {

    private String rom_id;
    SharedPreferences prefs;
    TextView romNr;
    TextView etasjeNr;
    TextView kapasitet;
    TextView beskrivelse;

    public RomReservasjonListe() {}

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.liste_meny, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.leggTilAction) {
            //Når brukeren trykker på pluss-ikonet sendes bruker til LeggTilRomREservasjon-aktivitet
            Intent leggTilIntent = new Intent(this,LeggTilRomReservasjon.class);
            leggTilIntent.putExtra(getString(R.string.romUt),rom_id);
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

        Intent i = this.getIntent();
        String rom_id = i.getExtras().getString("romUt");
        GetRomJSON taskRom = new GetRomJSON();
        taskRom.execute(new String[]{"http://student.cs.hioa.no/~s333761//jsonoutRom.php/?Id="+ rom_id});
        ListView lv = (ListView) findViewById(R.id.liste);
        etasjeNr = (TextView) findViewById(R.id.etasjeNr);
        romNr = (TextView) findViewById(R.id.romNr);
        kapasitet = (TextView) findViewById(R.id.kapasitet);
        beskrivelse = (TextView) findViewById(R.id.beskrivelse);
        String jsonBygg = prefs.getString(getString(R.string.byggUt),"");
        String[] splittet = jsonBygg.split(";");
        rom_id = splittet[0];
        //Oppretter en liste med alle møte-objekter

        GetRomJSON task = new GetRomJSON();
        task.execute(new
                String[]{"http://student.cs.hioa.no/~s333761//jsonoutRom.php/?Id="+rom_id});
        romNr.setText(splittet[3]);
        beskrivelse.setText(splittet[5]);
        kapasitet.setText(splittet[4]);
        etasjeNr.setText(splittet[2]);
        String romJson = prefs.getString(getString(R.string.reservasjonUt),"");
        ArrayList<RomReservasjon> resevasjoner = lagRomReservasjonliste(romJson);
        final ReservasjonAdapter adapter = new ReservasjonAdapter(this,resevasjoner);
        lv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    //Oppdaterer listefragmenetet
    public void oppdater() {
        ListView lv = (ListView) findViewById(R.id.liste);
        GetRomJSON task = new GetRomJSON();
        task.execute(new
                String[]{"http://student.cs.hioa.no/~s333761//jsonoutRom.php/?Bygg_id="});
        String romJson = prefs.getString(getString(R.string.romUt),"");
        ArrayList<RomReservasjon> resevasjoner = lagRomReservasjonliste(romJson);
        final ReservasjonAdapter adapter = new ReservasjonAdapter(this,resevasjoner);
        lv.setAdapter(adapter);

    }


    @Override
    public void onResume() {
        super.onResume();
        oppdater();
    }

    public ArrayList<RomReservasjon> lagRomReservasjonliste(String romJson) {
        String[] splittet = romJson.split(":");
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
}
