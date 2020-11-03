package com.example.s328084s333761mappe3;

public class Bygg {
    public int Id;
    public String Beskrivelse;
    public String Adresse;
    public String Koordinater; //Splitte opp?
    public String AntEtasjer;

    public Bygg(int Id, String Beskrivelse, String Adresse, String Koordinater, String AntEtasjer) {
        this.Adresse = Adresse;
        this.Id = Id;
        this.Beskrivelse = Beskrivelse;
        this.AntEtasjer = AntEtasjer;
        this.Koordinater = Koordinater;
    }

    public Bygg() {

    }
}

