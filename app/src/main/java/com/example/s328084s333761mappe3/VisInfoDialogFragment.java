package com.example.s328084s333761mappe3;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class VisInfoDialogFragment extends DialogFragment {

    TextView adresseBox;
    TextView koordinaterBox;
    TextView etasjeBox;
    TextView beskrivelseBox;
    Button visRomKnapp;
    Button avsluttKnapp;
    String adresse;
    String Id;
    String antEtasjer;
    SharedPreferences prefs;
/*
    private DialogClickListener callback;


    public interface DialogClickListener {
        public void onRomClick();
        public void onReservasjonClick();
        public void onAvsluttClick();
    }

 */

    /*public static VisInfoDialogFragment newInstance(String id) {
        EditNameDialogFragment frag = new EditNameDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

     */

    public VisInfoDialogFragment(String adresse) {
        this.adresse = adresse;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.vis_info_dialog_layout, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        adresseBox = (TextView) view.findViewById(R.id.adresse);
        koordinaterBox = (TextView) view.findViewById(R.id.koordinater);
        etasjeBox = (TextView) view.findViewById(R.id.antEtasjer);
        beskrivelseBox = (TextView) view.findViewById(R.id.beskrivelse);
        GetByggJSON bygg = new GetByggJSON();
        String[] splittet = adresse.split("\\s+");
        String formatertAdresse = splittet[0];
        for (int i = 1; i < splittet.length; i++) {
            formatertAdresse += "%20" + splittet[i];
        }
        bygg.execute(new
                String[]{"http://student.cs.hioa.no/~s333761/JSONoutBygg.php/?Adresse="+formatertAdresse});
        Context applicationContext = MapsActivity.getContextOfApplication();


        avsluttKnapp = (Button) view.findViewById(R.id.avsluttKnapp);
        visRomKnapp = (Button) view.findViewById(R.id.leggTilRomKnapp);

        avsluttKnapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        visRomKnapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ivisrom = new Intent(getActivity(), RomListe.class);
                ivisrom.putExtra(getString(R.string.byggUt), adresseBox.getText());
                ivisrom.putExtra(getString(R.string.bygg_id),Id);
                ivisrom.putExtra(getString(R.string.bygg_etasjer),antEtasjer);
                startActivity(ivisrom);
            }
        });

        // Fetch arguments from bundle and set title
       // String title = getArguments().getString("title", "Enter Name");
        //getDialog().setTitle(title);
        // Show soft keyboard automatically and request focus to field
    }
/*
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            callback = (DialogClickListener)getActivity();
        }
        catch (ClassCastException e) {
            throw new  ClassCastException("kallende klasse må implementere interface");
        }
    }

 */

    private class GetByggJSON extends AsyncTask<String, Void,String> {
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

                        JSONObject jsonobject = mat.getJSONObject(0);
                        String id = jsonobject.getString("id");
                        String beskrivelse = jsonobject.getString("Beskrivelse");
                        String adresse = jsonobject.getString("Adresse");
                        String koordinater = jsonobject.getString("Koordinater");
                        String antEtasjer = jsonobject.getString("AntEtasjer");
                        retur = id + ";"+ beskrivelse + ";" + adresse + ";" + koordinater + ";" + antEtasjer;

                        return retur;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return retur;
                } catch (Exception e) {
                    Log.d("Tag","get bygg feilet" + e.getMessage());
                    return "";
                }
            }
            return retur;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("TAG",s);
            String[] splittet = s.split(";");
            adresseBox.setText(splittet[2]);
            koordinaterBox.setText(splittet[3]);
            etasjeBox.setText(splittet[4]);
            antEtasjer = splittet[4];
            beskrivelseBox.setText(splittet[1]);
            Id = splittet[0];
        }
    }

}

