package com.example.s328084s333761mappe3;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class VisInfoDialogFragment extends DialogFragment {

    TextView byggInfo;
    TextView romInfo;
    TextView reservasjonInfo;
    Button leggTilRomKnapp;
    Button leggTilReservasjonKnapp;
    Button avsluttKnapp;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.vis_info_dialog_layout, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        byggInfo = (TextView) view.findViewById(R.id.byggInfo);
        romInfo = (TextView) view.findViewById(R.id.romInfo);
        reservasjonInfo = (TextView) view.findViewById(R.id.reservasjonInfo);
        avsluttKnapp = (Button) view.findViewById(R.id.avsluttKnapp);
        leggTilRomKnapp = (Button) view.findViewById(R.id.leggTilRomKnapp);
        leggTilReservasjonKnapp = (Button) view.findViewById(R.id.leggTilReservasjonKnapp);

        avsluttKnapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        leggTilRomKnapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendJSON task = new SendJSON("{Beskrivelse: huset, Adresse: adressen, Koordinater: koordinatene, AntEtasjer: antallet}");
                task.execute(new
                        String[]{"http://student.cs.hioa.no/~s333761//jsoninBygg.php/?Beskrivelse=lasse&Adresse=adressen&Koordinater=koordinatene&AntEtasjer=antallet"});


                GetByggJSON task2 = new GetByggJSON();
                task2.execute(new
                        String[]{"http://student.cs.hioa.no/~s333761/jsonoutBygg.php"});
                Context applicationContext = MapsActivity.getContextOfApplication();
                prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext);
                String bygg = prefs.getString(getString(R.string.byggUt),"Feilet, ikke noe i preferanser");
                byggInfo.setText(bygg);
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
}
