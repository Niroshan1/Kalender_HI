/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utilities;

import java.io.Serializable;

/**
 * Klasse mit Informationen fuer Anfrage
 */
public class Anfrage extends Meldung implements Serializable{
    
    // Variablen Deklaration und Initialisierung
    private final Termin termin;
    private final String absender;
    
    /**
     *  
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
     * gibt den Termin zurueck
     * 
     * @return termin
     */
    public Termin getTermin(){
        return termin;
    }
    
    /**
     * gibt den Absender zurueck 
     * 
     * @return absender
     */
    public String getAbsender(){
        return absender;
    }
    
    /**
     * gibt den Text fuer die zurueck
     * 
     * @return string
     */
    @Override
    public String getText(){
        return absender + " lädt sie zu dem Termin " + termin.getTitel() + " am " + termin.getDatum() + " ein.";
    }
}
