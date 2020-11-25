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
import android.util.Log;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mapFragment.getMapAsync(this);
        contextOfApplication = getApplicationContext();
    }

    public static Context getContextOfApplication(){
        return contextOfApplication;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Pilestredet and move the camera
        LatLng pilestredet = new LatLng(59.921559, 10.733572);
        mMap.addMarker(new MarkerOptions().position(pilestredet).title("Pilestredet"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(pilestredet));
        moveToCurrentLocation(pilestredet);

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        int antall = prefs.getInt(getString(R.string.antallMarkers), 0);
        for (int i = 0;i < antall;i++) {
            int ant = i + 1;
            String koordinater = prefs.getString("" + ant, "noe gikk galt");
            String[] splittet = koordinater.split(",");
            double latitude = Double.parseDouble(splittet[0]);
            double longitude = Double.parseDouble(splittet[1]);
            try {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                Log.d("TAG", addresses.get(0).getAddressLine(0));
                String[] splittetAdresse = addresses.get(0).getAddressLine(0).split(",");
                LatLng koord = new LatLng(latitude, longitude);
                mMap.addMarker(new MarkerOptions().position(koord).title(splittetAdresse[0]));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        mMap.setOnMapClickListener(this);
        mMap.setOnMarkerClickListener(this);
    }

    @Override
    public void onMapClick(LatLng point) {
        mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(point.latitude, point.longitude, 1);
            Log.d("TAG", addresses.get(0).getAddressLine(0));
            String[] splittet = addresses.get(0).getAddressLine(0).split(",");
            if(splittet.length < 3) {
                Toast.makeText(getApplicationContext(),R.string.ikkeAdresse, Toast.LENGTH_SHORT).show();
            }
            else {
                MarkerOptions marker = new MarkerOptions().position(new LatLng(point.latitude, point.longitude)).title(splittet[0]);
                int antall = prefs.getInt(getString(R.string.antallMarkers), 0);
                mMap.addMarker(marker);
                Intent ileggtilbygg = new Intent(this, LeggTilBygg.class);
                ileggtilbygg.putExtra(getString(R.string.adresse),splittet[0]);
                String koordinater = point.latitude + "," + point.longitude;
                ileggtilbygg.putExtra(getString(R.string.koordinater),koordinater);
                antall++;
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(""+antall,koordinater);
                editor.putInt(getString(R.string.antallMarkers),antall);
                editor.apply();
                startActivity(ileggtilbygg);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        visInfoDialog(marker.getTitle());
        return false;
    }

    public void visInfoDialog(String adresse){
        DialogFragment dialog = new VisInfoDialogFragment(adresse);
        dialog.show(getSupportFragmentManager(),"VisInfoDialog");
    }

    private void moveToCurrentLocation(LatLng currentLocation) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,15));
        // Zoom in, animating the camera.
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
    }
}