package com.example.s328084s333761mappe3;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener {

    public GoogleMap mMap;
    public static Context contextOfApplication;
    SharedPreferences prefs;
    public static Marker marker;

    //Funksjon for å slette en markør på kartet, brukes hvis opprettelse av bygg avbrytes
    public static void deleteMarker(){
        if(!(marker == null)) {
            marker.remove();
            marker = null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        mapFragment.getMapAsync(this);
        contextOfApplication = getApplicationContext();
    }
    //Hjelpefunksjon for å få konteksten til mapsActivity
    public static Context getContextOfApplication(){
        return contextOfApplication;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Setter kamera på Pilestredet
        LatLng pilestredet = new LatLng(59.921559, 10.733572);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(pilestredet));
        moveToCurrentLocation(pilestredet);

        //Lager et geocoder-objekt for å finne adresser
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        //Henter antallet bygg som er lagret i shared preferences
        int antall = prefs.getInt(getString(R.string.antallMarkers), 0);
        //Henter ut koordinatene for byggene
        for (int i = 0;i < antall;i++) {
            int ant = i + 1;
            String koordinater = prefs.getString("" + ant, "noe gikk galt");
            String[] splittet = koordinater.split(",");
            double latitude = Double.parseDouble(splittet[0]);
            double longitude = Double.parseDouble(splittet[1]);
            try {
                //Henter adressen til byggene
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                String[] splittetAdresse = addresses.get(0).getAddressLine(0).split(",");
                //Oppretter markører på kartet med adressen til bygget
                LatLng koord = new LatLng(latitude, longitude);
                mMap.addMarker(new MarkerOptions().position(koord).title(splittetAdresse[0]));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //Deklarerer listener for å klikke på kartet og på markører
        mMap.setOnMapClickListener(this);
        mMap.setOnMarkerClickListener(this);
    }

    //Funksjon for når kartet klikkes på
    @Override
    public void onMapClick(LatLng point) {
        //Flytter fokus til stedet som ble klikket på
        mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
        //Lager et geokoder-objekt for å finne adresse
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(point.latitude, point.longitude, 1);
            String[] splittet = addresses.get(0).getAddressLine(0).split(",");
            //Sjekker om punktet har en lovlig adresse
            if(splittet.length < 3) {
                Toast.makeText(getApplicationContext(),R.string.ikkeAdresse, Toast.LENGTH_SHORT).show();
            }
            else {
                //Henter ut antall lagrede markører for bygg
                int antall = prefs.getInt(getString(R.string.antallMarkers), 0);
                //Åpner nytt vindu med oppretelse av bygg
                Intent ileggtilbygg = new Intent(this, LeggTilBygg.class);
                ileggtilbygg.putExtra(getString(R.string.adresse),splittet[0]);
                String koordinater = point.latitude + "," + point.longitude;
                ileggtilbygg.putExtra(getString(R.string.koordinater),koordinater);
                antall++;
                SharedPreferences.Editor editor = prefs.edit();
                //Legger inn ny markør sine koordinater og øker antallet lagrede markører
                editor.putString(""+antall,koordinater);
                editor.putInt(getString(R.string.antallMarkers),antall);
                editor.apply();
                //Oppretter markør og legger i variabel med siste oprettete markør
                marker = mMap.addMarker(new MarkerOptions().position(point).title(splittet[0]));
                startActivity(ileggtilbygg);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    //Funksjon for klikking på markør
    @Override
    public boolean onMarkerClick(Marker marker) {
        //Viser infodialog box og sender med adressen lagret i markøren
        visInfoDialog(marker.getTitle());
        return false;
    }

    public void visInfoDialog(String adresse){
        DialogFragment dialog = new VisInfoDialogFragment(adresse);
        dialog.show(getSupportFragmentManager(),"VisInfoDialog");
    }

    //Funksjon for å gå til nåværende posisjon
    private void moveToCurrentLocation(LatLng currentLocation) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,15));
        // Zoom in, animating the camera.
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
    }
}