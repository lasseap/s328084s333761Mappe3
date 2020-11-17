package com.example.s328084s333761mappe3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ReservasjonAdapter extends ArrayAdapter<RomReservasjon> {
    public ReservasjonAdapter(Context context, ArrayList<RomReservasjon> reservasjoner) {
        super(context, 0, reservasjoner);
    }

    @Override
    public View getView(int posisjon, View convertView, ViewGroup parent) {
        //Får tak i møte-objektet for denne posisjonen
        RomReservasjon romReservasjon = getItem(posisjon);
        //Sjekker om en eksisterende view blir brukt, hvis ikke inflater vi viewet
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.romreservasjon_view, parent, false);
        }

        //TextView romNr = (TextView) convertView.findViewById(R.id.romNrView);
        TextView dato = (TextView) convertView.findViewById(R.id.datoView);
        TextView tidFra = (TextView) convertView.findViewById(R.id.tidFraView);
        TextView tidTil = (TextView) convertView.findViewById(R.id.tidTilView);

        //romNr.setText(romReservasjon.getRomNr());
        dato.setText(romReservasjon.dato);
        tidFra.setText(romReservasjon.fra);
        tidTil.setText(romReservasjon.til);

        return convertView;
    }
}
