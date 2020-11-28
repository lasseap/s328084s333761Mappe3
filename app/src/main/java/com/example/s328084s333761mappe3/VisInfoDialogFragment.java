package com.example.s328084s333761mappe3;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

    String adresse;
    String Id;
    String antEtasjer;
    String beskrivelse;
    String koordinater;

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
        //Finner tekstboksene i layouten
        adresseBox = (TextView) view.findViewById(R.id.adresse);
        koordinaterBox = (TextView) view.findViewById(R.id.koordinater);
        etasjeBox = (TextView) view.findViewById(R.id.antEtasjer);
        beskrivelseBox = (TextView) view.findViewById(R.id.beskrivelse);
        GetByggJSON bygg = new GetByggJSON();
        //Formaterer adresse fordi httpurlconnection ikke takler vanlige mellomrom i url-en
        String[] splittet = adresse.split("\\s+");
        String formatertAdresse = splittet[0];
        for (int i = 1; i < splittet.length; i++) {
            formatertAdresse += "%20" + splittet[i];
        }
        //Henter bygget fra webserver
        bygg.execute(new
                String[]{"http://student.cs.hioa.no/~s333761/JSONoutBygg.php/?Adresse="+formatertAdresse});

        visRomKnapp = (Button) view.findViewById(R.id.leggTilRomKnapp);
        visRomKnapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Åpner romliste-aktiviteten
                Intent ivisrom = new Intent(getActivity(), RomListe.class);
                //Sender info om bygget med intentet
                ivisrom.putExtra(getString(R.string.byggUt), adresseBox.getText());
                ivisrom.putExtra(getString(R.string.bygg_id),Id);
                ivisrom.putExtra(getString(R.string.bygg_etasjer),antEtasjer);
                ivisrom.putExtra(getString(R.string.bygg_beskrivelse),beskrivelse);
                ivisrom.putExtra(getString(R.string.bygg_koordinater),koordinater);
                startActivity(ivisrom);
            }
        });
    }

    //Klasse som henter ut et bygg fra webserver
    private class GetByggJSON extends AsyncTask<String, Void,String> {

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

                        //Skal ha et spesifikt byyg, så henter ut det ene bygget i arrayen
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
                    return "";
                }
            }
            return retur;
        }

        @Override
        protected void onPostExecute(String s) {
            //Viser feilmelding hvis webserver er nede
            if(s.equals("")) {
                adresseBox.setText(getString(R.string.serverFeil));
            }
            else {
                String[] splittet = s.split(";");
                //Splitter opp jsonstrengen og viser data til bruker
                adresseBox.setText(splittet[2]);
                koordinater = splittet[3];
                String splittetKoordinater[] = koordinater.split(",");
                String formatertKoordinater = splittetKoordinater[0].substring(0,5) +"," + splittetKoordinater[1].substring(0,5);
                koordinaterBox.setText(formatertKoordinater);
                etasjeBox.setText(splittet[4]);
                antEtasjer = splittet[4];
                beskrivelseBox.setText(splittet[1]);
                beskrivelse = splittet[1];
                Id = splittet[0];
            }
        }
    }
}

