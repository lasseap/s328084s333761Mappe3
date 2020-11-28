package com.example.s328084s333761mappe3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class RomAdapter extends ArrayAdapter<Rom> {
    public RomAdapter(Context context, ArrayList<Rom> rom) {
        super(context, 0, rom);
    }

    @Override
    public View getView(int posisjon, View convertView, ViewGroup parent) {
        //Får tak i rom-objektet for denne posisjonen
        Rom rom = getItem(posisjon);
        //Sjekker om en eksisterende view blir brukt, hvis ikke inflater vi viewet
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.rom_view, parent, false);
        }

        TextView romNr = (TextView) convertView.findViewById(R.id.romNrView);
        TextView etasjeNr = (TextView) convertView.findViewById(R.id.etasjeNrView);
        TextView kapasitet = (TextView) convertView.findViewById(R.id.kapasitetView);

        romNr.setText(rom.getRomNr());
        etasjeNr.setText(rom.getEtasjeNr());
        kapasitet.setText(rom.getKapasitet());

        return convertView;
    }
}
