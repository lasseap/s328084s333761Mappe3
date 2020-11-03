package com.example.s328084s333761mappe3;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.URL;

public class GetByggJSON extends AsyncTask<String, Void,String> {
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

                    JSONObject jsonobject = mat.getJSONObject(0);
                    String id = jsonobject.getString("Id");
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
                Log.d("Tag","get bygg feilet");
                return "";
            }
        }
        return retur;
    }
}