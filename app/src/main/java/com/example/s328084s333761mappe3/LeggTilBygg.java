package com.example.s328084s333761mappe3;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

public class LeggTilBygg extends AppCompatActivity {

    //Legger til ikon for lagre i actionbaren
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.leggtil_meny, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*
        if (item.getItemId() == R.id.leggTilAction) {
            //Når brukeren trykker på lagre-ikonet kjøres leggtil-funksjonen
            leggtil();
        } else {
            return super.onOptionsItemSelected(item);
        }
        */
        return true;
    }
}
