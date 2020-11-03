package com.example.s328084s333761mappe3;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetRomJSON extends AsyncTask<String, Void,String> {
    JSONObject jsonObject;

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
                        String id = jsonobject.getString("Id");
                        String bygg_id = jsonobject.getString("Bygg_Id");
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
}