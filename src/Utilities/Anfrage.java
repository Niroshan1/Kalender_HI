/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utilities;

import java.io.Serializable;

public class Anfrage extends Meldung implements Serializable{
    
    // Variablen Deklaration und Initialisierung
    private final Termin termin;
    private final String absender;
    
    /**
     * Konstruktur
     * @param text
     * @param termin
     * @param absender
     * @param meldungsID 
     */
    public Anfrage(String text, Termin termin, String absender, int meldungsID){
        super(text, meldungsID);
        this.termin = termin;
        this.absender = absender;
    }
    
    /**
     * Mehtode, die einen Termin zurueckgibt
     * @return 
     */
    public Termin getTermin(){
        return termin;
    }
    
    /**
     * Methode, die den Absender zurueckgibt
     * @return 
     */
    public String getAbsender(){
        return absender;
    }
    
    /**
     * Methode, die eine Einladung für den Client zurueckgibt
     * @return 
     */
    @Override
    public String getText(){
        return absender + " lädt sie zu dem Termin " + termin.getTitel() + " am " + termin.getDatum() + " ein.";
    }
}
